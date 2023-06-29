package com.kwaishou.riaid.production.factory;

import com.kuaishou.riaid.proto.BasicVariable;
import com.kuaishou.riaid.proto.BasicVariableValue;

public class VariableFactory {
    // 是不是本地用，本地用不需要加?c ?json_string
    public static final boolean isLocal = false;

    public static BasicVariable createBoolVariable(int key, boolean value) {
        BasicVariable.Builder builder = BasicVariable.newBuilder();
        builder.setKey(key);
        builder.setValue(BasicVariableValue.Value.newBuilder()
                .setB(value)
                .setType(BasicVariableValue.Type.BOOL)
                .build());
        return builder.build();
    }

    public static BasicVariable createIntegerVariable(int key, long value) {
        BasicVariable.Builder builder = BasicVariable.newBuilder();
        builder.setKey(key);
        builder.setValue(BasicVariableValue.Value.newBuilder()
                .setI(value)
                .setType(BasicVariableValue.Type.INTEGER)
                .build());
        return builder.build();
    }

    /**
     * 替换成字符串的变量
     */
    public static String getVar(String variable) {
        if (isLocal) {
            return "${" + variable + "}";
        } else {
            return "${" + variable + "?json_string}";
        }
    }

    public static String geDefaultVar(String variable) {
        return "${" + variable + "}";
    }

    /**
     * 替换成数字的变量
     */
    public static String getNumVar(String variable) {
        if (isLocal) {
            return "${" + variable + "}";
        } else {
            return "${" + variable + "?c}";
        }
    }

    /**
     * 数字变量原返回
     */
    public static String getCodeVar(int var) {
        if (isLocal) {
            return "${" + var + "}";
        } else {
            return "${code_" + var + "?json_string}";
        }
    }
}
