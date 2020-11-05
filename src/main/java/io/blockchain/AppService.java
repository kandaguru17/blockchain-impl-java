package io.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class AppService {

    public static final String DIFFICULTY_LVL = "00";
    public static final String GENESIS_HASH = "00000000000000000000000000000000000000000000";
    private BlockChain blockChain = new BlockChain();
    private final List<Transaction> transactionPool = Collections.synchronizedList(new ArrayList<>());


    /**
     * create the blockchain with the genesis block as the APP starts
     */
    @PostConstruct
    public void createGenesisBlock() {
        Block genesisBlock = new Block(Collections.emptyList(), 0, GENESIS_HASH, BigInteger.valueOf(-1L));
        genesisBlock.setBlockHash(GENESIS_HASH);
        genesisBlock.setTimeStamp(Instant.now().getEpochSecond());
        blockChain = new BlockChain(genesisBlock);
    }


    /**
     * adding the transactions created into the transaction pool ,
     * enabling the miners to pick and add it in their block
     * to be added to the blockchain
     *
     * @param data
     * @return Transaction
     */
    public Transaction addTxnToPool(Transaction data) {
        Transaction newTransaction = new Transaction(data);
        transactionPool.add(newTransaction);
        return newTransaction;
    }


    /**
     * Creates a new block to be added on the blockchain
     *
     * @return Block
     */
    public Block createBlock() {
        List<Transaction> transactionsToMine = new ArrayList<>(transactionPool);
        transactionPool.clear();
        Block newBlock = new Block(transactionsToMine);
        if (blockChain.getChain().isEmpty())
            newBlock.setPreviousHash(GENESIS_HASH);
        return newBlock;
    }


    /**
     * Retrieve the last block of the chain
     *
     * @return Block
     */
    public Block getPreviousBlock() {
        if (blockChain != null && !blockChain.getChain().isEmpty()) {
            int chainSize = blockChain.getChain().size();
            return blockChain.getChain().get(chainSize - 1);
        }
        return null;
    }

    /**
     * This method conducts the actual mining process,
     * Greater the processing power quicker the execution
     *
     * @return BigInteger
     * @throws JsonProcessingException
     * @throws NoSuchAlgorithmException
     */
    public BigInteger proofOfWork() throws JsonProcessingException, NoSuchAlgorithmException {
        BigInteger nonce = BigInteger.ZERO;
        String blockHash;
        Block minedBlock;

        if (transactionPool.isEmpty())
            throw new IllegalStateException("no transactions to mine");

        //get the last mined block
        Block previousBlockInChain = getPreviousBlock();
        int prevBlockNo = previousBlockInChain == null ? 0 : previousBlockInChain.getBlockNumber();
        String prevBlockHash = previousBlockInChain == null ? GENESIS_HASH : previousBlockInChain.getBlockHash();

        // get the block to be mined from the transaction pool
        Block currentBlockToMine = createBlock();

        do {
            minedBlock = new Block(currentBlockToMine.getData(),
                    prevBlockNo + 1,
                    prevBlockHash,
                    nonce);
            blockHash = createHash(minedBlock);
            nonce = nonce.add(BigInteger.ONE);
        } while (!blockHash.startsWith(DIFFICULTY_LVL));

        minedBlock.setTimeStamp(Instant.now().getEpochSecond());
        minedBlock.setBlockHash(blockHash);
        minedBlock.setPreviousHash(prevBlockHash);
        blockChain.getChain().add(minedBlock);
        return nonce;
    }


    /**
     * Ensures if the chain is clean and  has no tampering
     *
     * @return Boolean
     * @throws JsonProcessingException
     * @throws NoSuchAlgorithmException
     */
    public Boolean checkIfChainValid() throws JsonProcessingException, NoSuchAlgorithmException {
        for (int i = 1; i < blockChain.getChain().size(); i++) {

            Block current = blockChain.getChain().get(i);
            String currentBlockHash = current.getBlockHash();

            // create a clone of the retrieved block
            Block clonedCurrent = new Block(current);

            //check hash
            String newHash = createHash(clonedCurrent);
            if (!newHash.equals(currentBlockHash))
                return false;

            //check if hash has correct difficulty level
            if (!(current.getBlockHash().startsWith(DIFFICULTY_LVL) && newHash.startsWith(DIFFICULTY_LVL)))
                return false;

            // if it is the last block,ignore comparing with the next block in the chain
            if (i < blockChain.getChain().size() - 1) {
                Block next = blockChain.getChain().get(i + 1);

                //check if the associations of the blocks in the chain
                if (!next.getPreviousHash().equals(currentBlockHash))
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns the resultant blockchain
     *
     * @return BlockChain
     */
    public BlockChain getBlockChain() {
        return blockChain;
    }

    /**
     * Hash algorithm used to generate the block hash
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws JsonProcessingException
     */
    private String createHash(Block data) throws NoSuchAlgorithmException, JsonProcessingException {
        ObjectMapper ow = new ObjectMapper();
        String json = ow.writeValueAsString(data);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
