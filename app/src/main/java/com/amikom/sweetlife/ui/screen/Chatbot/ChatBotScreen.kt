package com.amikom.sweetlife.ui.screen.Chatbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatBotScreen(
    viewModel: ChatBotViewModel = hiltViewModel()
) {
    var message by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // List chat
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            chatHistory.forEach { chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (chat.isFromUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (chat.isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = chat.message,
                            modifier = Modifier
                                .padding(12.dp)
                                .widthIn(max = 250.dp),
                            color = if (chat.isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (message.isNotBlank()) {
                    viewModel.sendMessage(message)
                    message = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}
