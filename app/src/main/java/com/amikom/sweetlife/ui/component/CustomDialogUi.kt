package com.amikom.sweetlife.ui.component

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.amikom.sweetlife.R
import com.amikom.sweetlife.ui.theme.MainBlue

@Composable
fun CustomDialog(
    openDialogCustom: MutableState<Boolean>,
    icon: Int? = null,
    title: String? = null,
    message: String? = null,
    buttons: List<Pair<String, () -> Unit>> = listOf(),
    dismissOnBackdropClick: Boolean = true
) {
    Dialog(
        onDismissRequest = {
            if (dismissOnBackdropClick) openDialogCustom.value = false
        }
    ) {
        CustomDialogUI(
            openDialogCustom = openDialogCustom,
            icon = icon,
            title = title,
            message = message,
            buttons = buttons
        )
    }
}

@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    openDialogCustom: MutableState<Boolean>,
    icon: Int? = null,
    title: String? = null,
    message: String? = null,
    buttons: List<Pair<String, () -> Unit>> = listOf()
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier.background(MaterialTheme.colorScheme.background)
        ) {
            // Ikon opsional
            icon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color = MainBlue),
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .height(70.dp)
                        .fillMaxWidth()
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Judul opsional
                title?.let {
                    Text(
                        color = Color.Gray,
                        text = it,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Pesan opsional
                message?.let {
                    Text(
                        color = Color.Gray,
                        text = it,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Tombol dinamis
            if (buttons.isNotEmpty()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(MainBlue),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    buttons.forEachIndexed { index, (buttonText, onClick) ->
                        androidx.compose.material3.TextButton(onClick = {
                            onClick() // Aksi tombol
                            openDialogCustom.value = false // Menutup dialog
                        }) {
                            val buttonColor = when (index) {
                                0 -> MaterialTheme.colorScheme.secondaryContainer
                                1 -> if (buttons.size == 2) MaterialTheme.colorScheme.onPrimary else Color.Red
                                2 -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.onSecondary
                            }

                            Text(
                                text = buttonText,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = buttonColor,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Dynamic Dialog Preview")
@Composable
fun CustomDialogPreview() {
    CustomDialog(
        openDialogCustom = mutableStateOf(true),
        icon = R.drawable.baseline_notifications_none_24,
        title = "Get Updates",
        message = "Allow Permission to send you notifications when new art styles added.",
        buttons = listOf(
            "Not Now" to { /* Handle Not Now */ },
            "Allow" to { /* Handle Allow */ }
        )
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Dynamic Dialog Preview Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CustomDialogPreviewDark() {
    CustomDialog(
        openDialogCustom = mutableStateOf(true),
        icon = R.drawable.baseline_notifications_none_24,
        title = "Get Updates",
        message = "Allow Permission to send you notifications when new art styles added.",
        buttons = listOf(
            "Not Now" to { /* Handle Not Now */ },
            "Allow" to { /* Handle Allow */ }
        )
    )
}
