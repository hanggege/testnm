package com.mei.orc.ext

import androidx.annotation.IntRange

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/8/30.
 */
fun <T> List<T>.addIf(condition: Boolean, vararg e: T): List<T> {
    return if (condition) this + listOf(*e) else this
}

fun <T> MutableList<T>.addSelfIf(condition: Boolean, vararg e: T): MutableList<T> {
    if (condition) e.forEach { this.add(it) }
    return this
}

fun <T> List<T>.addIf(condition: Boolean, condition2: Boolean, vararg e: T): List<T> {
    return if (condition && condition2) this + listOf(*e) else this
}

fun <T> List<T>.lastElement() = if (isEmpty()) null else this[lastIndex]


fun <T> MutableList<T>.joinList(elements: List<T>): List<T> {
    addAll(elements)
    return this
}


/**
 * 长度大于index 不传则是判不为空
 * @param index 长度
 */
fun <T> Collection<T>?.indexGreaterThan(index: Int = 0): Boolean = this?.size ?: 0 > index

/**
 * 列表去重
 */
fun <T, K> MutableList<T>.distinctByTo(selector: (T) -> K): MutableList<T> {
    val list = distinctBy(selector)
    clear()
    addAll(list)
    return this
}

/**
 * 检查是否有改变
 */
fun <T> List<T>?.checkChange(list: List<T>?, predicate: (T, T) -> Boolean): Boolean {
    if (this == null || list == null || size != list.size) return true
    var changed = false
    forEachIndexed { index, t ->
        if (predicate(t, list[index])) {
            changed = true
        }
    }
    return changed
}

/**
 * 提取前面多少项，如果不够则全量返回
 */
fun <T> MutableList<T>?.subListByIndex(@IntRange(from = 0) index: Int): MutableList<T> {
    val list = arrayListOf<T>()
    if (this != null) list.addAll(this.subList(0, Math.min(index, orEmpty().size)))
    return list
}

/**
 * 获取指定位置的数据或者空
 */
fun <T> List<T>.getIndexOrNull(index: Int): T? = if (index in 0 until size) get(index) else null

/**
 * 是否在列表范围中
 */
fun <T> List<T>.inList(index: Int): Boolean = index in 0 until size



