package com.gokhanaytekinn.sdandroid.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatDate(dateStr: String?): String {
        if (dateStr == null) return ""
        return try {
            val date = LocalDate.parse(dateStr)
            date.format(formatter)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun calculateDaysRemaining(dateStr: String?): Long {
        if (dateStr == null) return -1
        return try {
            val targetDate = LocalDate.parse(dateStr)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, targetDate)
        } catch (e: Exception) {
            -1
        }
    }

    fun isWithinNextDays(dateStr: String?, days: Int): Boolean {
        val remaining = calculateDaysRemaining(dateStr)
        return remaining in 0..days.toLong()
    }
}
