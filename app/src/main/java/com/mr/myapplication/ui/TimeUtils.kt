package com.mr.myapplication.ui

import androidx.annotation.PluralsRes
import com.mr.myapplication.R
import java.util.*
import java.util.concurrent.TimeUnit


class TimeUtils {

    companion object {
        private val times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1)
        )

        private val timesString = Arrays.asList(
            R.plurals.years_plural, R.plurals.month_plural, R.plurals.week_plural,
            R.plurals.day_plural, R.plurals.hour_plural, R.plurals.minute_plural,
            R.plurals.second_plural
        )

        fun toDuration(duration: Long): Pair<Int, Int>? {
            for (i in times.indices) {
                val current = times.get(i)
                val temp = duration / current
                if (temp > 0) {
                    return Pair<Int, Int>(temp.toInt(), timesString[i])
                }
            }
            return null
        }
    }
}