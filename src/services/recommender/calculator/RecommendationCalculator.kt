package com.gmail.marcosav2010.services.recommender.calculator

import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.utils.Matrix
import com.gmail.marcosav2010.utils.SymmetricMatrix
import com.gmail.marcosav2010.utils.getOrPut
import com.gmail.marcosav2010.utils.print
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.math.abs

enum class SimilarityMode {
    PEARSON, COSINE, ADJUSTED_COSINE
}

class RecommendationCalculator(di: DI) {

    companion object {
        private val CURRENT_MODE = SimilarityMode.PEARSON
    }

    private val userActionRepository by di.instance<UserActionRepository>()

    private val items: List<Long> = userActionRepository.findRatedProducts().map { it.item }
    private val users: List<Long> = userActionRepository.findRaters().map { it.user }

    private val userAverages: HashMap<Long, Double> = hashMapOf()
    private val itemAverages: HashMap<Long, Double> = hashMapOf()
    private val productRatingsCache: HashMap<Long, HashMap<Long, Double>> = hashMapOf()

    lateinit var similarityMatrix: SymmetricMatrix
        private set
    lateinit var scoreMatrix: Matrix
        private set

    private fun similarity(item1: Long, item2: Long): Double {
        val ratings1 = getRatingsFor(item1)
        val ratings2 = getRatingsFor(item2)
        val ctx = ItemComparisonContext(
            item1,
            item2,
            ratings1,
            ratings2,
            { userAverages.avg(it)!! }) { itemAverages.iAvg(it)!! }

        return when (CURRENT_MODE) {
            SimilarityMode.ADJUSTED_COSINE -> AdjustedCosineSim.calculate(ctx)
            SimilarityMode.PEARSON -> PearsonSim.calculate(ctx)
            SimilarityMode.COSINE -> CosineSim.calculate(ctx)
        }
    }

    private fun createSimilarityMatrix(items: Iterable<Long>) {
        similarityMatrix = SymmetricMatrix()

        items.forEach { i1 ->
            items.forEach { i2 ->
                if (similarityMatrix[i1, i2] == 0.0) {
                    val s = similarity(i1, i2)
                    similarityMatrix[i1, i2] = s
                }
            }
        }
    }

    private fun nearestItems(user: Long, item: Long, n: Int = 10): List<Triple<Double, Long, Double>> {
        val userRatings = userActionRepository.findUserRatingsFor(user, items)

        return userRatings.filter { it.key != item }
            .map { Triple(similarityMatrix[it.key, item], it.key, it.value) }
            .sortedByDescending { it.first }
            .take(n)
    }

    private fun score(user: Long, item: Long): Double {
        //val userRatingAvg = userAverages.avg(user)!!

        if (userActionRepository.findUserRatingsFor(user, listOf(item)).isNotEmpty()) return -1.0

        val nearest = nearestItems(user, item)

        var b = 0.0
        var a = 0.0
        nearest.forEach { (sim, _, userRating) ->
            /*val itemRatingAvg = itemAverages.iAvg(it) { i -> userActionRepository.findAverageRating(i)!! }!!

            a += (userRating - itemRatingAvg) * sim
            b += sim*/

            a += userRating * sim
            b += abs(sim)
        }

        return a / b// + userRatingAvg
    }

    private fun createScoreMatrix() {
        createSimilarityMatrix(items)

        scoreMatrix = Matrix()

        users.forEach { u ->
            items.forEach { i ->
                val s = score(u, i)
                scoreMatrix[Pair(u, i)] = s
            }
        }

        scoreMatrix.print(items, users)
    }

    fun execute() {
        //val t = System.currentTimeMillis()
        createScoreMatrix()
        //println("\nElapsed ${System.currentTimeMillis() - t} ms")
    }

    private fun getRatingsFor(item: Long) = productRatingsCache.getOrPut(item) {
        hashMapOf<Long, Double>().apply {
            userActionRepository.findRatingsFor(it).forEach { a -> this[a.user] = a.value!! }
        }
    }!!

    private fun HashMap<Long, Double>.avg(it: Long) = getOrPut(it) { userActionRepository.findUserAverageRating(it) }
    private fun HashMap<Long, Double>.iAvg(it: Long) = getOrPut(it) { userActionRepository.findAverageRating(it) }
}