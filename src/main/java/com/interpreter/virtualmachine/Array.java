package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.PrimaryType;

import java.util.ArrayList;

public class Array {
    private ArrayList<Value> array;
    private PrimaryType elemType;

    public Array(int size, PrimaryType type) {
        elemType = type;
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(new Value(type));
        }
    }

    public Value getElement(int index) {
        if (index < 0) {
            throw new RuntimeException("index cannot be negative");
        } else if (index >= array.size()) {
            throw new RuntimeException("index out of bounds, index:" + index + " size:" + array.size());
        }
        return array.get(index);
    }

    public void setElement(int index, Value value) {
        if (index < 0) {
            throw new RuntimeException("index cannot be negative");
        } else if (index >= array.size()) {
            throw new RuntimeException("index out of bounds, index:" + index + " size:" + array.size());
        }
        Value oldValue = array.get(index);
        value = Value.convertNumberToHeightTypeLevel(value, oldValue);
        if (value.type != elemType) {
            throw new RuntimeException(value + " cannot be converted to " + elemType);
        }
        array.set(index, value);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append('[');
        for (Value v : array) {
            str.append("'").append(v).append("',");
        }
        str.deleteCharAt(str.length()-1).append(']');
        return str.toString();
    }

}
