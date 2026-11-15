package com.wordgames;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordLadder {
    private static String[] finalArray = new String[0];

    public static String[] getWordList(int difficulty) {
        String filePath = null; // path to text file

        filePath = "src\\main\\java\\com\\wordgames\\WordBanks\\" + (difficulty == 0 ? "easy" : (difficulty == 1 ? "medium" : "hard")) + ".txt";

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

    public static String[] getPairs(int difficulty, boolean noPositions) {
        String[] words = getWordList(difficulty);

        Map<String, List<String>> firstTwo = new HashMap<>();
        Map<String, List<String>> firstLast = new HashMap<>();
        Map<String, List<String>> lastTwo = new HashMap<>();

        for (String word : words) {
            firstTwo.computeIfAbsent(word.substring(0, 2), k -> new ArrayList<>()).add(word);
            firstLast.computeIfAbsent("" + word.charAt(0) + word.charAt(2), k -> new ArrayList<>()).add(word);
            lastTwo.computeIfAbsent(word.substring(1, 3), k -> new ArrayList<>()).add(word);
        }

        List<String> pairs = new ArrayList<>();

        for (String word : words) {
            String[] keys = {word.substring(0, 2), "" + word.charAt(0) + word.charAt(2), word.substring(1, 3)};

            for (int position = 0; position < 3; position++) {
                Map<String, List<String>> map = position == 0 ? firstTwo : position == 1 ? firstLast : lastTwo;
                List<String> matches = map.get(keys[position]);

                if (matches == null) continue;

                for (String match : matches) {
                    if (word.equals(match)) continue;

                    pairs.add(word + ":" + match + (noPositions ? "" : ":" + (2 - position)));
                }
            }
        }

        return pairs.toArray(new String[0]);
    }

    public static String[] generateLadder(int difficulty) {
        String[] pairs = getPairs(difficulty, false); // Find all one-letter-away pairs

        // Find all valid three word chains

        List<String> result = new ArrayList<>();
        Map<String, List<String[]>> pairMap1 = new HashMap<>(); // Index pairs by their starting word.

        for (String pair : pairs) {
            String[] p = pair.split(":");

            pairMap1.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p);
        }

        for (String pair : pairs) {
            String[] p = pair.split(":");

            String first = p[0];
            String middle = p[1];
            String position = p[2];

            List<String[]> matches = pairMap1.get(middle);

            if (matches == null) continue;

            for (String[] next : matches) {
                if (position.equals(next[2])) continue;

                result.add(first + ":" + middle + ":" + next[1] + ":" + position + ":" + next[2]);
            }
        }

        String[] threePairs = result.toArray(new String[0]);
                    
        // Find all possible four word chains

        List<String> fourList = new ArrayList<>();
        Map<String, List<String[]>> pairMap2 = new HashMap<>(); // Index pairs by their starting word.
        Set<String> existingPairs = new HashSet<>(); // Store existing two-pair combinations.

        for (String pair : pairs) {
            String[] p = pair.split(":");

            pairMap2.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p);
            existingPairs.add(p[0] + ":" + p[1]);
        }

        Set<String> existingThreePairs = new HashSet<>(); // Store existing three-pair combinations.
        List<String[]> parsedThreePairs = new ArrayList<>(); // Parse threePairs once.

        for (String three : threePairs) {
            String[] p = three.split(":");

            parsedThreePairs.add(p);
            existingThreePairs.add(p[0] + ":" + p[2]);
        }

        // Generate four-pair combinations.
        for (String[] three : parsedThreePairs) {
            String firstWord = three[0];
            String secondWord = three[1];
            String thirdWord = three[2];
            List<String[]> matches = pairMap2.get(thirdWord);

            if (matches == null) continue;

            for (String[] pair : matches) {
                String endWord = pair[1];

                if (secondWord.equals(endWord)) continue; // Original conditions.
                if (three[4].equals(pair[2])) continue;
                if (existingPairs.contains(firstWord + ":" + endWord)) continue; // Check whether the first and fourth words already have a direct pair.
                if (existingThreePairs.contains(firstWord + ":" + endWord)) continue; // Check whether the first and fourth words already have a three-pair connection.

                fourList.add(firstWord + ":" + secondWord + ":" + thirdWord + ":" + endWord + ":" + three[3] + ":" + three[4] + ":" + pair[2]);
            }
        }

        finalArray = fourList.toArray(new String[0]);
        //System.out.println("Finished array");

        return finalArray;
    }
}