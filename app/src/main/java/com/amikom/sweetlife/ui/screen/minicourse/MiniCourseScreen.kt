// package com.amikom.sweetlife.ui.screen.minicourse

package com.amikom.sweetlife.ui.screen.minicourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniCourseScreen(viewModel: MiniCourseViewModel = hiltViewModel()) {
    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.uiState.collectAsState() // Amati uiState dari ViewModel

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mini Course") })
        }
    ) { safeDrawingPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeDrawingPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.courses.isEmpty() -> { // Tampilkan pesan jika data kosong (setelah loading dan tidak error)
                    Text(
                        text = "Tidak ada mini course yang tersedia.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> { // Data tersedia, tampilkan daftar
                    LazyColumn {
                        items(uiState.courses) { course ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { uriHandler.openUri(course.url) } // Gunakan 'url' untuk link
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AsyncImage(
                                        model = course.image, // Gunakan 'image' untuk URL gambar
                                        contentDescription = course.title,
                                        modifier = Modifier.size(100.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(1f) // Agar kolom teks mengisi sisa ruang
                                    ) {
                                        Text(course.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            course.description,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}