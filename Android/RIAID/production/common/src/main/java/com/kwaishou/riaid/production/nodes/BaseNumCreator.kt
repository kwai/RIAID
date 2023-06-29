package com.kwaishou.riaid.production.nodes

import com.kuaishou.riaid.proto.BoolValue
import com.kuaishou.riaid.proto.FloatValue
import com.kuaishou.riaid.proto.Int32Value

/**
 * 基本类型封装构造器
 */
object BaseNumCreator {

    fun createBoolValue(value: Boolean): BoolValue {
        return BoolValue.newBuilder()
                .setValue(value)
                .build()
    }

    fun createInt32Value(value: Int): Int32Value {
        return Int32Value.newBuilder()
                .setValue(value)
                .build()
    }

    fun createFloatValue(value: Float): FloatValue {
        return FloatValue.newBuilder()
                .setValue(value)
                .build()
    }

}