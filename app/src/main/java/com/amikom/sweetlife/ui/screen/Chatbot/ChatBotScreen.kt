package com.amikom.sweetlife.ui.screen.Chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat list
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            chatHistory.forEach { chat ->
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = timeFormat.format(Date(chat.timestamp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
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
                            text = time, // tambahkan timestamp di model
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (isBotTyping) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(message)
                        message = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
