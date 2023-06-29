package com.kwaishou.riaid.production.common.helper;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.kuaishou.riaid.proto.Node;

public class RIAIDTrans {
    public static void transNode(String json) {
        try {
            Node.Builder builder = Node.newBuilder();
            JsonFormat.parser().merge(json, builder);
            Node build = builder.build();
            String print = JsonFormat.printer().printingEnumsAsInts().print(build);
            System.out.println(print);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

}
