package com.wordgames;

import org.junit.jupiter.api.Test;

public class LadderTest {
    @Test
    void generateLadder() {
        final long startTime = System.nanoTime();

        WordLadder.generateLadder(0);

        System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
    }

    @Test
    void findPairs() {
        final long startTime = System.nanoTime();

        WordLadder.getPairs(0, false);

        System.out.println("Execution time: " + (System.nanoTime() - startTime) / 1_000_000 + " ms");
    }
}
