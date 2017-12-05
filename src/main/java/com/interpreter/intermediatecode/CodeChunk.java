package com.interpreter.intermediatecode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeChunk implements Iterable<CodeChunk.Code> {

    private final List<Code> container = new ArrayList<>();

    public Code getCodeByLine(int line) {
        return container.get(line);
    }

    @Override
    public Iterator<Code> iterator() {
        return container.iterator();
    }

    int getCurrentPostion() {
        return container.size();
    }

    public int getSize() {
        return container.size();
    }
    public enum Command {
        Mov,
        Add, Sub, Mul, Div, Mod, Opposite,

        Not, And, Or,
        Gt, Gte, Lt, Lte, Equal, NotEqual,

        Jmp, JmpUnless,
        Write, Read,
        NewArray, Get, Set
    }

    public static class Code {
        int line;

        Command command;
        int num1;
        int num2;
        int num3;
        ImmediateNumber immediateNumber;

        Code() {}

        public Command getCommand() {
            return command;
        }

        public int getNum1() {
            return num1;
        }

        public int getNum2() {
            return num2;
        }

        public int getNum3() {
            return num3;
        }

        public ImmediateNumber getImmediateNumber() {
            return immediateNumber;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(line).append(" ");
            str.append(String.format("%10s", command)).append("   ");
            str.append(num1).append(", ").append(num2).append(", ").append(num3);
            if(immediateNumber != null) {
                if (immediateNumber.type == ImmediateType.String) {
                    str.append("  <<  \"").append(immediateNumber).append("\"");
                } else {
                    str.append("  <<  ").append(immediateNumber);
                }
            }
            return str.toString();
        }
    }

    public enum ImmediateType {
        Integer, Double, Bool, String,
    }

    public static class ImmediateNumber {
        ImmediateType type;
        int integerValue = 0;
        double doubleValue = 0.0;
        boolean boolValue = false;
        String stringValue = "";

        @Override
        public String toString() {
            if(type == ImmediateType.Bool) {
                return String.valueOf(boolValue);
            } else if(type == ImmediateType.Integer) {
                return String.valueOf(integerValue);
            } else if(type == ImmediateType.Double) {
                return String.valueOf(doubleValue);
            } else if(type == ImmediateType.String) {
                return String.valueOf(stringValue);
            } else {
                return "NULL";
            }
        }

        public ImmediateType getType() {
            return type;
        }

        public int getIntegerValue() {
            return integerValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public boolean getBooValue() {
            return boolValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    private void push(Command command, int num1, int num2, int num3, ImmediateNumber immediateNumber) {
        Code code = new Code();
        code.command = command;
        code.num1 = num1;
        code.num2 = num2;
        code.num3 = num3;
        code.immediateNumber = immediateNumber;
        code.line = container.size();
        container.add(code);
    }

    void push(Command command, int num1, int num2, int num3) {
        push(command, num1, num2, num3, null);
    }

    void push(Command command, int num1, int num2) {
        push(command, num1, num2, 0, null);
    }

    void push(Command command, int num1) {
        push(command, num1, 0, 0, null);
    }

    void push(Command command) {
        push(command, 0, 0, 0, null);
    }

    void push(Command command, int num1, ImmediateNumber immediateNumber) {
        push(command, num1, 0, 0, immediateNumber);
    }

    void push(Code code) {
        container.add(code);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int lineNumber = 0;
        for(Code code:container) {
            str.append(lineNumber).append("\t");
            str.append(code).append("\n");
            lineNumber++;
        }
        return str.toString();
    }
}
