package com.example.account_book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen() {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var chatList by remember { mutableStateOf(listOf<ChatMessage>()) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(chatList.size) {
        // 当有新消息时自动滑动到底部
        if (chatList.isNotEmpty()) {
            scrollState.animateScrollToItem(chatList.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI助手", fontSize = 20.sp) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // 消息列表
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                state = scrollState
            ) {
                items(chatList) { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (msg.isUser) Color(0xFFDCF8C6) else Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(msg.text, fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }
            }

            // 输入栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("请输入文本") },
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val userMsg = inputText.text.trim()
                        if (userMsg.isNotEmpty()) {
                            // 新用户消息
                            chatList = chatList + ChatMessage(userMsg, isUser = true)
                            inputText = TextFieldValue("")

                            // 可以异步调用AI，这里先假设立刻回复一个示例
                            chatList = chatList + ChatMessage(
                                text = getAiReply(userMsg),
                                isUser = false
                            )
                        }
                    }
                ) {
                    Text("发送")
                }
            }
        }
    }
}


// 示例：模拟AI回复
fun getAiReply(userInput: String): String {
    // 这里未来可以对接真实API
    // 先用固定回复做演示
    return when {
        userInput.contains("你好") -> "你好，我是账本AI，有什么可以帮您？"
        userInput.contains("记账") -> "你可以告诉我花了多少钱、买了什么，我帮你记账哦。"
        else -> "我已收到: \"$userInput\""
    }
}