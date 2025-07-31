package com.amikom.sweetlife.ui.screen.scan

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.AdditionalInfo
import com.amikom.sweetlife.data.model.AdditionalItem
import com.amikom.sweetlife.data.model.FoodRequest
import com.amikom.sweetlife.data.model.ScanItem
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.scan.FindFoodResponse
import com.amikom.sweetlife.data.remote.dto.scan.FoodListItem
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.CustomDialog
import com.google.gson.Gson
import java.util.Locale
import kotlin.math.ceil

@Composable
fun ResultAndAdditionalScreen(
    foodList: String,
    viewModel: ResultScanViewModel,
    navController: NavController
) {
    val foodListItems = remember(foodList) {
        Gson().fromJson(foodList, Array<FoodListItem>::class.java).toList()
    }

    var additionalList by remember { mutableStateOf(mutableListOf<AdditionalItem>()) }
    var showPopup by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }

    // Ini sudah benar, observeAsState untuk LiveData di Compose
    val findFoodData by viewModel.findFoodData.observeAsState()
    val saveFoodData by viewModel.saveFoodData.observeAsState()

    LaunchedEffect(foodListItems) {
        if (foodListItems.isEmpty()) {
            navController.navigate(Route.DashboardScreen) {
                popUpTo(Route.DashboardScreen) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // State untuk dialog sukses/gagal, ini sudah lumayan.
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(saveFoodData) {
        when (saveFoodData) {
            is Result.Success -> {
                showSuccessDialog = true
                viewModel.resetSaveState() // Reset state setelah berhasil atau gagal
                navController.navigate(Route.DashboardScreen) {
                    popUpTo(Route.DashboardScreen) { inclusive = true }
                    launchSingleTop = true
                }
                Toast.makeText(navController.context, "Success", Toast.LENGTH_SHORT).show()
            }
            is Result.Error -> {
                showErrorDialog = true
//                viewModel.resetSaveState() // Reset state setelah berhasil atau gagal
            }
            Result.Empty -> { // Tambahkan ini agar dialog tidak muncul tiba-tiba saat inisialisasi
                showSuccessDialog = false
                showErrorDialog = false
            }
            else -> {} // Jangan lakukan apa-apa untuk Result.Loading
        }
    }

    // Dialog Sukses
    if (showSuccessDialog) {
        CustomDialog(
            icon = R.drawable.baseline_check_circle_outline_24,
            title = "Success",
            message = "Your food data has been saved!",
            openDialogCustom = remember { mutableStateOf(true) }, // Ini udah diobservasi dari showSuccessDialog
            buttons = listOf(
                "Ok" to {
                    showSuccessDialog = false
                    navController.navigate(Route.DashboardScreen) {
                        popUpTo(Route.DashboardScreen) { inclusive = true } // popUpTo biar stacknya bersih
                        launchSingleTop = true
                    }
                }
            ),
            dismissOnBackdropClick = false
        )
    }

    // Dialog Error
    if (showErrorDialog) {
        CustomDialog(
            icon = R.drawable.baseline_info_outline_24,
            title = "Failed",
            message = "Can't save your food data",
            openDialogCustom = remember { mutableStateOf(true) }, // Sama, udah diobservasi
            buttons = listOf(
                "Try again" to {
                    showErrorDialog = false
                }
            ),
            dismissOnBackdropClick = false
        )
    }

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    if (!isUserLoggedIn) {
        CustomDialog(
            icon = R.drawable.baseline_info_outline_24,
            title = "Info",
            message = "Your session is ended. Please login again",
            openDialogCustom = remember { mutableStateOf(true) },
            buttons = listOf(
                "Ok" to {
                    navController.navigate(Route.LoginScreen) {
                        // Jangan cuma launchSingleTop, bersihkan back stack juga.
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ),
            dismissOnBackdropClick = false
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp, start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header - Result
            item {
                Text(
                    text = "Result",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // List of Food Items (Result Section)
            items(foodListItems) { item ->
                ExpandableItem(foodListItem = item)
            }

            // Header - Additional
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Additional",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = {
                        // Pastikan tidak ada proses saveFood yang sedang berjalan saat mau nambahin
                        if (saveFoodData !is Result.Loading) {
                            viewModel.resetFindState() // Reset state pencarian baru
                            searchText = "" // Reset teks pencarian
                            weightText = "" // Reset teks berat
                            showPopup = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add), // Pastikan ini icon yang benar
                            contentDescription = "Add Additional",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // List of Additional Items
            items(additionalList) { item ->
                ExpandableAdditionalItem(additionalItem = item)
            }
        }

        // Sticky Save Button
        Button(
            onClick = {
                val foodRequest = FoodRequest(
                    scan = foodListItems.map {
                        ScanItem(
                            it.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                                ?: "",
                            it.unit ?: 1
                        )
                    },
                    additionall = additionalList
                )

                // Panggil ViewModel untuk menyimpan data
                viewModel.saveFood(foodRequest)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.small,
            enabled = saveFoodData !is Result.Loading // Disable tombol saat loading
        ) {
            if (saveFoodData is Result.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Save")
            }
        }
    }

    // Popup for Additional Item Search
    if (showPopup) {
        AlertDialog(
            onDismissRequest = {
                if (findFoodData !is Result.Loading) { // Hanya bisa dismiss kalau tidak sedang loading
                    showPopup = false
                    viewModel.resetFindState() // Reset state pencarian saat dismiss
                }
            },
            title = { Text("Search Additional Food") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it.replaceFirstChar { char -> // Perbaiki Locale.ROOT
                                if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(
                                "Food Name",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(
                                "Weight (gram)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Tampilkan ringkasan nutrisi atau loading/error
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD8D8D8))
                            .padding(16.dp)
                    ) {
                        when (findFoodData) {
                            is Result.Success -> {
                                val foodListItem = (findFoodData as Result.Success<FindFoodResponse>).data.data
                                foodListItem?.let {
                                    Text(
                                        text = "Calorie\n${ceil(it.calories ?: 0.0).toInt()} kcal",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Text(
                                        text = "Fat\n${ceil(it.fat ?: 0.0).toInt()} g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Text(
                                        text = "Carbs\n${ceil(it.carbohydrates ?: 0.0).toInt()} g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Text(
                                        text = "Sugar\n${ceil(it.sugar ?: 0.0).toInt()} g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Text(
                                        text = "Protein\n${ceil(it.protein ?: 0.0).toInt()} g",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                } ?: run { // Kalau data null
                                    Text("No data found for this food.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.fillMaxWidth())
                                }
                            }
                            is Result.Loading -> {
                                Text(text = "Loading...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                            is Result.Error -> {
                                Text(
                                    text = "Food is not found!",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> { // Result.Empty
                                Text(
                                    text = "Calorie\n0 kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(text = "Fat\n0 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondary)
                                Text(text = "Carbs\n0 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondary)
                                Text(text = "Sugar\n0 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondary)
                                Text(text = "Protein\n0 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val weight = weightText.toIntOrNull() ?: 0
                val canSearch = searchText.isNotBlank() && weight > 0
                val enableAddButton = findFoodData is Result.Success && (findFoodData as Result.Success<FindFoodResponse>).data.data != null

                TextButton(
                    onClick = {
                        if (findFoodData is Result.Success) {
                            val resultData = (findFoodData as Result.Success).data.data
                            if (resultData != null) {
                                additionalList.add(
                                    AdditionalItem(
                                        name = searchText,
                                        weight = weight,
                                        info = AdditionalInfo(
                                            calories = resultData.calories ?: 0.0,
                                            fat = resultData.fat ?: 0.0,
                                            carbohydrates = resultData.carbohydrates ?: 0.0,
                                            sugar = resultData.sugar ?: 0.0,
                                            protein = resultData.protein ?: 0.0
                                        )
                                    )
                                )
                                // Clear input dan tutup popup setelah menambahkan
                                searchText = ""
                                weightText = ""
                                showPopup = false
                                viewModel.resetFindState() // Penting untuk reset state setelah berhasil
                            }
                            } else if (findFoodData !is Result.Loading && canSearch) {
                                // Ini kondisi untuk trigger pencarian jika belum ada hasil atau error, dan input valid
                                viewModel.findFood(searchText, weight)
                            }
                    },
                    enabled = (findFoodData !is Result.Loading && canSearch) || enableAddButton
                ) {
                    when (findFoodData) {
                        is Result.Loading -> Text("Loading...")
                        is Result.Success -> Text("Add") // Ganti jadi "Add" kalau sudah sukses cari
                        else -> Text("Search")

                    }
                }
            },
            dismissButton = {
                if (findFoodData !is Result.Loading) {
                    TextButton(onClick = {
                        showPopup = false
                        viewModel.resetFindState() // Reset state saat dismiss
                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun ExpandableItem(foodListItem: FoodListItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = foodListItem.name ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${foodListItem.unit ?: 0} unit",
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down // Pastikan resource ID-nya benar
                    ),
                    contentDescription = "Expand/Collapse"
                )
            }
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD8D8D8))
                    .padding(16.dp)
            ) {
                // Pastikan nilai nullable dihandle dengan operator Elvis
                Text(
                    text = "Calorie\n${foodListItem.calories?.let { ceil(it).toInt() } ?: 0} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Fat\n${foodListItem.fat?.let { ceil(it).toInt() } ?: 0} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Carbs\n${foodListItem.carbohydrate?.let { ceil(it).toInt() } ?: 0} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Sugar\n${foodListItem.sugar?.let { ceil(it).toInt() } ?: 0} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = "Protein\n${foodListItem.protein?.let { ceil(it).toInt() } ?: 0} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun ExpandableAdditionalItem(additionalItem: AdditionalItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = additionalItem.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${additionalItem.weight} g",
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down // Pastikan resource ID-nya benar
                    ),
                    contentDescription = "Expand/Collapse"
                )
            }
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            if (additionalItem.info == null) {
                Text(text = "No additional details available.", textAlign = TextAlign.Center)
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD8D8D8))
                        .padding(16.dp)
                ) {
                    // Pastikan nilai nullable dihandle dengan operator Elvis
                    Text(
                        text = "Calorie\n${additionalItem.info.calories.let { ceil(it).toInt() }} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Fat\n${additionalItem.info.fat.let { ceil(it).toInt() }} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Carbs\n${additionalItem.info.carbohydrates.let { ceil(it).toInt() }} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Sugar\n${additionalItem.info.sugar.let { ceil(it).toInt() }} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Protein\n${additionalItem.info.protein.let { ceil(it).toInt() }} g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}