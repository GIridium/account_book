package com.example.account_book
//预算设置界面
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.ranges.coerceIn
import kotlin.text.format

@Composable
fun BudgetProgressBar(category: String, limit: Float, spent: Float) {
    Column(Modifier.padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$category")
            Text("￥%.2f/￥%.2f".format(spent, limit))
        }
        LinearProgressIndicator(
            progress = (spent / limit).coerceIn(0f, 1f), // 比例在0~1之间
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}