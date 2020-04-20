package com.project.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-17 01:16
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // use REPLACEMENT to replace sensitive words
    private static final String REPLACEMENT = "***";
    // Root node
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while ((keyword = reader.readLine()) != null ) {
                // add to trie tree
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("Cannot get sensitive word" + e.getMessage());
        }
    }

    /**
     * filter sensitive words
     * @param text text before filter
     * @return text after filter
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // pointer 1
        TrieNode tempNode = rootNode;
        // pointer 2
        int begin = 0;
        // pointer 3
        int position = 0;
        // result
        StringBuilder stringBuilder = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    stringBuilder.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                stringBuilder.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // sensitive word identified
                stringBuilder.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            } else {
                position++;
            }
        }
        stringBuilder.append(text.substring(begin));

        return stringBuilder.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * add keyword to trie tree
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // initialize subNode
                subNode = new TrieNode();
                tempNode.addSubNodes(c, subNode);
            }
            // point to subNode
            tempNode = subNode;
            // set keyword end
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    // Node of Trie Tree
    private class TrieNode {
        // if sensitive
        private boolean isKeywordEnd = false;
        // child nodes
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNodes(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
