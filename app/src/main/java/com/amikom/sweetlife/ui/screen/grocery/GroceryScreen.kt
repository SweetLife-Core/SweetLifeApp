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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.amikom.sweetlife.R
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.util.Constants

data class GroceryProduct(
    val name: String,
    val price: String,
    val description: String,
    val photoUrl: String,
    val marketplaceUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(products: List<GroceryProduct> = sampleProducts(), navController: NavController ) {
    val uriHandler = LocalUriHandler.current
    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController)
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
                        AsyncImage(
                            model = product.photoUrl,
                            contentDescription = product.name,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.bapak),
                            error = painterResource(id = R.drawable.bapak)
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
            name = "East Java & Co Organic Brown Rice – 1 kg",
            price = "Rp 56 000",
            description = "Organic brown rice, single origin dari lereng Gunung Merapi, bersertifikat organik & halal.",
            photoUrl = "https://down-id.img.susercontent.com/file/sg-11134201-7rfg7-m44dd51rm8hce8.webp",
            marketplaceUrl = "https://shopee.co.id/RHT-Beras-Coklat-Organik-Premium-5Kg-Brown-Rice-RHT-Bersertifikat-Untuk-Diet-i.189034852.29071418190?sp_atk=93cf6908-506b-4ecf-8812-79c2f67bba6e&xptdk=93cf6908-506b-4ecf-8812-79c2f67bba6e"
        ),
