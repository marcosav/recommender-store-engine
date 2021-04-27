package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime

class ActionPopulationService(di: DI) {

    companion object {
        private const val DATASET = 1
    }

    private val userActionRepository by di.instance<UserActionRepository>()

    fun setup() {
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