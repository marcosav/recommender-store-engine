package com.gmail.marcosav2010.services.recommender.calculator

import com.gmail.marcosav2010.model.SimilarItem
import com.gmail.marcosav2010.model.SimilarItems
import com.gmail.marcosav2010.model.UserTopItem
import com.gmail.marcosav2010.model.UserTopItems
import com.gmail.marcosav2010.repositories.SimilarItemRepository
import com.gmail.marcosav2010.repositories.UserInterestRepository
import com.gmail.marcosav2010.repositories.UserTopItemsRepository
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

    private val userInterestRepository by di.instance<UserInterestRepository>()
    private val similarItemRepository by di.instance<SimilarItemRepository>()
    private val userTopItemsRepository by di.instance<UserTopItemsRepository>()

    private val items: List<Long> = userInterestRepository.findItems().map { it.item }
    private val users: List<Long> = userInterestRepository.findUsers().map { it.user }

    private val userAverages: HashMap<Long, Double> = hashMapOf()
    private val itemAverages: HashMap<Long, Double> = hashMapOf()
    private val productScoresCache: HashMap<Long, HashMap<Long, Double>> = hashMapOf()

    private lateinit var similarityMatrix: SymmetricMatrix
    private lateinit var scoreMatrix: Matrix

    private fun similarity(item1: Long, item2: Long): Double {
        val scores1 = getScoresFor(item1)
        val scores2 = getScoresFor(item2)
        val ctx = ItemComparisonContext(
            item1,
            item2,
            scores1,
            scores2,
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
        val userRatings = userInterestRepository.findUserScoresFor(user, items)

        return userRatings.filter { it.key != item }
            .map { Triple(similarityMatrix[it.key, item], it.key, it.value) }
            .sortedByDescending { it.first }
            .take(n)
    }

    private fun score(user: Long, item: Long): Double {
        //val userScoreAvg = userAverages.avg(user)!!

        //if (userInterestRepository.findUserScoresFor(user, listOf(item)).isNotEmpty()) return -1.0

        val nearest = nearestItems(user, item)

        var b = 0.0
        var a = 0.0
        nearest.forEach { (sim, _, userRating) ->
            /*val itemScoreAvg = itemAverages.iAvg(it)

            a += (userRating - itemScoreAvg) * sim
            b += sim*/

            a += userRating * sim
            b += abs(sim)
        }

        return a / b// + userScoreAvg
    }

    private fun createScoreMatrix(save: Boolean) {
        createSimilarityMatrix(items)

        scoreMatrix = Matrix()

        users.forEach { u ->
            val itemScores = items.map { i ->
                val s = score(u, i)
                scoreMatrix[Pair(u, i)] = s

                if (save)
                    userInterestRepository.updatePredictedScore(u, i, s)

                Pair(i, s)
            }

            if (save)
                saveTopScores(u, itemScores)
        }

        scoreMatrix.print(items, users)
    }

    private fun saveItemSimilarity() {
        items.forEach { i1 ->
            val similar = mutableListOf<SimilarItem>()

            items.forEach { i2 ->
                val s = similarityMatrix[i1, i2]
                similar.add(SimilarItem(i2, s))
            }

            val entry = SimilarItems(i1, similar.sortedByDescending { it.score }.take(25))
            similarItemRepository.add(entry)
        }
    }

    private fun saveTopScores(user: Long, itemScores: List<Pair<Long, Double>>) {
        val topUserItems = itemScores
            .sortedByDescending { it.second }
            .take(25)
            .map { UserTopItem(it.first, it.second) }

        userTopItemsRepository.add(UserTopItems(user, topUserItems))
    }

    fun execute(save: Boolean = true) {
        similarItemRepository.clearAll()
        userTopItemsRepository.clearAll()
        //val t = System.currentTimeMillis()
        createScoreMatrix(save)
        //println("\nElapsed ${System.currentTimeMillis() - t} ms")
        if (save)
            saveItemSimilarity()
    }

    private fun getScoresFor(item: Long) = productScoresCache.getOrPut(item) {
        hashMapOf<Long, Double>().apply {
            userInterestRepository.findScoresFor(it).forEach { a -> this[a.user] = a.score!! }
        }
    }!!

    private fun HashMap<Long, Double>.avg(it: Long) = getOrPut(it) { userInterestRepository.findUserAverageScore(it) }
    private fun HashMap<Long, Double>.iAvg(it: Long) = getOrPut(it) { userInterestRepository.findItemAverageScore(it) }
}