// app/src/main/java/com/example/account_book/ui/theme/Color.kt
package com.example.account_book.ui.theme

import androidx.compose.ui.graphics.Color

// 保留原有的颜色
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// 添加蓝色调色板 - 降低饱和度，增加白天模式舒适度
val BluePrimary = Color(0xFF5B9BD5)          // 降低饱和度的主蓝色
val BlueLight = Color(0xFFD4E6F1)            // 调整浅蓝色，更柔和
val BlueExtraLight = Color(0xFFECF2F8)       // 调整极浅蓝（背景色），更浅更柔和
val BlueDark = Color(0xFF2E5C8A)             // 调整深蓝色，降低对比度
val Teal = Color(0xFF4CA6A6)                 // 降低饱和度的青绿色

// 收入和支出颜色 - 白天模式降低饱和度
val IncomeGreen = Color(0xFF6BA86B)          // 降低饱和度的收入绿色
val ExpenseRed = Color(0xFFE07070)           // 降低饱和度的支出红色