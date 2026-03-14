// app/src/main/java/com/example/account_book/AccountBookManagementScreen.kt
package com.example.account_book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.account_book.data.AccountRepository
import com.example.account_book.model.Account
import com.example.account_book.model.getColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBookManagementScreen() {
    val scrollState = rememberScrollState()
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf(AccountRepository.getCurrentAccount()) }
    var accountList by remember { mutableStateOf(AccountRepository.getAllAccounts()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部栏
        TopAppBar(
            title = {
                Text(
                    "账本管理",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 当前账本卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        "当前账本",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(selectedAccount.getColor())
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    selectedAccount.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (selectedAccount.isDefault) {
                                    Text(
                                        "默认账本",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 账本列表标题
            Text(
                "我的账本",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 账本列表
            accountList.forEach { account ->
                AccountCard(
                    account = account,
                    isSelected = account.id == selectedAccount.id,
                    onSelect = {
                        selectedAccount = account
                        AccountRepository.setCurrentAccount(account.id)
                    },
                    onEdit = {
                        showAddAccountDialog = true
                    },
                    onDelete = {
                        if (AccountRepository.deleteAccount(account.id)) {
                            accountList = AccountRepository.getAllAccounts()
                            selectedAccount = AccountRepository.getCurrentAccount()
                        }
                    }
                )
            }

            // 添加账本按钮
            Button(
                onClick = { showAddAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_add),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "添加账本",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // 添加账本对话框
    if (showAddAccountDialog) {
        AddAccountDialog(
            onDismiss = { showAddAccountDialog = false },
            onConfirm = { name, colorValue ->
                AccountRepository.addAccount(name, colorValue)
                accountList = AccountRepository.getAllAccounts()
                showAddAccountDialog = false
            }
        )
    }
}

@Composable
fun AccountCard(
    account: Account,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(account.getColor())
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        account.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (account.isDefault) {
                        Text(
                            "默认账本",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (isSelected) {
                        Text(
                            "当前使用",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!account.isDefault) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "删除账本",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorValue: Long) -> Unit
) {
    var accountName by remember { mutableStateOf("新账本") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val predefinedColors = listOf(
        0xFF5B9BD5 to "蓝色",
        0xFF4CA6A6 to "青绿色",
        0xFF6BA86B to "绿色",
        0xFFE07070 to "红色",
        0xFFD4A574 to "褐色",
        0xFF9B7CB8 to "紫色",
        0xFF80A0C2 to "灰蓝",
        0xFFC4A58E to "沙色"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "新建账本",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 账本名称输入
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { if (it.length <= 20) accountName = it },
                    label = { Text("账本名称") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    supportingText = {
                        Text("${accountName.length}/20")
                    }
                )

                // 颜色选择
                Column {
                    Text(
                        "选择颜色",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 颜色选择网格
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (rowIndex in predefinedColors.indices step 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (colIndex in 0 until 4) {
                                    val colorIndex = rowIndex + colIndex
                                    if (colorIndex < predefinedColors.size) {
                                        val (colorValue, colorName) = predefinedColors[colorIndex]
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(colorValue))
                                                .clickable {
                                                    selectedColorIndex = colorIndex
                                                }
                                                .border(
                                                    width = if (selectedColorIndex == colorIndex) 3.dp else 0.dp,
                                                    color = if (selectedColorIndex == colorIndex)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        Color.Transparent,
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (selectedColorIndex == colorIndex) {
                                                Icon(
                                                    painter = painterResource(
                                                        id = android.R.drawable.ic_menu_view
                                                    ),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(56.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (accountName.isNotBlank()) {
                        onConfirm(accountName, predefinedColors[selectedColorIndex].first)
                    }
                }
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// 辅助函数
fun Modifier.border(
    width: androidx.compose.ui.unit.Dp,
    color: Color,
    shape: RoundedCornerShape
): Modifier {
    return this.then(
        Modifier.background(
            color = color,
            shape = shape
        )
    )
}