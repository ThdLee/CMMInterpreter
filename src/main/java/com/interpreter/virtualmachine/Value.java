package com.interpreter.virtualmachine;

import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.PrimaryType;

import java.util.EnumMap;
import java.util.Map;

public class Value {

    private static final Map<CodeChunk.ImmediateType, PrimaryType> typeMap = new EnumMap<CodeChunk.ImmediateType, PrimaryType>(CodeChunk.ImmediateType.class) {{
        put(CodeChunk.ImmediateType.Integer, PrimaryType.Int);
        put(CodeChunk.ImmediateType.Double, PrimaryType.Double);
        put(CodeChunk.ImmediateType.Bool, PrimaryType.Bool);
        put(CodeChunk.ImmediateType.String, PrimaryType.String);
    }};

    public final PrimaryType type;
    public final int intValue;
    public final double douValue;
    public final boolean bolValue;
    public final String strValue;
    public final Array arrValue;

    private Value(PrimaryType type, int intValue, double douValue, boolean bolValue, String strValue, Array arrValue) {
        this.type = type;
        this.intValue = intValue;
        this.douValue = douValue;
        this.bolValue = bolValue;
        this.strValue = strValue;
        this.arrValue = arrValue;
    }

    public Value(CodeChunk.ImmediateNumber immediateNumber) {
        this(typeMap.get(immediateNumber.getType()), immediateNumber.getIntegerValue(),
                immediateNumber.getDoubleValue(), immediateNumber.getBooValue(), immediateNumber.getStringValue(), null);
    }

    public Value(int value) {
        this(PrimaryType.Int, value, 0.0, false, "", null);
    }

    public Value(double value) {
        this(PrimaryType.Double, 0, value, false, "", null);
    }

    public Value(boolean value) {
        this(PrimaryType.Bool, 0, 0.0, value, "", null);
    }

    public Value(String str) {
        this(PrimaryType.String, 0, 0.0, false, str, null);
    }

    public Value(Array arr) {
        this(PrimaryType.Array, 0, 0.0, false, "", arr);
    }

    public Value(PrimaryType type) {
        this(type, 0, 0.0, false, "", null);
    }

    public boolean convertToBool() {
        if (type == PrimaryType.Bool) {
            return bolValue;
        } else if (type == PrimaryType.Int) {
            return intValue != 0;
        } else if (type == PrimaryType.Double) {
            return douValue != 0.0;
        }
        throw new RuntimeException(this + " cannot convert to " + PrimaryType.Bool);
    }

    public static Value add(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue + v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue + v2.douValue);
            else if (v1.type == PrimaryType.String)
                return new Value(v1.strValue + v2.strValue);
        }
        return null;
    }

    public static Value sub(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue - v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue - v2.douValue);
        }
        return null;
    }

    public static Value mul(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue * v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue * v2.douValue);
        }
        return null;
    }

    public static Value div(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue / v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue / v2.douValue);
        }
        return null;
    }

    public static Value gt(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue > v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue > v2.douValue);
        }
        return null;
    }

    public static Value lt(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue < v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue < v2.douValue);
        }
        return null;
    }

    public static Value gte(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue >= v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue <= v2.douValue);
        }
        return null;
    }

    public static Value lte(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue <= v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue <= v2.douValue);
        }
        return null;
    }

    public static Value checkEquals(Value v1, Value v2) {
        v1 = convertNumberToHeightTypeLevel(v1, v2);
        v2 = convertNumberToHeightTypeLevel(v2, v1);

        if (v1.type == v2.type) {
            if (v1.type == PrimaryType.Int)
                return new Value(v1.intValue == v2.intValue);
            else if (v1.type == PrimaryType.Double)
                return new Value(v1.douValue == v2.douValue);
            else if (v1.type == PrimaryType.Bool)
                return new Value(v1.bolValue == v2.bolValue);
            else if (v1.type == PrimaryType.String)
                return new Value(v1.strValue.equals(v2.strValue));
            else if (v1.type == PrimaryType.Array)
                return new Value((v1.arrValue == v2.arrValue));
        }
        return null;
    }

    public static Value checkNotEquals(Value v1, Value v2) {
        Value value = checkEquals(v1, v2);
        if (value != null) {
            return new Value(!value.bolValue);
        }
        return null;
    }

    public static Value convertNumberToHeightTypeLevel(Value convertedValue, Value comparedValue) {
        if (convertedValue.type == PrimaryType.Int && comparedValue.type == PrimaryType.Double) {
            convertedValue = new Value((double)convertedValue.intValue);
        }
        return convertedValue;
    }

    @Override
    public String toString() {
        if (type == PrimaryType.Int) {
            return String.valueOf(intValue);
        } else if (type == PrimaryType.Double) {
            return String.valueOf(douValue);
        } else if (type == PrimaryType.Bool) {
            return String.valueOf(bolValue);
        } else if (type == PrimaryType.String) {
            return strValue;
        } else if (type == PrimaryType.Array) {
            return arrValue.toString();
        }
        throw new RuntimeException();
    }
}
