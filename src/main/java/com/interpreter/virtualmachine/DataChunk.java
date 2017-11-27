package com.interpreter.virtualmachine;

import java.util.ArrayList;
import java.util.List;

public class DataChunk {
    private final List<Value> dataArray = new ArrayList<>();

    public void setData(int position, Value value) {
        dataArray.set(position, value);
    }

    public Value getData(int position) {
        return dataArray.get(position);
    }
}
