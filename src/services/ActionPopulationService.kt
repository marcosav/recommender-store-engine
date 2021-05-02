package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.random.Random

class ActionPopulationService(di: DI) {

    companion object {
        private const val DATASET = 1

        private const val ITEM_AMOUNT = 5000L
        private const val USER_AMOUNT = 25L

        private const val MAX_VISITS_PER_USER = 10
        private const val MAX_CLICKS_PER_USER_SESSION = 4

        private const val VISIT_PROBABILITY = 0.01

        private const val ADD_CART_PROBABILITY = 0.4
        private const val ADD_FAV_PROBABILITY = 0.5
        private const val BUY_PROBABILITY = 0.6
        private const val RATE_PROBABILITY = 0.7
    }

    private val userActionRepository by di.instance<UserActionRepository>()

    fun generate() {
        val startTime = System.currentTimeMillis()

        userActionRepository.clearAll()

        println("Populating...")

        (1..ITEM_AMOUNT).forEach { item ->
            if (Random.nextDouble() <= VISIT_PROBABILITY) {
                (1..USER_AMOUNT).forEach { user ->
                    (1..Random.nextInt(MAX_VISITS_PER_USER + 1)).forEach {
                        userActionRepository.add(
                            UserAction(
                                "SESSION-$user-$it-${Random.nextInt()}",
                                user,
                                item,
                                ActionType.VISIT.id,
                                LocalDateTime.now(),
                                0
                            )
                        )

                        (1..Random.nextInt(MAX_CLICKS_PER_USER_SESSION + 1)).forEach { _ ->
                            userActionRepository.add(
                                UserAction(
                                    "",
                                    user,
                                    item,
                                    ActionType.CLICK.id,
                                    LocalDateTime.now(),
                                    0
                                )
                            )
                        }
                    }

                    if (Random.nextDouble() <= ADD_FAV_PROBABILITY)
                        userActionRepository.add(
                            UserAction(
                                "",
                                user,
                                item,
                                ActionType.FAVORITE.id,
                                LocalDateTime.now(),
                                0,
                            )
                        )

                    if (Random.nextDouble() <= ADD_CART_PROBABILITY) {
                        userActionRepository.add(
                            UserAction(
                                "",
                                user,
                                item,
                                ActionType.CART.id,
                                LocalDateTime.now(),
                                0,
                            )
                        )

                        if (Random.nextDouble() <= BUY_PROBABILITY) {
                            userActionRepository.add(
                                UserAction(
                                    "",
                                    user,
                                    item,
                                    ActionType.BUY.id,
                                    LocalDateTime.now(),
                                    0,
                                )
                            )

                            if (Random.nextDouble() <= RATE_PROBABILITY)
                                userActionRepository.add(
                                    UserAction(
                                        "",
                                        user,
                                        item,
                                        ActionType.RATING.id,
                                        LocalDateTime.now(),
                                        0,
                                        Random.nextDouble(0.5, 5.0)
                                    )
                                )
                        }
                    }
                }
            } else {
                /*if (Random.nextInt(10) == 1)
                    (1..10L).forEach { user ->
                        (0..Random.nextInt(15)).forEach {
                            userActionRepository.add(
                                UserAction(
                                    "SESSION-$user-$it-${Random.nextInt()}",
                                    user,
                                    item,
                                    ActionType.VISIT.id,
                                    LocalDateTime.now(),
                                    0
                                )
                            )
                        }
                    }*/
            }
        }

        println("Done in ${System.currentTimeMillis() - startTime} ms")
    }

    fun import() {
        userActionRepository.clearAll()

        var items = emptyList<Long>()
        val items2 = hashMapOf<String, Long>()
        val users = hashMapOf<String, Long>()
        var ii = 0L
        var ui = 0L

        Files.readAllLines(File("ratings$DATASET.tsv").toPath()).forEachIndexed { i, it ->
            val columns = it.split("\t")
            println(columns)

            if (DATASET == 2) {
                val user = items2.getOrPut(columns[0]) { ++ii }
                val item = users.getOrPut(columns[2]) { ++ui }

                userActionRepository.add(
                    UserAction(
                        "",
                        user,
                        item,
                        ActionType.RATING.id,
                        LocalDateTime.now(),
                        0,
                        columns[1].toDouble()
                    )
                )
            } else {
                if (i == 0) {
                    items = columns
                        .subList(1, columns.size)
                        .map {
                            it.replace("\"", "")
                                .split(":")[0].toLong()
                        }
                    println(items)
                } else {
                    val user = columns[0].toLong()
                    columns.subList(1, columns.size).forEachIndexed { index, r ->
                        val v = if (r.isBlank()) 0.0 else r.toDouble()
                        if (v > 0.0) {
                            userActionRepository.add(
                                UserAction(
                                    "",
                                    user,
                                    items[index],
                                    ActionType.RATING.id,
                                    LocalDateTime.now(),
                                    0,
                                    if (r.isBlank()) 0.0 else r.toDouble()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}