package com.gmail.marcosav2010.model

import kotlinx.serialization.Serializable
import java.time.Period

@Serializable
data class PopularItem(val item: Long, val amount: Long, val action: Int, val type: Int, val value: Double?)

enum class RankType(val id: Int, val period: Period) {
    WEEKLY(0, Period.ofWeeks(1)),
    MONTHLY(1, Period.ofMonths(1));

    companion object {
        fun from(id: Int) = values().find { it.id == id }
    }
}