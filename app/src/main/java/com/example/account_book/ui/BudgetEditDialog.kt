package com.example.account_book.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.account_book.model.Budget
import com.example.account_book.model.Category
import kotlin.collections.filterIndexed
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.mapIndexed
import kotlin.collections.plus
import kotlin.text.isNotBlank
import kotlin.text.toFloatOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetEditDialog(
    visible: Boolean,
    onSave: (List<Budget>) -> Unit,
    budgets: List<Budget>,
    categoryOptions: List<Category>,
    onDismiss: () -> Unit,
    onBudgetsChange: (List<Budget>) -> Unit
) {
    // 注意：budgetList用不可变list结构
    var budgetList by remember { mutableStateOf(budgets) }

    // Sync budgetList with incoming budgets or when dialog becomes visible
    LaunchedEffect(visible, budgets) {
        if (visible) {
            budgetList = budgets
        }
    }

    var adding by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Category.OTHER) }
    var inputLimit by remember { mutableStateOf("") }

    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置预算") },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                // 已有预算列表
                budgetList.forEachIndexed { idx, b ->
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = b.category, // Budget是String类型
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = b.limit.toString(),
                            onValueChange = { input ->
                                val v = input.toFloatOrNull() ?: 0f
                                // 更新额度：重新构造新列表
                                budgetList = budgetList.mapIndexed { i, item ->
                                    if (i == idx) item.copy(limit = v) else item
                                }
                                onBudgetsChange(budgetList)
                            },
                            label = { Text("额度") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp)
                        )
                        IconButton(onClick = {
                            budgetList = budgetList.filterIndexed { i, _ -> i != idx }
                            onBudgetsChange(budgetList)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                HorizontalDivider()
                // 新增预算
                if (adding) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 分类下拉菜单
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1.5f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedCategory.uniqueDisplayName,
                                onValueChange = {},
                                label = { Text("分类") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categoryOptions.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.uniqueDisplayName) },
                                        onClick = {
                                            selectedCategory = cat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = inputLimit,
                            onValueChange = { inputLimit = it },
                            label = { Text("额度") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp)
                        )
                        Button(
                            onClick = {
                                val limitNum = inputLimit.toFloatOrNull() ?: 0f
                                val categoryName = selectedCategory.uniqueDisplayName
                                if (categoryName.isNotBlank() && limitNum > 0) {
                                    // 检查是否存在同名分类
                                    val existingIndex = budgetList.indexOfFirst { it.category == categoryName }
                                    if (existingIndex != -1) {
                                        // 存在则更新
                                        budgetList = budgetList.mapIndexed { index, budget ->
                                            if (index == existingIndex) budget.copy(limit = limitNum) else budget
                                        }
                                    } else {
                                        // 不存在则新增
                                        budgetList = budgetList + Budget(categoryName, limitNum, 0f)
                                    }
                                    onBudgetsChange(budgetList)
                                    inputLimit = ""
                                    adding = false
                                }
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) { Text("添加") }
                    }
                } else {
                    Button(
                        onClick = {
                            adding = true
                            inputLimit = ""
                            selectedCategory = categoryOptions.firstOrNull() ?: Category.OTHER
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) { Text("新增预算") }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(budgetList)
                onDismiss()
            }) { Text("完成") }
        }
    )

}