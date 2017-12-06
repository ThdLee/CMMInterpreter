package com.interpreter.analysis.node;

import com.interpreter.analysis.NonTerminalSymbol;
import com.interpreter.analysis.TerminalSymbol;
import com.interpreter.analysis.Token;

import java.util.ArrayList;

public abstract class Node {

    private int line;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

}
