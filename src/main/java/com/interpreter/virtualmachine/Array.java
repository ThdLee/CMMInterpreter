package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.PrimaryType;
import com.interpreter.virtualmachine.DataChunk.Package;

import java.util.ArrayList;

public class Array {
    private ArrayList<Package> array;

    public Array(int size, PrimaryType type) {
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Package pack = new Package();
            pack.value = new Value(type);
            array.set(i, pack);
        }
    }

    public Package getElement(int index) {
        return array.get(index);
    }


}
