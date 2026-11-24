package com.wordgames;

import org.junit.jupiter.api.Test;

public class LadderTest {
    @Test
    void generateLadder() {
        for (int i = 0; i < 3; i++) {
            final long startTime = System.nanoTime();

            WordLadder.generateLadder(i);

            System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
        }
        
    }
    
    @Test
    void findPairs() {
        for (int i = 0; i < 3; i++) {
            final long startTime = System.nanoTime();

            WordLadder.getPairs(i);

            System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
        }
    }
}
