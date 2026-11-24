package com.wordgames;

import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadder {
    public static String[] getWordList(int difficulty) {
        //String filePath = "src\\main\\java\\com\\wordgames\\WordBanks\\" + (difficulty == 0 ? "easy" : (difficulty == 1 ? "medium" : "hard")) + ".txt"; // path to text file
        Path filePath = Paths.get(System.getProperty("user.dir"), "WordBanks\\" + (difficulty == 0 ? "easy" : (difficulty == 1 ? "medium" : "hard")) + ".txt");
        
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            List<String> allLines = br.readAllLines();

            for (String line : allLines) {
                if (line == null) continue;
                if (line.length() != difficulty + 3 || line.matches(".*[^a-zA-Z].*")) continue;

                lines.add(line.toLowerCase());
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines.toArray(new String[0]);
    }

    private static boolean oneLetterOff(String s1, String s2) {
        boolean oneOff = false;
        for (int i = 0; i < s1.length(); i++)
            if (s1.charAt(i) != s2.charAt(i)) {
                if (oneOff) return false; // More than one difference
                
                oneOff = true;
            }
        
        return oneOff; // Must be exactly one difference
    }

    public static String[] getPairs(int difficulty) {
        String[] words = getWordList(difficulty);
        List<String> pairs = new ArrayList<>();

        for (String word1 : words) 
            for (String word2 : words) {
                if (word1.equals(word2)) continue;
                if (!oneLetterOff(word1, word2)) continue;

                pairs.add(word1 + ":" + word2);
            }
        
        return pairs.toArray(new String[0]); 
    }

    public static String[] generateLadder(int difficulty) {
        String[] words = WordLadder.getWordList(difficulty);

        Set<String> wordSet = new HashSet<>();

        // Determine the word length from the word list.
        int n = words[0].length();

        for (String word : words)
            wordSet.add(word);

        Map<String, List<Neighbor>> graph = buildGraph(wordSet, n);

        List<String> result = new ArrayList<>();
        Set<String> chainSet = new HashSet<>();
        List<String> chain = new ArrayList<>(n + 1);

        // Cache shorter-connection results for each starting word.
        Map<String, Set<String>> shorterConnections = new HashMap<>();

        for (String word : wordSet) {
            chain.clear();
            chainSet.clear();

            chain.add(word);
            chainSet.add(word);

            generateChains(
                word, -1, graph, chain, chainSet,
                result, shorterConnections, n
            );
        }

        return result.toArray(new String[0]);
    }

    private static class Neighbor {
        String word;
        int position;

        Neighbor(String word, int position) {
            this.word = word;
            this.position = position;
        }
    }

    private static Map<String, List<Neighbor>> buildGraph(Set<String> wordSet, int n) {
        Map<String, List<Neighbor>> graph = new HashMap<>();
        Map<String, List<String>> patterns = new HashMap<>();

        for (String word : wordSet) {
            graph.put(word, new ArrayList<>());

            char[] chars = word.toCharArray();

            for (int i = 0; i < n; i++) {
                char original = chars[i];
                chars[i] = '\0';

                String pattern = new String(chars);

                patterns.computeIfAbsent(pattern, k -> new ArrayList<>()).add(word);

                chars[i] = original;
            }
        }

        for (Map.Entry<String, List<String>> entry : patterns.entrySet()) {
            List<String> matches = entry.getValue();
            int position = entry.getKey().indexOf('\0');

            for (int i = 0; i < matches.size(); i++) {
                String word1 = matches.get(i);

                for (int j = i + 1; j < matches.size(); j++) {
                    String word2 = matches.get(j);

                    graph.get(word1).add(new Neighbor(word2, position));
                    graph.get(word2).add(new Neighbor(word1, position));
                }
            }
        }

        return graph;
    }

    private static void generateChains(
        String current,
        int previousPosition,
        Map<String, List<Neighbor>> graph,
        List<String> chain,
        Set<String> chainSet,
        List<String> result,
        Map<String, Set<String>> shorterConnections,
        int n
    ) {
        // A chain contains n + 1 words.
        if (chain.size() == n + 1) {
            String start = chain.get(0);
            String end = chain.get(n);

            Set<String> connected = shorterConnections.computeIfAbsent(start, k -> findShorterConnections(k, graph, n));

            if (!connected.contains(end))
                result.add(String.join(":", chain));

            return;
        }

        for (Neighbor neighbor : graph.get(current)) {
            String next = neighbor.word;
            int changedPosition = neighbor.position;

            if (chainSet.contains(next)) continue;

            // Do not change the same position consecutively.
            if (changedPosition == previousPosition) continue;

            chain.add(next);
            chainSet.add(next);

            generateChains(
                next,
                changedPosition,
                graph,
                chain,
                chainSet,
                result,
                shorterConnections,
                n
            );

            chain.remove(chain.size() - 1);
            chainSet.remove(next);
        }
    }

    private static Set<String> findShorterConnections(String start, Map<String, List<Neighbor>> graph, int n) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        Queue<Integer> distances = new ArrayDeque<>();

        queue.add(start);
        distances.add(0);
        visited.add(start);

        Set<String> connected = new HashSet<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int distance = distances.poll();

            // Only search paths shorter than n edges.
            if (distance == n - 1) continue;

            for (Neighbor neighbor : graph.get(current)) {
                String next = neighbor.word;

                connected.add(next);

                if (visited.add(next)) {
                    queue.add(next);
                    distances.add(distance + 1);
                }
            }
        }

        return connected;
    }
}