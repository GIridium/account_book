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
import com.example.account_book.data.BudgetRepository // 新增导入
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
    originBudgets: List<Budget>,
    onSave: (List<Budget>) -> Unit,
    budgets: List<Budget>,
    categoryOptions: List<Category>,
    onDismiss: () -> Unit,
    onBudgetsChange: (List<Budget>) -> Unit
) {
    // 注意：budgetList用不可变list结构
    var budgetList by remember { mutableStateOf(budgets) }
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
                                // 保存到全局Repository
                                BudgetRepository.setBudgets(budgetList)
                                onBudgetsChange(budgetList)
                            },
                            label = { Text("额度") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp)
                        )
                        IconButton(onClick = {
                            budgetList = budgetList.filterIndexed { i, _ -> i != idx }
                            BudgetRepository.setBudgets(budgetList)
                            onBudgetsChange(budgetList)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Divider()
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
                                    .menuAnchor()
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
                                    // 新增预算，重新生成新列表
                                    budgetList = budgetList + Budget(categoryName, limitNum, 0f)
                                    BudgetRepository.setBudgets(budgetList)
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
                BudgetRepository.setBudgets(budgetList)
                onSave(budgetList)
                onDismiss()
            }) { Text("完成") }
        }
    )

}