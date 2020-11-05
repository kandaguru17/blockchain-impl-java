package io.blockchain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private Integer blockNumber;
    private BigInteger nonce;
    private Long timeStamp;
    private List<Transaction> data = new ArrayList<>();
    private String blockHash;
    private String previousHash;


    public Block(List<Transaction> data, Integer blockNumber, String previousHash, BigInteger nonce) {
        this.data = data;
        this.blockNumber = blockNumber;
        this.nonce = nonce;
        this.previousHash = previousHash;
    }

    public Block(List<Transaction> data) {
        this.data = data;
    }


    public Block(Block block) {
        this.previousHash = block.getPreviousHash();
        this.nonce = block.getNonce();
        this.data = block.getData();
        this.blockNumber = block.getBlockNumber();
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Transaction> getData() {
        return data;
    }

    public void setData(List<Transaction> data) {
        this.data = data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

}
