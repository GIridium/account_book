// app/src/main/java/com/example/account_book/MainActivity.kt
package com.example.account_book

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.account_book.data.TransactionRepository
import com.example.account_book.model.Transaction
import com.example.account_book.model.TransactionType
import com.example.account_book.ui.theme.AccountBookTheme
import com.example.account_book.ui.theme.ExpenseRed
import com.example.account_book.ui.theme.IncomeGreen
import com.example.account_book.utils.toCalendar
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.example.account_book.model.Budget
import com.example.account_book.model.Category
import com.example.account_book.ui.BudgetEditDialog
import com.example.account_book.data.BudgetRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.Add // 引入加号图标


// 时间筛选枚举
enum class TimeFilter {
    TODAY, WEEK, MONTH, CUSTOM
}

// 底部导航项数据类
data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Int
)


// 主题状态管理
object ThemeState {
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by ThemeState.isDarkMode
            AccountBookTheme(
                darkTheme = isDarkMode,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccountBookApp()
                }
            }
        }
    }
}

@Composable
fun AccountBookApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    MainScreen(
                        onNavigateToAddTransaction = {
                            navController.navigate("add_transaction")
                        },
                        onNavigateToEditTransaction = { transactionId ->
                            navController.navigate("add_transaction?id=$transactionId")
                        }
                    )
                }
                composable("account_management") {
                    AccountBookManagementScreen()
                }
                composable("ai_chat") {
                    AiChatScreen()
                }
                composable("profile") {
                    ProfileScreen()
                }
                composable(
                    route = "add_transaction?id={id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id")
                    val transactionId = if (id != null && id != -1L) id else null
                    AddTransactionScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        transactionId = transactionId
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(title = "首页", route = "home", icon = android.R.drawable.ic_menu_gallery),
        BottomNavItem(title = "账本", route = "account_management", icon = android.R.drawable.ic_menu_edit),
        BottomNavItem(title = "AI", route = "ai_chat", icon = android.R.drawable.ic_menu_search),
        BottomNavItem(title = "我的", route = "profile", icon = android.R.drawable.ic_menu_my_calendar)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit
) {
    // 模拟从ViewModel获取数据
    var budgets by remember { mutableStateOf<List<Budget>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        // Normalize category names (e.g. "其他" -> "其他(支出)") to avoid duplicates
        // Use spent from backend response
        budgets = BudgetRepository.getBudgets().map { b ->
            val cats = Category.entries.filter { it.displayName == b.category }
            val uniqueName = if (cats.size == 1) {
                cats.first().uniqueDisplayName
            } else if (b.category == "其他") {
                Category.OTHER.uniqueDisplayName // Default to Expense if ambiguous
            } else {
                b.category
            }
            b.copy(category = uniqueName)
        }
    }

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    // 3. 首页启动时先显示“当前余额”，使用 rememberSaveable 保持状态
    var isBudgetView by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    var selectedFilter by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(TimeFilter.MONTH) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var customStartDate by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<Date?>(null) }
    var customEndDate by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<Date?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val scope = rememberCoroutineScope()

    // Refresh when screen becomes active
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val scrollState = rememberScrollState()
    val transactions by produceState<List<Transaction>>(
        initialValue = emptyList(),
        key1 = selectedFilter,
        key2 = refreshTrigger,
        key3 = customStartDate to customEndDate
    ) {
        value = try {
            when (selectedFilter) {
                TimeFilter.TODAY -> TransactionRepository.getTodayTransactions()
                TimeFilter.WEEK -> TransactionRepository.getThisWeekTransactions()
                TimeFilter.MONTH -> TransactionRepository.getThisMonthTransactions()
                TimeFilter.CUSTOM -> {
                    if (customStartDate != null && customEndDate != null) {
                        TransactionRepository.getCustomRangeTransactions(customStartDate!!, customEndDate!!)
                    } else {
                        TransactionRepository.getThisMonthTransactions()
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 专门用于预算计算的本月数据，确保预算始终显示本月进度，不受上方时间筛选器影响
    val monthTransactions by produceState<List<Transaction>>(
        initialValue = emptyList(),
        key1 = refreshTrigger
    ) {
        value = try {
            TransactionRepository.getThisMonthTransactions()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val summary = TransactionRepository.getSummary(transactions)
    val balance = summary.totalIncome - summary.totalExpense
    val expenseByCategory = if (selectedFilter == TimeFilter.MONTH) {
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category.displayName }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
    } else {
        emptyMap()
    }

    // ======================= UI ============================
    // 使用 Box 作为根容器，以便可以将 FAB 固定在右下角
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 20.dp)
                .padding(bottom = 80.dp), // 增加底部内边距，防止最后的内容被 FAB 遮挡
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "欢迎回来",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 40.sp
                    )
                    Text(
                        text = "记录每一笔收支",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box {
                    Button(
                        onClick = { showFilterMenu = true },
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = when (selectedFilter) {
                                TimeFilter.TODAY -> "今天"
                                TimeFilter.WEEK -> "本周"
                                TimeFilter.MONTH -> "本月"
                                TimeFilter.CUSTOM -> "自定义"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "今天") },
                            onClick = {
                                selectedFilter = TimeFilter.TODAY
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "本周") },
                            onClick = {
                                selectedFilter = TimeFilter.WEEK
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "本月") },
                            onClick = {
                                selectedFilter = TimeFilter.MONTH
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "自定义") },
                            onClick = {
                                showCustomDatePicker = true
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // ======= 预算/余额卡片切换 =======
            if (isBudgetView) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .padding(24.dp)
                                .align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = "本月预算",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.height(16.dp))
                            budgets.forEach { budget ->
                                // 实时计算已用金额：从本月交易记录中筛选出对应分类的交易并求和
                                val used = monthTransactions
                                    .filter { it.category.uniqueDisplayName == budget.category }
                                    .sumOf { it.amount }
                                    .toFloat()

                                val cat = Category.entries.find { it.uniqueDisplayName == budget.category } ?: Category.OTHER
                                val isIncome = cat.type == TransactionType.INCOME

                                Text(
                                    "${budget.category}   已${used}/限${budget.limit}  (${(if (budget.limit > 0) 100 * used / budget.limit else 0f).toInt()}%)",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 15.sp
                                )
                                LinearProgressIndicator(
                                    progress = { if (budget.limit > 0) (used / budget.limit).coerceIn(0f, 1f) else 0f },
                                    color = if (isIncome) IncomeGreen else ExpenseRed,
                                    trackColor = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                )
                                val remain = (budget.limit - used).coerceAtLeast(0f)
                                Text(
                                    "剩余${remain} (${(if (budget.limit > 0) 100 * remain / budget.limit else 0f).toInt()}%)",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                            Button(
                                onClick = { showEditBudgetDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .align(Alignment.End)
                            ) { Text("编辑预算", fontSize = 14.sp) }
                        }

                        // 右上角切换按钮
                        OutlinedButton(
                            onClick = { isBudgetView = !isBudgetView },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = androidx.compose.ui.graphics.Color.White
                            )
                        ) {
                            Text("显示余额")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .padding(24.dp)
                                .align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = "总余额",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format("¥ %.2f", balance),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // 收入
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.arrow_up_float),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = String.format("+%.2f", summary.totalIncome),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "收入",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                                // 支出
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.arrow_down_float),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = String.format("-%.2f", summary.totalExpense),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "支出",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            // Button removed here as requested
                        }

                        // 右上角切换按钮
                        OutlinedButton(
                            onClick = { isBudgetView = !isBudgetView },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = androidx.compose.ui.graphics.Color.White
                            )
                        ) {
                            Text("显示预算")
                        }
                    }
                }
            }

            // 预算编辑对话框
            BudgetEditDialog(
                visible = showEditBudgetDialog,
                onSave = { newBudgets ->
                    scope.launch {
                        BudgetRepository.syncBudgets(newBudgets) // 保存预算
                        budgets = newBudgets                     // Compose状态更新
                    }
                },
                budgets = budgets,
                categoryOptions = Category.entries.toList(),
                onDismiss = { showEditBudgetDialog = false },
                onBudgetsChange = {
                    budgets = it
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 统计卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp)
                ) {
                    Text(
                        text = "本月统计",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    // 收入统计
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(IncomeGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "收入",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = String.format("+¥ %.2f", summary.totalIncome),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // 支出统计
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ExpenseRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "支出",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = String.format("-¥ %.2f", summary.totalExpense),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "结余",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format("¥ %.2f", balance),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balance >= 0) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // 分类支出统计
            if (selectedFilter == TimeFilter.MONTH && expenseByCategory.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    ) {
                        Text(
                            text = "支出分类统计",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        val totalExpense = summary.totalExpense
                        expenseByCategory.forEach { (category, amount) ->
                            val percentage = (amount / totalExpense * 100).toInt()
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = String.format("%d%%", percentage),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(percentage / 100f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ExpenseRed)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            // 交易列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp)
                ) {
                    Text(
                        text = "交易记录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无记录",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        val sortedTransactions = transactions.sortedByDescending { it.date }
                        sortedTransactions.forEachIndexed { index, transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onClick = { onNavigateToEditTransaction(transaction.id) },
                                onDeleteClick = { transactionToDelete = transaction }
                            )
                            if (index < sortedTransactions.size - 1) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 【已移除】原来的底部大按钮
            // 这里不再放置 Button，而是交给外层的 Box 处理 FloatingActionButton

            Text(
                text = "努力赚钱，好好记账",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 新增：固定在右下角的浮动按钮 (FAB)
        // padding 值根据底部导航栏高度和屏幕边缘调整
        FloatingActionButton(
            onClick = onNavigateToAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp), // bottom=80dp 是为了避开底部导航栏
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "记账",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (transactionToDelete != null) {
        val transaction = transactionToDelete!!
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            TransactionRepository.deleteTransaction(transaction.id)
                            refreshTrigger++
                            transactionToDelete = null
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    // 自定义日期范围选择器
    if (showCustomDatePicker) {
        CustomDateRangeDialog(
            onDismiss = { showCustomDatePicker = false },
            onConfirm = { start, end ->
                customStartDate = start
                customEndDate = end
                selectedFilter = TimeFilter.CUSTOM
                showCustomDatePicker = false
            }
        )
    }

}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isExpense)
                        ExpenseRed.copy(alpha = 0.1f)
                    else
                        IncomeGreen.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = transaction.category.displayName.take(1),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) ExpenseRed else IncomeGreen
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.category.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = dateFormat.format(transaction.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            text = String.format("%s%.2f", if (isExpense) "-" else "+", transaction.amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isExpense) ExpenseRed else IncomeGreen
        )
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (start: Date, end: Date) -> Unit
) {
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "选择日期范围") },
        text = {
            Column {
                Text(text = "开始日期", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showStartPicker = true }
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(all = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = dateFormat.format(startDate))
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_today),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "结束日期", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showEndPicker = true }
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(all = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = dateFormat.format(endDate))
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_today),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.time = startDate
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val start = calendar.time
                    calendar.time = endDate
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val end = calendar.time
                    onConfirm(start, end)
                }
            ) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        }
    )

    if (showStartPicker) {
        val calendar = startDate.toCalendar()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                startDate = newCalendar.time
                showStartPicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showEndPicker) {
        val calendar = endDate.toCalendar()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                endDate = newCalendar.time
                showEndPicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

}

@Composable
fun HomeScreen(
    budgetList: List<Budget>,
    onSetBudgetClick: () -> Unit,
    totalLimit: Float,
    totalSpent: Float
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = onSetBudgetClick) {
            Text("设置预算")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 总预算
        BudgetProgressBar("总预算", totalLimit, totalSpent)

        Spacer(modifier = Modifier.height(8.dp))

        // 分类预算
        budgetList.forEach { budget ->
            BudgetProgressBar(budget.category, budget.limit, budget.spent)
        }
    }

}



//// 占位函数，防止编译错误（假设这些在其他文件中定义）
//@Composable
//fun AccountBookManagementScreen() { Text("账本管理") }
//@Composable
//fun AiChatScreen() { Text("AI 聊天") }
//@Composable
//fun ProfileScreen() { Text("个人中心") }
//@Composable
//fun AddTransactionScreen(onNavigateBack: () -> Unit, transactionId: Long?) { Text("添加/编辑交易") }
//@Composable
//fun BudgetProgressBar(category: String, limit: Float, spent: Float) {
//    Column {
//        Text("$category: $spent / $limit")
//        LinearProgressIndicator(progress = (spent/limit).coerceIn(0f, 1f))
//    }
//}