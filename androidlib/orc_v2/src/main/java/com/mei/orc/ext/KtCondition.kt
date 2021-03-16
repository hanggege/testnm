package com.mei.orc.ext

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/1
 */

/**
 * 三元运算替代方案  会马上执行所有的取值操作，一般用于固定值时
 * @param trueResult 为true时返回时
 * @param falseResult 为false时返回时
 * @param trueBack 为true时回调
 * @param falseBack 为false时回调
 *
 */
fun <T> Boolean?.selectBy(trueResult: T,
                          falseResult: T,
                          trueBack: () -> Unit = {},
                          falseBack: () -> Unit = {}): T =
        if (this == true) {
            trueBack()
            trueResult
        } else {
            falseBack()
            falseResult
        }

/**
 * 不会马上执行所有取值操作
 */
fun <T> Boolean?.selectBy(trueBack: () -> T? = { null },
                          falseBack: () -> T? = { null }): T? =
        if (this == true) {
            trueBack()
        } else {
            falseBack()
        }

/**
 * 三元运算替代方案
 * @param trueBack 为true时回调
 * @param falseBack 为false时回调
 */
fun Boolean?.selectByBack(trueBack: () -> Unit = {}, falseBack: () -> Unit = {}) = if (this == true) trueBack() else falseBack()

/**
 * 只处理true
 */
fun Boolean?.selectByTrue(trueBack: () -> Unit) = if (this == true) trueBack() else {
}

/**
 * let的补充
 */
fun <T> T?.letElse(nonullBack: (T) -> Unit = {}, nullBack: () -> Unit = {}) = if (this != null) nonullBack(this) else nullBack()


/**
 * boolean 与Int相互转换  1 ： true  0:false
 */
fun Int.isTrue(): Boolean = this != 0
fun Int.isFalse(): Boolean = this == 0

fun Boolean.toInt(): Int = if (this) 1 else 0