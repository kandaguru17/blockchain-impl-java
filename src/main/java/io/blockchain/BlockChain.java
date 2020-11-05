package io.blockchain;

import java.util.ArrayList;
import java.util.List;

public class BlockChain {

    private List<Block> chain = new ArrayList<>();

    public BlockChain() {

    }

    public BlockChain(Block block) {
        this.chain.add(block);
    }

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        this.chain = chain;
    }
}
