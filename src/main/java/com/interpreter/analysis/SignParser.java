package com.interpreter.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class SignParser {
    private final static HashSet<String> signSet = new HashSet<String>() {{
        add("+"); add("-"); add("*"); add("/"); add("%");
        add(">"); add("<"); add(">="); add("<="); add("=="); add("!=");
        add("+="); add("-="); add("*="); add("/="); add("%=");
        add("&&"); add("||"); add("!"); add("&&="); add("||=");
//        add("<<"); add(">>"); add("<<="); add(">>=");
//        add("&"); add("|"); add("^"); add("&="); add("|="); add("^=");
//        add("?"); add(":");
        add("("); add(")"); add("{"); add("}"); add("["); add("]");
    }};
    private final static HashSet<Character> signCharSet = new HashSet<>();

    static {
        for (String s : signSet) {
            for (char c : s.toCharArray()) {
                signCharSet.add(c);
                boolean a = false;
            }
        }
    }

    static boolean inCharSet(char c) {
        return signCharSet.contains(c);
    }

    static List<String> parse(String str) throws LexerException {
        LinkedList<String> signs = new LinkedList<>();
        int endIndex = str.length();
        int startIndex = 0;
        while (startIndex < endIndex) {
            String s = str.substring(startIndex, endIndex);
            if (signSet.contains(s)) {
                signs.add(s);
                startIndex = endIndex;
                endIndex = str.length();
            } else {
                endIndex--;
            }
        }
        return signs;
    }

}
