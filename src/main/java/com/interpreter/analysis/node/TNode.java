package com.interpreter.analysis.node;

import com.interpreter.analysis.TerminalSymbol;

// Terminal Node
public class TNode implements Node {

    private TerminalSymbol symbol = null;

    private String value;

    public TNode() {}

    public TNode(TerminalSymbol symbol) {
        this.symbol = symbol;
    }

    public TNode(TerminalSymbol symbol, String value) {
        this.symbol = symbol;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TerminalSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(TerminalSymbol tSymbol) {
        this.symbol = tSymbol;
    }
}
