package com.gmail.marcosav2010.utils

typealias Matrix = HashMap<Pair<Long, Long>, Double>

class SymmetricMatrix : Matrix() {

    private val items by lazy { keys.map { it.first }.union(keys.map { it.second }).distinct() }

    operator fun get(i1: Long, i2: Long): Double {
        return get(Pair(i1, i2)) ?: 0.0
    }

    override fun get(key: Pair<Long, Long>): Double? {
        return super.get(Pair(key.first, key.second)) ?: super.get(Pair(key.second, key.first))
    }

    operator fun set(i1: Long, i2: Long, value: Double) {
        val p1 = Pair(i1, i2)
        val p2 = Pair(i2, i1)
        if (!containsKey(p1) && !containsKey(p2))
            put(p1, value)
    }

    fun print() = print(items, items)
}