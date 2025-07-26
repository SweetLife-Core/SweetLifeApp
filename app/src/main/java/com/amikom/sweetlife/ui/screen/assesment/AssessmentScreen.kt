package com.amikom.sweetlife.ui.screen.assesment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.amikom.sweetlife.BuildConfig
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.theme.MainBlue
import java.util.Calendar

fun String.isValidDecimal(): Boolean = isEmpty() || matches(Regex("^\\d*\\.?\\d*\$"))
fun String.isValidInteger(): Boolean = isEmpty() || matches(Regex("^\\d*\$"))

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(15.dp),
    validator: (String) -> Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (validator(it)) onValueChange(it)
        },
        modifier = modifier,
        shape = shape,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun DropdownSelector(
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F5),
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            border = BorderStroke(1.dp, Color.Gray),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue.ifEmpty { placeholder },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Gray
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onValueSelected(option)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(navController: NavController, viewModel: AssessmentViewModel = viewModel()) {
    val pages = listOf<@Composable () -> Unit>(
        { PersonalDataScreen(viewModel) },
        { DiabetesStatusScreen(viewModel) },
        { NextDiabetesStatusScreen(viewModel) },
        { ActivityScreen1(viewModel) },
        { ActivityScreen2(viewModel) },
        { DayGoalsScreen(viewModel) }
    )

    val showDialog = remember { mutableStateOf(false) }
    var icon by remember { mutableStateOf(R.drawable.baseline_info_outline_24) }
    var title by remember { mutableStateOf("Failed!") }
    var message by remember { mutableStateOf("Please fill all field!") }
    var buttons by remember { mutableStateOf(emptyList<Pair<String, () -> Unit>>()) }

    val result by viewModel.result.collectAsState(initial = false)
    val showError by viewModel.showError.collectAsState(initial = false)
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val updateProfileResult by viewModel.updateProfileResult.collectAsState()
    val createHealth by viewModel.createHealth.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var currentPage by remember { mutableStateOf(0) }
    val isLastPage = currentPage == pages.lastIndex

    if (!isUserLoggedIn) {
        CustomDialog(
            icon = R.drawable.baseline_info_outline_24,
            title = "Info",
            message = "You'r session is ended. Please login again",
            openDialogCustom = remember { mutableStateOf(true) },
            buttons = listOf(
                "Ok" to {
                    navController.navigate(Route.LoginScreen) {
                        launchSingleTop = true
                    }
                }
            ),
            dismissOnBackdropClick = false
        )
    }

// Trigger validation when page changes
    LaunchedEffect(currentPage) {
        when (currentPage) {
            0 -> viewModel.validatePersonalData()
            1 -> viewModel.validateDiabetesStatus()
            2 -> viewModel.validateNextDiabetesStatus()
            3 -> viewModel.validateActivityData()
            4 -> viewModel.validateHealthHistory()
            5 -> viewModel.validateDayGoals()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Assessment", style = MaterialTheme.typography.titleLarge) },
            )
        },
        bottomBar = {
            val isNotLoading = updateProfileResult !is Result.Loading && createHealth !is Result.Loading

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentPage > 0) {
                    Button(
                        onClick = {
                            if (currentPage == 3 && !viewModel.diabetesStatus.isDiabetic) {
                                currentPage -= 2
                            } else {
                                currentPage--
                            }
                        },
                        enabled = isNotLoading
                    ) {
                        Text(text = "Back")
                    }
                } else {
                    Button(
                        onClick = { /* No Action */ },
                        colors = ButtonColors(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent
                        )
                    ) {
                        Text("")
                    }
                }

                Button(
                    onClick = {
                        if (isLastPage) {
                            viewModel.submitDataToServer()
                        } else {
                            currentPage =
                                if (currentPage == 1 && !viewModel.diabetesStatus.isDiabetic) {
                                    currentPage + 2 // Skip NextDiabetesStatusScreen
                                } else {
                                    currentPage + 1
                                }
                        }
                    },
                    enabled = isNotLoading
                ) {
                    if (!isNotLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = if (isLastPage) "Save" else "Continue")
                    }
                }
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = (currentPage + 1) / pages.size.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Displaying the current page
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (currentPage) {
                        0 -> pages[0]()
                        1 -> pages[1]()
                        2 -> pages[2]()
                        3 -> pages[3]()
                        4 -> pages[4]()
                        5 -> pages[5]()
                    }
                }
            }
        }
    )

    LaunchedEffect(showError) {
        if (showError) {
            showDialog.value = true
        }
    }

    if (showDialog.value) {
        CustomDialog(
            openDialogCustom = showDialog,
            icon = icon,
            title = title,
            message = errorMessage.ifEmpty { message },
            buttons = listOf("Ok" to {
                // do nothing
                showDialog.value = false
                viewModel.dismissError()
            }),
            dismissOnBackdropClick = false
        )
    }

    LaunchedEffect(result) {
        if (result) {
            navController.navigate(Route.DashboardScreen) {
                popUpTo<Route.AssessmentScreen> { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun PersonalDataScreen(viewModel: AssessmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Fill in Your Personal Data",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete your data to find out your assessment results",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name Field
        OutlinedTextField(
            value = viewModel.personalData.fullName,
            onValueChange = { viewModel.personalData = viewModel.personalData.copy(fullName = it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            label = {
                Text(
                    "Full Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Date of Birth Picker Button
        DatePickerButton(
            date = viewModel.personalData.dateOfBirth,
            onDateSelected = {
                viewModel.personalData = viewModel.personalData.copy(dateOfBirth = it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gender Picker Button
        GenderPickerButton(
            selectedGender = viewModel.personalData.gender,
            onGenderSelected = {
                viewModel.personalData = viewModel.personalData.copy(gender = it)
            }
        )
    }
}

@Composable
fun DatePickerButton(date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Default date values
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate by remember { mutableStateOf(date) }

    Button(
        onClick = {
            val datePicker = android.app.DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedYear-${
                        (selectedMonth + 1).toString().padStart(2, '0')
                    }-${selectedDay.toString().padStart(2, '0')}"
                    selectedDate = formattedDate
                    onDateSelected(formattedDate)
                },
                year,
                month,
                day
            )
            datePicker.show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .width(16.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonColors(
            Color.Transparent,
            MaterialTheme.colorScheme.primary,
            Color.Gray,
            MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(
            textAlign = TextAlign.Start,
            text = if (selectedDate.isEmpty()) "Select Date of Birth" else "Born Date: $selectedDate",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun GenderPickerButton(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val genderOptions = listOf("Male", "Female")
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = { expanded = true },
        modifier = Modifier
            .fillMaxWidth()
            .width(16.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonColors(
            Color.Transparent,
            MaterialTheme.colorScheme.primary,
            Color.Gray,
            MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(
            text = if (selectedGender.isEmpty()) "Select Gender" else "Gender: $selectedGender",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = TextAlign.Start,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        genderOptions.forEach { gender ->
            DropdownMenuItem(
                text = { Text(gender) },
                onClick = {
                    expanded = false
                    onGenderSelected(gender)
                }
            )
        }
    }
}

@Composable
fun DiabetesStatusScreen(viewModel: AssessmentViewModel) {
    val options = listOf("Yes, I have diabetes", "No, I'm not a diabetic")
    var selectedOption by remember { mutableStateOf(options[0]) }

    // Sinkronkan status awal `selectedOption` dengan data di ViewModel
    LaunchedEffect(viewModel.diabetesStatus.isDiabetic) {
        selectedOption = when (viewModel.diabetesStatus.isDiabetic) {
            true -> "Yes, I have diabetes"
            false -> "No, I'm not a diabetic"
        }
    }

    // Parent container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and Subtitle
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Diabetes Status",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete your data to find out your assessment results",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form fields
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Diabetes Status?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(
                            width = 1.dp,
                            color = if (selectedOption == option) Color.Blue else MainBlue,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable {
                            selectedOption = option
                            viewModel.diabetesStatus = when (option) {
                                "Yes, I have diabetes" -> viewModel.diabetesStatus.copy(isDiabetic = true)
                                "No, I'm not a diabetic" -> viewModel.diabetesStatus.copy(isDiabetic = false)
                                else -> viewModel.diabetesStatus.copy(isDiabetic = false)
                            }
                        }
                        .padding(5.dp)
                        .padding(horizontal = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedOption == option) MainBlue else Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = {
                                selectedOption = option
                                viewModel.diabetesStatus = when (option) {
                                    "Yes, I have diabetes" -> viewModel.diabetesStatus.copy(
                                        isDiabetic = true
                                    )

                                    "No, I'm not a diabetic" -> viewModel.diabetesStatus.copy(
                                        isDiabetic = false
                                    )

                                    else -> viewModel.diabetesStatus.copy(isDiabetic = false)
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MainBlue,
                                unselectedColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextDiabetesStatusScreen(viewModel: AssessmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diabetes Status",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Complete your data to find out your assessment results",
            fontSize = 16.sp,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            DropdownSelector(
                selectedValue = viewModel.nextDiabetesStatus.type,
                options = listOf("Type 1", "Type 2", "Type 3", "Gestational"),
                onValueSelected = {
                    viewModel.nextDiabetesStatus = viewModel.nextDiabetesStatus.copy(type = it)
                },
                placeholder = "Select Your Diabetic Type"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValidatedTextField(
                value = viewModel.nextDiabetesStatus.insulinLevel.toString(),
                onValueChange = { value ->
                    viewModel.nextDiabetesStatus = viewModel.nextDiabetesStatus.copy(
                        insulinLevel = value.toDoubleOrNull() ?: 0.0
                    )
                },
                label = "Insulin Level",
                keyboardType = KeyboardType.Decimal,
                validator = String::isValidDecimal
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValidatedTextField(
                value = viewModel.nextDiabetesStatus.bloodPressure.toString(),
                onValueChange = { value ->
                    viewModel.nextDiabetesStatus = viewModel.nextDiabetesStatus.copy(
                        bloodPressure = value.toIntOrNull() ?: 0
                    )
                },
                label = "Blood Sugar Pressure",
                keyboardType = KeyboardType.Number,
                validator = String::isValidInteger
            )
        }
    }
}

@Composable
fun ActivityScreen1(viewModel: AssessmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How is your activity?",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ValidatedTextField(
                value = viewModel.activityData1.height.toString(),
                onValueChange = { value ->
                    viewModel.activityData1 = viewModel.activityData1.copy(
                        height = value.toIntOrNull() ?: 0
                    )
                },
                label = "Height (Cm)",
                keyboardType = KeyboardType.Number,
                validator = String::isValidInteger,
                modifier = Modifier.weight(1f)
            )
            ValidatedTextField(
                value = viewModel.activityData1.weight.toString(),
                onValueChange = { value ->
                    viewModel.activityData1 = viewModel.activityData1.copy(
                        weight = value.toIntOrNull() ?: 0
                    )
                },
                label = "Weight (Kg)",
                keyboardType = KeyboardType.Number,
                validator = String::isValidInteger,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        DropdownSelector(
            selectedValue = viewModel.activityData1.physicalActivity,
            options = listOf("Extremely", "Active", "Moderate", "Light", "Never"),
            onValueSelected = {
                viewModel.activityData1 = viewModel.activityData1.copy(physicalActivity = it)
            },
            placeholder = "Select Your Physical Activity Level"
        )
    }
}

@Composable
fun ActivityScreen2(viewModel: AssessmentViewModel) {
    // Parent container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and Subtitle
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "How is your activity?",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete your data to find out your assessment results",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Activity2Select(
            typeOptions = listOf(
                "Never",
                "Current",
                "Former",
                "Ever"
            ),
            title = "Select your smoking history",
            selectedType = viewModel.activityData2.smokingHistory,
            onTypeSelected = {
                viewModel.activityData2 = viewModel.activityData2.copy(smokingHistory = it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Activity2Select(
            typeOptions = listOf("Yes", "No"),
            title = "Have you ever had heart disease?",
            selectedType = viewModel.activityData2.heartDiseaseHistory,
            onTypeSelected = {
                viewModel.activityData2 = viewModel.activityData2.copy(heartDiseaseHistory = it)
            }
        )
    }
}

@Composable
fun Activity2Select(
    typeOptions: List<String>,
    title: String,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = { expanded = true },
        modifier = Modifier
            .fillMaxWidth()
            .width(16.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonColors(
            Color.Transparent,
            MaterialTheme.colorScheme.primary,
            Color.Gray,
            MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(
            text = selectedType.ifEmpty { title },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = TextAlign.Start,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        typeOptions.forEach { type ->
            DropdownMenuItem(
                text = { Text(type) },
                onClick = {
                    expanded = false
                    onTypeSelected(type)
                }
            )
        }
    }
}

@Composable
fun DayGoalsScreen(viewModel: AssessmentViewModel) {
    // Parent container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and Subtitle
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Day Goals",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete your data to find out your assessment results",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(150.dp)
            ) {
                Text(
                    text = "Calories",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Sugar Intake",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.dayGoals.calories.toString(),
                    onValueChange = {
                        viewModel.dayGoals = viewModel.dayGoals.copy(
                            calories = it.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    label = {
                        Text(
                            "Kcal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal // Memungkinkan angka dengan koma
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.dayGoals.sugarIntake.toString(),
                    onValueChange = {
                        viewModel.dayGoals = viewModel.dayGoals.copy(
                            sugarIntake = it.toIntOrNull() ?: 0
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    label = {
                        Text(
                            "gram",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal // Memungkinkan angka dengan koma
                    ),
                    singleLine = true
                )
            }
        }
    }
}