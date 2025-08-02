package com.amikom.sweetlife.ui.screen.grocery

import android.net.Uri
import androidx.compose.foundation.Image
import com.amikom.sweetlife.domain.nvgraph.Route.GroceryDetailScreen
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.amikom.sweetlife.R
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.util.Constants
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(viewModel: GroceryViewModel = hiltViewModel(), navController: NavController ) {
    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.uiState.collectAsState()

    // Ambil tombol bottom nav dengan benar
    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController) // <<< DEKLARASI INI YANG HILANG!

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Grocery") })
        },
        bottomBar = {
            BottomNavigationBar(
                buttons = buttons,
                navController = navController,
                currentScreen = Route.GroceryScreen
            )
        },
        modifier = Modifier.fillMaxSize()
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
                uiState.grocery.isEmpty() -> {
                    Text(
                        text = "Tidak ada produk yang tersedia.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.grocery) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val productJson = Uri.encode(Gson().toJson(product))
                                        navController.navigate(GroceryDetailScreen(productId = product.id))
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                ) {
                                    AsyncImage(
                                        model = product.image,
                                        contentDescription = product.title,
                                        modifier = Modifier.size(64.dp),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(id = R.drawable.bapak),
                                        error = painterResource(id = R.drawable.bapak)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            product.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "Rp. ${product.price}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = product.description.shorten(100),
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

fun String.shorten(maxLength: Int): String {
    return if (this.length > maxLength) {
        "${this.take(maxLength)}..."
    } else {
        this
    }
}