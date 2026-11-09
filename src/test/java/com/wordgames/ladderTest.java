package com.wordgames;

import org.junit.jupiter.api.Test;

public class ladderTest {
    @Test
    void generateLadder() {
        final long startTime = System.nanoTime();

        String[] easyArray = wordLadderController.getWordLadder(0);

        System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1_000_000 + " ms");

        //for(String line : easyArray) {
        //    System.out.println(line);
        //}
    }
}
