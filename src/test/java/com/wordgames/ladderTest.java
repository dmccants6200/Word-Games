package com.wordgames;

import org.junit.jupiter.api.Test;

public class ladderTest {
    @Test
    void generateLadder() {
        String[] easyArray = wordLadderController.getWordLadder(0);

        for(String line : easyArray) {
            System.out.println(line);
        }
    }
}
