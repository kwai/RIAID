package com.kwaishou.riaid.production.nodes
import com.kuaishou.riaid.proto.Node

/**
 * 统一接口，创建Node对象
 *
 * @param <T> 返回的类型
 */
interface INodeCreator {

    fun createNode(): Node

    fun createNode(key: Int): Node? = null

}