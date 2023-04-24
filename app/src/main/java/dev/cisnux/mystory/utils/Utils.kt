package dev.cisnux.mystory.utils

import android.content.res.Configuration
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val BASE_URL = "https://story-api.dicoding.dev/v1/"

fun createSpannableText(text: String, isDarkModeActive: Int, start: Int, end: Int): Spannable {
    val spannableText = SpannableString(text)
    when (isDarkModeActive) {
        Configuration.UI_MODE_NIGHT_NO -> {
            spannableText.setSpan(
                ForegroundColorSpan(Color.BLUE),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        Configuration.UI_MODE_NIGHT_YES -> {
            spannableText.setSpan(
                ForegroundColorSpan(Color.CYAN),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    return spannableText
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}


fun String.isEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
    return Regex(emailRegex).matches(this)
}

fun String.isPasswordSecure(): Boolean = this.trim().length >= 8

