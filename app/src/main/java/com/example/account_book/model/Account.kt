// app/src/main/java/com/example/account_book/model/Account.kt
package com.example.account_book.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Account(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "默认账本",
    val colorValue: Long = 0xFF5B9BD5,  // 默认蓝色
    val isDefault: Boolean = false
) : Parcelable

fun Account.getColor(): Color = Color(colorValue)