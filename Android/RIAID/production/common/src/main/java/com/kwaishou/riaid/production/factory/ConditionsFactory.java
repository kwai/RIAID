package com.kwaishou.riaid.production.factory;

import com.kuaishou.riaid.proto.ADConditionModel;

public class ConditionsFactory {

    public ADConditionModel createCondition(String name, String value) {
        return ADConditionModel.newBuilder()
                .setConditionName(name)
                .setConditionValue(value)
                .build();
    }
}
