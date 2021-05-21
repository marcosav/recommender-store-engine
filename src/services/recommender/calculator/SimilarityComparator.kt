package com.gmail.marcosav2010.services.recommender.calculator

import kotlin.math.pow
import kotlin.math.sqrt

interface SimilarityComparator {

    fun calculate(ctx: ItemComparisonContext): Double
}

class ItemComparisonContext(
    val item1: Long,
    val item2: Long,
    val ratings1: HashMap<Long, Double>,
    val ratings2: HashMap<Long, Double>,
    val uAvg: (Long) -> Double,
    val iAvg: (Long) -> Double,
) {
    var common: Long = 0
}

object CosineSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        val a = ctx.ratings1.map { (u, v) ->
            if (ctx.ratings2.containsKey(u))
                (ctx.ratings2[u]!! * v).also { ctx.common++ }
            else 0.0
        }.sum()

        val b = sqrt(ctx.ratings1.map { it.value.pow(2) }.sum()) *
                sqrt(ctx.ratings2.map { it.value.pow(2) }.sum())

        return a / b
    }
}

object AdjustedCosineSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        val a = ctx.ratings1.map { (u, v) ->
            val ratingAvg = ctx.uAvg(u)
            if (ctx.ratings2.containsKey(u))
                ((ctx.ratings2[u]!! - ratingAvg) * (v - ratingAvg)).also { ctx.common++ }
            else 0.0
        }.sum()

        val b = sqrt(ctx.ratings1.map { (it.value - ctx.uAvg(it.key)).pow(2) }.sum()) *
                sqrt(ctx.ratings2.map { (it.value - ctx.uAvg(it.key)).pow(2) }.sum())

        return a / b
    }
}

object PearsonSim : SimilarityComparator {

    override fun calculate(ctx: ItemComparisonContext): Double {
        val ratingAvg1 = ctx.iAvg(ctx.item1)
        val ratingAvg2 = ctx.iAvg(ctx.item2)

        val a = ctx.ratings1.map { (u, v) ->
            if (ctx.ratings2.containsKey(u))
                ((ctx.ratings2[u]!! - ratingAvg2) * (v - ratingAvg1)).also { ctx.common++ }
            else 0.0
        }.sum()

        val b = sqrt(ctx.ratings1.map { (it.value - ratingAvg1).pow(2) }.sum()) *
                sqrt(ctx.ratings2.map { (it.value - ratingAvg2).pow(2) }.sum())

        return a / b
    }
}