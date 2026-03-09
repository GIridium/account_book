// app/src/main/java/com/example/account_book/utils/Extensions.kt
package com.example.account_book.utils

import java.util.*

fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@toCalendar
    }
}