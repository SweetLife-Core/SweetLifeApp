package com.amikom.sweetlife.ui.screen.grocery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amikom.sweetlife.R

data class GroceryProduct(
    val name: String,
    val price: String,
    val description: String,
    val photoUrl: String,
    val marketplaceUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(products: List<GroceryProduct> = sampleProducts()) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Grocery") })
        }
    ) { safeDrawingPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeDrawingPadding)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )  {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        uriHandler.openUri(product.marketplaceUrl)
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        // Replace with AsyncImage if using Coil/Glide
                        Image(
                            painter = painterResource(id = R.drawable.bapak), // placeholder image
                            contentDescription = product.name,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(product.price, style = MaterialTheme.typography.bodyMedium)
                            Text(product.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

fun sampleProducts() = listOf(
    GroceryProduct(
        name = "Brown Rice",
        price = "Rp 30.000",
        description = "Healthy brown rice for your diet.",
        photoUrl = "",
        marketplaceUrl = "https://tokopedia.com"
    ),
    GroceryProduct(
        name = "Olive Oil",
        price = "Rp 50.000",
        description = "Extra virgin olive oil.",
        photoUrl = "",
        marketplaceUrl = "https://shopee.com"
    )
)