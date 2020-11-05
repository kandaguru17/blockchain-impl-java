package io.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/blockchain")
public class AppController {


    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }


    @GetMapping("/")
    public ResponseEntity<Object> getChain() {
        BlockChain blockChain = appService.getBlockChain();
        return ResponseEntity.ok().body(blockChain);
    }

    @PostMapping("/transactions")
    public ResponseEntity<Object> createTxn(@RequestBody Object txnData) {
        Transaction transaction = appService.addTxnToPool(new Transaction(txnData));
        return ResponseEntity.ok().body(transaction);
    }

    @GetMapping("/mine")
    public ResponseEntity<Object> mineTheBlock() throws NoSuchAlgorithmException, JsonProcessingException {
        BigInteger nonce = appService.proofOfWork();
        return ResponseEntity.ok().body(nonce);
    }


    @GetMapping("/verifyChain")
    public ResponseEntity<Object> verifyChain() throws NoSuchAlgorithmException, JsonProcessingException {
        Boolean isValid = appService.checkIfChainValid();
        return ResponseEntity.ok().body(isValid);
    }

}
