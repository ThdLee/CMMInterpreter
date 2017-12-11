package com.interpreter.intermediatecode;

import java.util.LinkedList;

public class VariablePool {

    private int nextIndex = 0;
    private LinkedList<Integer> indexPool = new LinkedList<>();

    int createIndex() {
        int index;
        if(indexPool.isEmpty()) {
            index = nextIndex;
            nextIndex++;
        } else {
            index = indexPool.removeFirst();
        }
        return index;
    }

    void freeIndex(int index) {
        if (index != -1) {
            indexPool.addLast(index);
        }
    }
}
