package com.google.engedu.ghost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.google.engedu.ghost.GhostDictionary.MIN_WORD_LENGTH;


public class TrieNode {
//    private HashMap<String, TrieNode> children;
    private HashMap<Character, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        if (s == null || s.length() == 0) {
            return;
        }

        TrieNode cur = this;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!cur.children.containsKey(ch)) {
                cur.children.put(ch, new TrieNode());
            }

            cur = cur.children.get(ch);
        }

        cur.isWord = true;
    }

    public boolean isWord(String s) {
        if (s == null || s.length() < MIN_WORD_LENGTH) {
            return false;
        }

        TrieNode cur = this;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!cur.children.containsKey(ch)) {
                return false;
            }

            cur = cur.children.get(ch);
        }

        return cur.isWord;
    }

    public String getAnyWordStartingWith(String s) {
        String result = "";
        if (s == null || s.length() == 0) {
            return getRandomWord(result, this);
        }

        TrieNode cur = this;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!cur.children.containsKey(ch)) {
                return null;
            }

            cur = cur.children.get(ch);
        }

        if (cur.isWord == true) {
            return null;
        }

        while (cur.isWord == false) {
            Iterator it = cur.children.entrySet().iterator();
            char next = '\0';
            while (it.hasNext()) {
                Map.Entry<Character, TrieNode> entry = (Map.Entry)it.next();
                next = entry.getKey();
                s += Character.toString(next);
                break;
            }

            cur = cur.children.get(next);
        }

        return s;
    }

    public String getGoodWordStartingWith(String s) {
        String result = "";
        if (s == null || s.length() == 0) {
            return getRandomWord(result, this);
        }

        List<String> rangeWords = getAllWordsStartsWith(s);
        List<String> evenCandidates = new ArrayList<>();
        List<String> oddCandidates = new ArrayList<>();
        for (String next : rangeWords) {
            if (next.length() % 2 == 0) {
                evenCandidates.add(next);
            } else {
                oddCandidates.add(next);
            }
        }

        // assume that user turn maps to the potential valid words with even length
        Random random = new Random();
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

    public String getRandomWord(String s, TrieNode cur) {
        if (cur.isWord == true) {
            return s;
        }

        int size = cur.children.size();
        Random random = new Random();
        int index = random.nextInt(size);
        int i = 0;
        Iterator it = cur.children.entrySet().iterator();
        while (i != index && it.hasNext()) {
            i++;
            continue;
        }

        char ch = '\0';
        if (i == index && it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            ch = (Character) entry.getKey();
            s += Character.toString(ch);
        }

        cur = cur.children.get(ch);
        return getRandomWord(s, cur);
    }

    public List<String> getAllWordsStartsWith(String s) {
        List<String> rangeWords = new ArrayList<>();
        TrieNode cur = isStartWithPrefix(s);
        if (s == null || s.length() == 0 || cur == null) {
            return rangeWords;
        }

        StringBuilder sb = new StringBuilder(s);
        helper(rangeWords, sb, cur);
        return rangeWords;
    }

    public void helper(List<String> rangeWords, StringBuilder sb, TrieNode cur) {
        if (cur.isWord == true) {
            rangeWords.add(sb.toString());
        }

        Iterator it = cur.children.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Character key = (Character)entry.getKey();
            TrieNode value = (TrieNode)entry.getValue();
            sb.append(key);
            helper(rangeWords, sb, value);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    public TrieNode isStartWithPrefix(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        TrieNode cur = this;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!cur.children.containsKey(ch)) {
                return null;
            }

            cur = cur.children.get(ch);
        }

        return cur;
    }
}
