package com.interpreter.virtualmachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataChunk {
    private final List<Value> dataArray = new ArrayList<>();

    public void setData(int position, Value value) {
        handlePosition(position);
        dataArray.set(position, value);
    }

    public Value getData(int position) {
        return dataArray.get(position);
    }

    private void handlePosition(int position) {
        if (position >= dataArray.size()) {
            int needCount = position - dataArray.size() + 1;
            for (int i=0; i<needCount; ++i) {
                dataArray.add(null);
            }
        }
    }
}
