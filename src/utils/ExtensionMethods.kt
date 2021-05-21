package com.gmail.marcosav2010.utils

import kotlin.collections.HashMap
import kotlin.random.Random

fun <T> HashMap<Long, T>.getOrPut(
    it: Long,
    put: (Long) -> T?
): T? {
    var v = this[it]
    if (v == null) {
        v = put(it)
        v?.let { n -> this[it] = n }
    }
    return v
}

const val PRINTF = "%10s "

fun Map<Pair<Long, Long>, Double>.print(x: Iterable<Long>, y: Iterable<Long>) {
    System.out.printf(PRINTF, "")
    x.forEach { System.out.printf(PRINTF, it) }
    println()
    y.forEach { i1 ->
        System.out.printf(PRINTF, i1)
        x.forEach { i2 -> System.out.printf(PRINTF, "%.5f".format(this[Pair(i1, i2)])) }
        println()
    }
}

private val rnd = java.util.Random()

fun Random.nextGaussian(std: Double = 1.0, mean: Double = 0.0) = rnd.nextGaussian() * std + mean