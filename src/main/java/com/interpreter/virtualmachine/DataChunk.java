package com.interpreter.virtualmachine;

import java.util.ArrayList;
import java.util.List;

public class DataChunk {
    static public class Package {
        Value value;

    }


    private final List<Package> dataArray = new ArrayList<>();

    public void setData(int position, Value value) {
        dataArray.get(position).value = value;
    }

    public void setData(int position, Package pack) {
        dataArray.set(position, pack);
    }

    public Value getData(int position) {
        return dataArray.get(position).value;
    }
}
