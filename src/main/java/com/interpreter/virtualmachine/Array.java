package com.interpreter.virtualmachine;

import java.util.ArrayList;

public class Array {
    private ArrayList<Value> array;

    public Array(int size) {
        array = new ArrayList<>(size);
    }

    public Value getElement(int index) {
        return array.get(index);
    }

    public void setElement(int index, Value value) {
        array.set(index, value);
    }

}
