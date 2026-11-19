package com.wordgames;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Jumble {
    public static String[] getWordList(int difficulty) {
        String filePath = "src\\main\\java\\com\\wordgames\\WordBanks\\" + (difficulty == 0 ? "easy" : (difficulty == 1 ? "medium" : "hard")) + ".txt"; // path to text file

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String> allLines = br.readAllLines();

            for (String line : allLines) {
                if (line == null) continue;
                if (line.length() > difficulty + 3 || line.matches(".*[^a-zA-Z].*")) continue;

                lines.add(line.toLowerCase());
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[0]);
    }

    public static String[] generateJumble(int difficulty) {
        String filePath = "src\\main\\java\\com\\wordgames\\JumbleClues\\" + (difficulty == 0 ? "easy" : (difficulty == 1 ? "medium" : "hard")) + ".txt"; // path to text file
    
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String> allLines = br.readAllLines();

            for (String line : allLines) {
                if (line == null) continue;

                String[] parts = line.split(":");
                if (parts.length != 2) continue;
                
                String answer = parts[1];
                if (answer.length() > difficulty + 3 || answer.matches(".*[^a-zA-Z].*")) continue;

                lines.add(parts[0] + ":" + answer.toLowerCase());
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[0]);
    }

    public static String randomWord(int difficulty) {
        String[] wordList = getWordList(difficulty);

        Random rand = new Random();
        return wordList[rand.nextInt(wordList.length)];
    }

    public static String scramble(String word) {
        List<Character> letters = new ArrayList<>();

        for (char c : word.toCharArray())
            letters.add(c);
        
        Collections.shuffle(letters);
        StringBuilder scrambledWord = new StringBuilder();

        for (char c : letters)
            scrambledWord.append(c);
        
        return scrambledWord.toString();
    }

    public static boolean areAnagrams(String str1, String str2) {
        if (str1.length() != str2.length()) return false;

        // Convert to char arrays and sort
        char[] arr1 = str1.toCharArray();
        char[] arr2 = str2.toCharArray();
        Arrays.sort(arr1);
        Arrays.sort(arr2);

        // Compare sorted arrays
        return Arrays.equals(arr1, arr2);
    }
}
