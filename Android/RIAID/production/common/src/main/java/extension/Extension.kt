package extension

import com.kwaishou.riaid.production.factory.VariableFactory

/**
 * 这里以后用来加方法扩展
 */


fun String.dataBinding(): String = VariableFactory.getVar(this)