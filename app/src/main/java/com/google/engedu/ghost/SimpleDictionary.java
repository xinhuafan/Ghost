package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            int index = random.nextInt(words.size());
            return words.get(index);
        }

        int start = 0;
        int end = words.size() - 1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            String temp = words.get(mid);
            if (temp.length() > prefix.length()) {
                temp = temp.substring(0, prefix.length());
            }

            int diff = temp.compareTo(prefix);
            if (diff == 0) {
                return words.get(mid);
            } else if (diff > 0) {
                end = mid;
            } else {
                start = mid;
            }
        }

        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            int index = random.nextInt(words.size());
            return words.get(index);
        }

        // Find the begin index of the range of the potential valid words
        int start = 0;
        int end = words.size() - 1;
        int beginIndex = -1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            String temp = words.get(mid);
            if (temp.length() > prefix.length()) {
                temp = temp.substring(0, prefix.length());
            }

            int diff = temp.compareTo(prefix);
            if (diff >= 0) {
                end = mid;
            } else {
                start = mid;
            }
        }

        String sEnd = words.get(end);
        if (sEnd.length() > prefix.length()) {
            sEnd = sEnd.substring(0, prefix.length());
        }

        if (sEnd.equals(prefix)) {
            beginIndex = end;
        }

        String sStart = words.get(start);
        if (sStart.length() > prefix.length()) {
            sStart = sStart.substring(0, prefix.length());
        }

        if (sStart.equals(prefix)) {
            beginIndex = start;
        }

        if (beginIndex == -1) {
            return null;
        }

        // Find the end index of the range of the potential valid words
        start = 0;
        end = words.size() - 1;
        int endIndex = -1;
        while (start + 1 < end) {
            int mid = start + (end - start) / 2;
            String temp = words.get(mid);
            if (temp.length() > prefix.length()) {
                temp = temp.substring(0, prefix.length());
            }

            int diff = temp.compareTo(prefix);
            if (diff > 0) {
                end = mid;
            } else {
                start = mid;
            }
        }

        sStart = words.get(start);
        if (sStart.length() > prefix.length()) {
            sStart = sStart.substring(0, prefix.length());
        }

        if (sStart.equals(prefix)) {
            endIndex = start;
        }

        sEnd = words.get(end);
        if (sEnd.length() > prefix.length()) {
            sEnd = sEnd.substring(0, prefix.length());
        }

        if (sEnd.equals(prefix)) {
            endIndex = end;
        }

        if (endIndex == -1) {
            return null;
        }

        // divide potential valid words into two groups based on length
        List<String> oddCandidates = new ArrayList<>();
        List<String> evenCandidates = new ArrayList<>();
        for (int i = beginIndex; i <= endIndex; i++) {
            String next = words.get(i);
            if (next.length() % 2 == 0) {
                evenCandidates.add(next);
            } else {
                oddCandidates.add(next);
            }
        }

        // assume that user turn maps to the potential valid words with even length
        boolean userTurn = random.nextBoolean();
        if (userTurn == true && evenCandidates.size() > 0) {
            int index = random.nextInt(evenCandidates.size());
            return evenCandidates.get(index);
        }

        if (userTurn == false && oddCandidates.size() > 0){
            int index = random.nextInt(oddCandidates.size());
            return oddCandidates.get(index);
        }

        return null;
    }
}
