package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.PrimaryType;

import java.util.ArrayList;

public class Array {
    private ArrayList<Value> array;

    public Array(int size, PrimaryType type) {
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(new Value(type));
        }
    }

    public Value getElement(int index) {
        return array.get(index);
    }

    public void setElement(int index, Value value) {
        array.set(index, value);
    }

}
