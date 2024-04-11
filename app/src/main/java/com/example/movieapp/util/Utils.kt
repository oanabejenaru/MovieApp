package com.example.movieapp.util

import java.text.DecimalFormat

object Utils {
    fun formatNumberToOneDecimal(number: Float?) : String {
        val decimalFormat = DecimalFormat("#.#")
        return decimalFormat.format(number)
    }

    fun getYearFromDateString(dateString: String?): String {
        var yearString = "-"
        if (!dateString.isNullOrEmpty() && dateString.count() >= 4) {
            yearString = dateString.slice(IntRange(0, 3))
        }
        return yearString
    }
}