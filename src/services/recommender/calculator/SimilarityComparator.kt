package com.gmail.marcosav2010.services.recommender.calculator

import kotlin.math.pow
import kotlin.math.sqrt

interface SimilarityComparator {

    fun calculate(ctx: ItemComparisonContext): Double
}

class ItemComparisonContext(
    val item1: Long,
    val item2: Long,
    val ratings1: Map<Long, Double>,
    val ratings2: Map<Long, Double>,
    val uAvg: (Long) -> Double,
    val iAvg: (Long) -> Double,
) {
    var common: Long = 0
}

object CosineSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        var a = 0.0
        var b = 0.0
        var c = 0.0

        ctx.ratings1.filter { ctx.ratings2.containsKey(it.key) }.forEach { (u, v) ->
            a += ctx.ratings2[u]!! * v
            b += v.pow(2)
            c += ctx.ratings2[u]!!.pow(2)

            ctx.common++
        }

        b = sqrt(b)
        c = sqrt(c)

        return a / (b * c)
    }
}

object AdjustedCosineSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        var a = 0.0
        var b = 0.0
        var c = 0.0

        ctx.ratings1.filter { ctx.ratings2.containsKey(it.key) }.forEach { (u, v) ->
            val ratingAvg = ctx.uAvg(u)

            a += (ctx.ratings2[u]!! - ratingAvg) * (v - ratingAvg)
            b += (v - ratingAvg).pow(2)
            c += (ctx.ratings2[u]!! - ratingAvg).pow(2)

            ctx.common++
        }

        b = sqrt(b)
        c = sqrt(c)

        return a / (b * c)
    }
}

object PearsonSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        val ratingAvg1 = ctx.iAvg(ctx.item1)
        val ratingAvg2 = ctx.iAvg(ctx.item2)

        var a = 0.0
        var b = 0.0
        var c = 0.0

        ctx.ratings1.filter { ctx.ratings2.containsKey(it.key) }.forEach { (u, v) ->
            a += (ctx.ratings2[u]!! - ratingAvg2) * (v - ratingAvg1)
            b += (v - ratingAvg1).pow(2)
            c += (ctx.ratings2[u]!! - ratingAvg2).pow(2)

            ctx.common++
        }

        b = sqrt(b)
        c = sqrt(c)

        return a / (b * c)
    }
}