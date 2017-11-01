package com.interpreter.analysis.node;

import com.interpreter.analysis.NonTerminalSymbol;

import java.util.ArrayList;

// Non-Terminal Node
public class NNode implements Node {

    private ArrayList<Node> children = new ArrayList<>();

    private NonTerminalSymbol symbol = null;

    public NNode(NonTerminalSymbol symbol) {
        this.symbol = symbol;
    }

    public NNode() {}

    public void addNode(Node node) {
        children.add(node);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public NonTerminalSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(NonTerminalSymbol symbol) {
        this.symbol = symbol;
    }
}
