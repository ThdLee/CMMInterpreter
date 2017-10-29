package com.interpreter.analysis;

import java.util.ArrayList;

public class Node {
    ArrayList<Node> children = new ArrayList<>();

    private TerminalSymbol tSymbol = null;
    private NonTerminalSymbol nSymbol = null;

    String value;
    public Node() {}

    public Node(NonTerminalSymbol symbol) {
        this.nSymbol = symbol;
    }

    public Node(TerminalSymbol symbol) {
        this.tSymbol = symbol;
    }

    public Node(TerminalSymbol symbol, String value) {
        this.tSymbol = symbol;
        this.value = value;
    }

    public void addNode(Node node) {
        children.add(node);
    }

    public void parseToken(Token token) {

    }

    public boolean isTerminal() {
        return tSymbol != null;
    }

    public TerminalSymbol getTSymbol() {
        return tSymbol;
    }

    public void setTSymbol(TerminalSymbol tSymbol) {
        this.tSymbol = tSymbol;
    }

    public NonTerminalSymbol getNSymbol() {
        return nSymbol;
    }

    public void setNSymbol(NonTerminalSymbol nSymbol) {
        this.nSymbol = nSymbol;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
