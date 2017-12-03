package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.CodeChunk.*;
import com.interpreter.intermediatecode.PrimaryType;
import com.interpreter.virtualmachine.DataChunk.Package;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class Interpreter {

    private final static Map<Command, CommandInterpreter> commandMap = new EnumMap<>(CodeChunk.Command.class);
    private final Runtime runtime;

    Interpreter(Runtime runtime) {
        this.runtime = runtime;
    }

    public int run(int line, Code code, DataChunk dataChunk) {
        CommandInterpreter interpreter = commandMap.get(code.getCommand());
        if (interpreter != null) {
            return interpreter.run(this, line, code, dataChunk);
        }
        throw new RuntimeException("unknown command " + code.getCommand());
    }

    private interface CommandInterpreter{
        int run(Interpreter self, int line, CodeChunk.Code code, DataChunk dataChunk);
    }

    static {
        Map<Command, CommandInterpreter> M = commandMap;

        M.put(Command.Jmp, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            return code.getNum1();
        });

        M.put(Command.JmpUnless, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value value = dataChunk.getData(code.getNum2());
            int nextLine = line + 1;
            if (!value.convertToBool()) {
                nextLine = code.getNum1();
            }
            return nextLine;
        });

        M.put(Command.Write, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value value = dataChunk.getData(code.getNum1());
            // TODO
            return  line + 1;
        });

        M.put(Command.Write, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value value = dataChunk.getData(code.getNum1());
            self.runtime.out.println(value);
            return  line + 1;
        });

        M.put(Command.NewArray, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            int length = code.getNum2();
            // TODO
            Array array = new Array(length, PrimaryType.Array);
            dataChunk.setData(code.getNum1(), new Value(array));
            return line + 1;
        });

        M.put(Command.Get, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value index = dataChunk.getData(code.getNum2());
            if (index.type != PrimaryType.Int) {
                throw new RuntimeException("array index cannot be '" + index + "'");
            }
            Value value = dataChunk.getData(code.getNum1());
            if (value.type != PrimaryType.Array) {
                throw new RuntimeException("'" + value + "' is not array type");
            }
            Array array = value.arrValue;
            dataChunk.setData(code.getNum1(), array.getElement(index.intValue));
            return line + 1;
        });

        M.put(Command.Add, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.add(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '+' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Sub, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.sub(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '-' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Mul, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.mul(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '*' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Div, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.div(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '/' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Mod, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            if (v1.type != PrimaryType.Int || v2.type != PrimaryType.Int) {
                throw new RuntimeException("undefined operation for "+ v1 +" '%' "+ v2);
            }
            Value res = new Value(v1.intValue % v2.intValue);
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Gt, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.gt(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '>' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Lt, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.div(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '<' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Gte, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.div(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '>=' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Lte, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.lte(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '<=' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Equal, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.checkEquals(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '==' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.NotEqual, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = Value.checkNotEquals(v1, v2);
            if (res == null) {
                throw new RuntimeException("undefined operation for "+ v1 +" '!=' "+ v2);
            }
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Not, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value value = dataChunk.getData(code.getNum1());
            value = new Value(!value.convertToBool());
            dataChunk.setData(code.getNum1(), value);
            return line + 1;
        });

        M.put(Command.Opposite, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value value = dataChunk.getData(code.getNum1());
            if (value.type == PrimaryType.Int) {
                value = new Value(-value.intValue);
            } else if (value.type == PrimaryType.Double) {
                value = new Value(-value.douValue);
            } else {
                throw new RuntimeException("undefined operation for '!=' " + value);
            }
            dataChunk.setData(code.getNum1(), value);
            return line + 1;
        });

        M.put(Command.And, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = v1.convertToBool() ? v2 : v1;
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

        M.put(Command.Or, (Interpreter self, int line, Code code, DataChunk dataChunk)-> {
            Value v1 = dataChunk.getData(code.getNum1());
            Value v2 = dataChunk.getData(code.getNum2());
            Value res = v1.convertToBool() ? v1 : v2;
            dataChunk.setData(code.getNum1(), res);
            return line + 1;
        });

    }
}
