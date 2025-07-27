package com.amikom.sweetlife.ui.screen.Chatbot

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

@Composable
fun ChatBotScreen(
    viewModel: ChatBotViewModel = hiltViewModel()
) {
    var message by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isBotTyping by viewModel.isBotTyping.collectAsState() // opsional
    val listState = rememberLazyListState()

    LaunchedEffect(chatHistory.size) {
        listState.animateScrollToItem(chatHistory.size)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        // Chat list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatHistory.size) { index ->
                val chat = chatHistory[index]
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.format(Date(chat.timestamp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = if (chat.isFromUser) Arrangement.End else Arrangement.Start
                ) {
                    Column {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (chat.isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 1.dp
                        ) {
                            Text(
                                text = chat.message,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp),
                                color = if (chat.isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Show typing indicator
            if (isBotTyping) {
                item {
                    val infiniteTransition = rememberInfiniteTransition(label = "dotBlink")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0.3f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 900
                                1f at 0
                                0.3f at 450
                                1f at 900
                            },
                            repeatMode = RepeatMode.Restart
                        ), label = "dotAlpha"
                    )
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "...",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.5),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .alpha(alpha)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            tonalElevation = 3.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Ketik pesan...") },
                    shape = MaterialTheme.shapes.large,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp, max = 150.dp)
                        .padding(end = 8.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            viewModel.sendMessage(message)
                            message = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

    }
}
