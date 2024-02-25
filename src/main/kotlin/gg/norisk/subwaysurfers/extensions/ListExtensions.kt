package gg.norisk.subwaysurfers.extensions

import java.util.*

fun <E> List<E>.toStack(): Stack<E> {
    val stack = Stack<E>()
    forEach(stack::add)
    return stack
}
