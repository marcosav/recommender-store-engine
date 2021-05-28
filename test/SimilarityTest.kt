package com.gmail.marcosav2010

import com.gmail.marcosav2010.services.recommender.calculator.AdjustedCosineSim
import com.gmail.marcosav2010.services.recommender.calculator.CosineSim
import com.gmail.marcosav2010.services.recommender.calculator.ItemComparisonContext
import com.gmail.marcosav2010.services.recommender.calculator.PearsonSim
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.*

class SimilarityTest {

    private val scores = arrayOf(
        // I1, I2, I3, I4, ...
        arrayOf(3.0, 4.0, 3.0, null, 3.5), // U1
        arrayOf(1.0, 1.5, null, null, 3.0), // ...
        arrayOf(4.5, 1.5, null, null, 5.0),
        arrayOf(3.5, 3.0, null, 4.0, 2.0),
        arrayOf(4.0, null, null, null, 4.0),
        arrayOf(5.0, null, 5.0, null, null),
        arrayOf(null, 5.0, null, null, 1.0),
        arrayOf(null, 2.0, null, 1.5, null),
        arrayOf(null, null, 1.0, null, 0.5)
    )

    private val itemScores = (0L until scores[0].size).associateWith { i ->
        (0L until scores.size)
            .map { Pair(it, scores[it.toInt()][i.toInt()]) }
            .filter { it.second != null }
            .associate { Pair(it.first, it.second!!) }
    }

    private val itemAvg = itemScores.map { i -> Pair(i.key, i.value.map { it.value }.average()) }.toMap()
    private val userAvg = (0L until scores.size).associateWith { scores[it.toInt()].filterNotNull().average() }

    private val i1 = 0L
    private val i2 = 4L

    private val i1n = 2L
    private val i2n = 3L

    private val i1o = 1L
    private val i2o = 2L

    @Test
    fun similarityCosine() {
        val ctx = ItemComparisonContext(i1, i2, itemScores[i1]!!, itemScores[i2]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = CosineSim.calculate(ctx)

        assertEquals(5, ctx.common)

        assertEquals(
            (3.0 * 3.5 + 1.0 * 3.0 + 4.5 * 5.0 + 3.5 * 2.0 + 4.0 * 4.0) /
                    (sqrt(arrayOf(3.0, 1.0, 4.5, 3.5, 4.0).sumOf { it.pow(2) })
                            * sqrt(arrayOf(3.5, 3.0, 5.0, 2.0, 4.0).sumOf { it.pow(2) })), res
        )
    }

    @Test
    fun similarityCosineNone() {
        val ctx =
            ItemComparisonContext(i1n, i2n, itemScores[i1n]!!, itemScores[i2n]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = CosineSim.calculate(ctx)

        assertEquals(0, ctx.common)
        assertEquals(Double.NaN, res)
    }

    @Test
    fun similarityAdjCosine() {
        val ctx = ItemComparisonContext(i1, i2, itemScores[i1]!!, itemScores[i2]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = AdjustedCosineSim.calculate(ctx)

        assertEquals(5, ctx.common)

        assertEquals(
            ((3.0 - userAvg[0]!!) * (3.5 - userAvg[0]!!)
                    + (1.0 - userAvg[1]!!) * (3.0 - userAvg[1]!!)
                    + (4.5 - userAvg[2]!!) * (5.0 - userAvg[2]!!)
                    + (3.5 - userAvg[3]!!) * (2.0 - userAvg[3]!!)
                    + (4.0 - userAvg[4]!!) * (4.0 - userAvg[4]!!)) /
                    (sqrt(
                        mapOf(
                            Pair(0L, 3.0),
                            Pair(1L, 1.0),
                            Pair(2L, 4.5),
                            Pair(3L, 3.5),
                            Pair(4L, 4.0)
                        ).map { (it.value - userAvg[it.key]!!).pow(2) }.sum()
                    ) * sqrt(
                        mapOf(
                            Pair(0L, 3.5),
                            Pair(1L, 3.0),
                            Pair(2L, 5.0),
                            Pair(3L, 2.0),
                            Pair(4L, 4.0)
                        ).map { (it.value - userAvg[it.key]!!).pow(2) }.sum()
                    )), res
        )
    }

    @Test
    fun similarityAdjCosineOne() {
        val ctx =
            ItemComparisonContext(i1o, i2o, itemScores[i1o]!!, itemScores[i2o]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = AdjustedCosineSim.calculate(ctx)

        assertEquals(1, ctx.common)

        assertEquals(
            ((4.0 - userAvg[0]!!) * (3.0 - userAvg[0]!!)) /
                    (sqrt((4.0 - userAvg[0]!!).pow(2)) * sqrt((3.0 - userAvg[0]!!).pow(2))), res
        )
    }

    @Test
    fun similarityAdjCosineNone() {
        val ctx =
            ItemComparisonContext(i1n, i2n, itemScores[i1n]!!, itemScores[i2n]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = AdjustedCosineSim.calculate(ctx)

        assertEquals(0, ctx.common)
        assertEquals(Double.NaN, res)
    }

    @Test
    fun similarityPearson() {
        val ctx = ItemComparisonContext(i1, i2, itemScores[i1]!!, itemScores[i2]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = PearsonSim.calculate(ctx)

        assertEquals(5, ctx.common)

        assertEquals(
            ((3.0 - itemAvg[i1]!!) * (3.5 - itemAvg[i2]!!)
                    + (1.0 - itemAvg[i1]!!) * (3.0 - itemAvg[i2]!!)
                    + (4.5 - itemAvg[i1]!!) * (5.0 - itemAvg[i2]!!)
                    + (3.5 - itemAvg[i1]!!) * (2.0 - itemAvg[i2]!!)
                    + (4.0 - itemAvg[i1]!!) * (4.0 - itemAvg[i2]!!)) /
                    (sqrt(arrayOf(3.0, 1.0, 4.5, 3.5, 4.0).sumOf { (it - itemAvg[i1]!!).pow(2) })
                            * sqrt(arrayOf(3.5, 3.0, 5.0, 2.0, 4.0).sumOf { (it - itemAvg[i2]!!).pow(2) })), res
        )
    }

    @Test
    fun similarityPearsonOne() {
        val ctx =
            ItemComparisonContext(i1o, i2o, itemScores[i1o]!!, itemScores[i2o]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = PearsonSim.calculate(ctx)

        assertEquals(1, ctx.common)
        assertEquals(
            ((4.0 - itemAvg[i1o]!!) * (3.0 - itemAvg[i2o]!!)) /
                    (sqrt((4.0 - itemAvg[i1o]!!).pow(2)) * sqrt((3.0 - itemAvg[i2o]!!).pow(2))), res
        )
    }

    @Test
    fun similarityPearsonNone() {
        val ctx =
            ItemComparisonContext(i1n, i2n, itemScores[i1n]!!, itemScores[i2n]!!, { userAvg[it]!! }) { itemAvg[it]!! }

        val res = PearsonSim.calculate(ctx)

        assertEquals(0, ctx.common)
        assertEquals(Double.NaN, res)
    }
}