GroceryProduct(
name = "Extra Virgin Olive Oil 500 ml",
price = "Rp 204 000",
description = "Minyak zaitun extra virgin, cold‑pressed, ideal untuk salad dan masakan sehat.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134207-7rbkc-m92voghrmo0t7c.webp",
marketplaceUrl = "https://shopee.co.id/Smart-Organik-Extra-Virgin-Olive-Oil-Minyak-Zaitun-EVOO-Murni-Pure-100-Original-i.1137846099.28621447822?sp_atk=e2d3ca6c-a43b-4e30-ba87-8432db1a62cb&xptdk=e2d3ca6c-a43b-4e30-ba87-8432db1a62cb"
),
GroceryProduct(
name = "Organic Quinoa 500 g",
price = "Rp 85 000",
description = "Quinoa organik tinggi protein dan serat, cocok untuk diet sehat.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134207-7rbkc-mawx32xtm4k970@resize_w900_nl.webp",
marketplaceUrl = "https://shopee.co.id/White-Quinoa-Organic-Quinoa-Organic-Putih-REPACK-i.300607928.43704299456?sp_atk=cc09c05e-75e1-4902-aea2-41a65e27b34d&xptdk=cc09c05e-75e1-4902-aea2-41a65e27b34d"
),
GroceryProduct(
name = "Unsweetened Almond Milk 1 L",
price = "Rp 45 000",
description = "Susu almond tanpa pemanis, cocok untuk vegan dan diet keto.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134258-7r98w-lyezzaurybot13",
marketplaceUrl = "https://shopee.co.id/AROMALAB-Almond-Milk-Chocolate-Susu-Almond-Bubuk-Rasa-Coklat-i.1525458707.43101609995?sp_atk=d8a0cd44-2661-4656-85ed-13a980a62756&xptdk=d8a0cd44-2661-4656-85ed-13a980a62756"
),
GroceryProduct(
name = "Rolled Oats 1 kg",
price = "Rp 35 000",
description = "Oatmeal utuh tinggi serat, baik untuk sarapan sehat.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134258-7rbk6-m7byijxbeg4ed7",
marketplaceUrl = "https://tokopedia.com/product/rolled-oats"
),
GroceryProduct(
name = "Natural Peanut Butter 350 g",
price = "Rp 49 000",
description = "Selai kacang alami tanpa gula tambahan & tanpa minyak sawit.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134258-7r98w-lyezzaurybot13",
marketplaceUrl = "https://shopee.com/product/peanutbutter-natural"
),
GroceryProduct(
name = "Chia Seeds 250 g",
price = "Rp 55 000",
description = "Biji chia kaya omega‑3 & serat, cocok untuk topping smoothie.",
photoUrl = "https://down-id.img.susercontent.com/file/id-11134258-7rbkc-m7bygnhputy84f",
marketplaceUrl = "https://tokopedia.com/product/chia-seeds"
),
GroceryProduct(
name = "Organic Coconut Sugar 500 g",
price = "Rp 42 000",
description = "Gula kelapa alami rendah indeks glikemik.",
photoUrl = "https://example.com/images/coconutsugar.jpg",
marketplaceUrl = "https://shopee.com/product/coconut-sugar"
),
GroceryProduct(
name = "Low Fat Greek Yogurt 500 ml",
price = "Rp 65 000",
description = "Yogurt rendah lemak, tinggi protein untuk diet & workout.",
photoUrl = "https://example.com/images/greekyogurt.jpg",
marketplaceUrl = "https://tokopedia.com/product/greek-yogurt"
),
GroceryProduct(
name = "Whole Wheat Pasta 500 g",
price = "Rp 38 000",
description = "Pasta gandum utuh untuk menu sehat & rendah kalori.",
photoUrl = "https://example.com/images/wholewheatpasta.jpg",
marketplaceUrl = "https://shopee.com/product/wholewheatpasta"
),
GroceryProduct(
name = "Granola Almond Honey 400 g",
price = "Rp 60 000",
description = "Granola sehat dengan almond, madu, dan biji-bijian.",
photoUrl = "https://example.com/images/granolaalmond.jpg",
marketplaceUrl = "https://tokopedia.com/product/granola-almond"
),
GroceryProduct(
name = "Matcha Green Tea Powder 100 g",
price = "Rp 70 000",
description = "Bubuk teh hijau matcha premium untuk minuman & kue.",
photoUrl = "https://example.com/images/matchapowder.jpg",
marketplaceUrl = "https://shopee.com/product/matcha-powder"
),
GroceryProduct(
name = "Organic Kale 250 g",
price = "Rp 30 000",
description = "Sayuran kale segar organik kaya antioksidan.",
photoUrl = "https://example.com/images/kale.jpg",
marketplaceUrl = "https://tokopedia.com/product/kale-organik"
),
GroceryProduct(
name = "Avocado Hass 500 g",
price = "Rp 45 000",
description = "Alpukat Hass segar, tinggi lemak sehat.",
photoUrl = "https://example.com/images/avocado.jpg",
marketplaceUrl = "https://shopee.com/product/avocado-hass"
),
GroceryProduct(
name = "Sweet Potato 1 kg",
price = "Rp 25 000",
description = "Ubi manis segar kaya karbohidrat kompleks.",
photoUrl = "https://example.com/images/sweetpotato.jpg",
marketplaceUrl = "https://tokopedia.com/product/sweetpotato"
),
GroceryProduct(
name = "Organic Baby Spinach 200 g",
price = "Rp 32 000",
description = "Bayam baby organik segar, cocok untuk salad.",
photoUrl = "https://example.com/images/spinach.jpg",
marketplaceUrl = "https://shopee.com/product/baby-spinach"
),
GroceryProduct(
name = "Egg Free Noodles 300 g",
price = "Rp 20 000",
description = "Mie sehat tanpa telur, rendah kolesterol.",
photoUrl = "https://example.com/images/eggfree-noodles.jpg",
marketplaceUrl = "https://tokopedia.com/product/egg-free-noodles"
),
GroceryProduct(
name = "Tofu Organik 300 g",
price = "Rp 18 000",
description = "Tahu organik kaya protein nabati.",
photoUrl = "https://example.com/images/tofu.jpg",
marketplaceUrl = "https://shopee.com/product/tofu-organik"
),
GroceryProduct(
name = "Tempe Kedelai Non-GMO 250 g",
price = "Rp 15 000",
description = "Tempe fermentasi alami dari kedelai non-GMO.",
photoUrl = "https://example.com/images/tempe.jpg",
marketplaceUrl = "https://tokopedia.com/product/tempe-nongmo"
),
GroceryProduct(
name = "Himalayan Pink Salt 200 g",
price = "Rp 29 000",
description = "Garam Himalaya alami kaya mineral.",
photoUrl = "https://example.com/images/pinksalt.jpg",
marketplaceUrl = "https://shopee.com/product/himalayan-pink-salt"
)

)