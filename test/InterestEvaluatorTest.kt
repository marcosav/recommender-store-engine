package com.gmail.marcosav2010

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.services.recommender.evaluator.InterestScoreEvaluator
import org.kodein.di.DI
import java.time.LocalDateTime
import kotlin.test.*

class InterestEvaluatorTest {

    private val interestEvaluator = InterestScoreEvaluator(DI {})

    private val actions1 = listOf(
        Pair(ActionType.VISIT.id, null),
        Pair(ActionType.VISIT.id, null),
        Pair(ActionType.VISIT.id, null),
        Pair(ActionType.VISIT.id, null),
        Pair(ActionType.VISIT.id, null),
    )

    private val actions2 = actions1 + listOf(
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
    )

    private val actions2b = actions2 + actions2 + actions2 + actions2 + actions2

    private val actions3 = actions2 + Pair(ActionType.FAVORITE.id, null)

    private val actions3b = actions3 + Pair(ActionType.FAVORITE.id, null)

    private val actions4 = actions3 + Pair(ActionType.CART.id, null)

    private val actions4b = actions4 + Pair(ActionType.CART.id, null)

    private val actions5 = actions4 + Pair(ActionType.BUY.id, null)

    private val actions5b = actions5 + Pair(ActionType.BUY.id, null)

    private val actions6 = actions5 + Pair(ActionType.RATING.id, 0.5)

    private val actions6b = actions5 + listOf(
        Pair(ActionType.RATING.id, 5.0),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
        Pair(ActionType.CLICK.id, null),
    )

    private val actions6c = listOf(
        Pair(ActionType.RATING.id, 1.5),
        Pair(ActionType.RATING.id, 5.0)
    ) + actions6

    @Test
    fun onlyVisits() {
        val score = interestEvaluator.calculateInterest(actions1.toActions())
        assertEquals(0.0, score)
    }

    @Test
    fun visitsAndClicks() {
        var score = interestEvaluator.calculateInterest(actions2.toActions())
        assertEquals(0.4, score)

        score = interestEvaluator.calculateInterest(actions2b.toActions())
        assertEquals(1.5, score)
    }

    @Test
    fun visitsClicksAndFav() {
        var score = interestEvaluator.calculateInterest(actions3.toActions())
        assertEquals(1.4, score)

        score = interestEvaluator.calculateInterest(actions3b.toActions())
        assertEquals(1.4, score)
    }

    @Test
    fun visitsClicksFavAndCart() {
        var score = interestEvaluator.calculateInterest(actions4.toActions())
        assertEquals(2.4, score)

        score = interestEvaluator.calculateInterest(actions4b.toActions())
        assertEquals(2.4, score)
    }

    @Test
    fun visitsClicksFavCartAndBuy() {
        var score = interestEvaluator.calculateInterest(actions5.toActions())
        assertEquals(3.65, score)

        score = interestEvaluator.calculateInterest(actions5b.toActions())
        assertEquals(3.65, score)
    }

    @Test
    fun visitsClicksFavCartBuyAndRating() {
        var score = interestEvaluator.calculateInterest(actions6.toActions())
        assertEquals(3.65 + 0.5 * 0.25, score)

        score = interestEvaluator.calculateInterest(actions6b.toActions())
        assertEquals(5.0, score)

        score = interestEvaluator.calculateInterest(actions6c.toActions())
        assertEquals(3.65 + 1.5 * 0.25, score)
    }

    private fun List<Pair<Int, Double?>>.toActions() = map {
        UserAction(
            "s",
            1L,
            1L,
            it.first,
            LocalDateTime.now(),
            -1,
            it.second
        )
    }
}
