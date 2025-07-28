package com.amikom.sweetlife.ui.screen.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.DiabetesPrediction
import com.amikom.sweetlife.data.model.HealthProfileModel
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.ui.presentation.onboarding.OnboardingModel.FirstPages.image
import com.amikom.sweetlife.util.Constants
import com.amikom.sweetlife.util.countAgeFromDate
import com.amikom.sweetlife.util.getCurrentDate

@Composable
fun UserProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavController,
) {
    val profileRawData by profileViewModel.profileData.observeAsState()
    val healthRawData by profileViewModel.healthData.observeAsState()
    val isUserLoggedIn by profileViewModel.isUserLoggedIn.collectAsState()

    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController)

    val profileData: ProfileModel
    val healthData: HealthProfileModel

    when (profileRawData) {
        Result.Loading -> {
            profileData = ProfileModel(
                "Loading...",
                "Loading...",
                "Loading...",
                "Loading...",
                getCurrentDate(),
                "Loading...",
            )
        }

        is Result.Success -> {
            profileData = (profileRawData as Result.Success<ProfileModel>).data
        }

        else -> {
            profileData = ProfileModel(
                Constants.DEFAULT_ERROR_TEXT,
                Constants.DEFAULT_ERROR_TEXT,
                Constants.DEFAULT_ERROR_TEXT,
                Constants.DEFAULT_ERROR_TEXT,
                getCurrentDate(),
                Constants.DEFAULT_ERROR_TEXT,
            )
        }
    }

    when (healthRawData) {
        Result.Loading -> {
            healthData = HealthProfileModel(
                "Loading...",
                0.0,
                0.0,
                false,
                diabeticType = "Loading...",
                insulinLevel = 0,
                bloodPressure = 0,
                "Loading...",
                false,
                "Loading...",
                DiabetesPrediction(
                    0.0,
                    "Loading...",
                    "Loading..."
                ),
            )
        }

        is Result.Success -> {
            healthData = (healthRawData as Result.Success<HealthProfileModel>).data
        }

        else -> {
            healthData = HealthProfileModel(
                Constants.DEFAULT_ERROR_TEXT,
                0.0,
                0.0,
                false,
                Constants.DEFAULT_ERROR_TEXT,
                0,
                0,
                Constants.DEFAULT_ERROR_TEXT,
                false,
                Constants.DEFAULT_ERROR_TEXT,
                DiabetesPrediction(
                    0.0,
                    Constants.DEFAULT_ERROR_TEXT,
                    Constants.DEFAULT_ERROR_TEXT
                ),
            )
        }
    }

    val userProfile = UserProfile(
        image = profileData.image,
        email = profileData.email,
        name = profileData.name,
        weight = healthData.weight,
        height = healthData.height,
        age = countAgeFromDate(profileData.dateOfBirth),
        isDiabetesRisk = healthData.isDiabetic
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(buttons = buttons, navController = navController, currentScreen = Route.ProfileScreen)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                UserInfo(userProfile = userProfile)
                gmailBox(text = userProfile.email)
                UserHealthData(userProfile = userProfile)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
                    onClick = {
                        navController.navigate(Route.EditProfileScreen) {
                            launchSingleTop = true
                        }
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    text = "Edit Health Data",
                    shape = RoundedCornerShape(0.dp),
                    onClick = {
                        navController.navigate(Route.EditHealthScreen) {
                            launchSingleTop = true
                        }
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                    shape = RoundedCornerShape(0.dp),
                    onClick = {
                        navController.navigate(Route.SettingsScreen) {
                            popUpTo<Route.ProfileScreen> { inclusive = false }
                        }
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.ExitToApp,
                    text = "Logout",
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp),
                    textColor = Color.Red,
                    onClick = {
                        navController.navigate(Route.LoginScreen) {
                            profileViewModel.logout()
                            popUpTo<Route.LoginScreen>() {inclusive=true}
                            launchSingleTop = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }

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
}

@Composable
private fun UserInfo(
    userProfile: UserProfile,
) {
    Log.d("UserProfileScreen", "${userProfile.image}")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val imagePainter = rememberAsyncImagePainter(
            model = userProfile.image.ifEmpty {
                "https://ui-avatars.com/api/?name=${userProfile.name}&size=120&background=random"
            },
            error = painterResource(R.drawable.bapak), // Gambar error jika gagal memuat
            placeholder = painterResource(R.drawable.bapak) // Gambar placeholder saat loading
        )
        Image(
            painter = imagePainter,
            contentDescription = "User Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun gmailBox(
    text: String,
    textColor: Color = MaterialTheme.colorScheme.primary,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .wrapContentWidth()
            .padding(0.dp, 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun diabetesRisk(
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.White,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .wrapContentWidth()
            .clickable(onClick = onClick)
            .padding(0.dp, 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

//predict
fun prediksiWaktuGoalBMI(
    beratSekarang: Double,
    tinggiCm: Double
): Int {
    val goalBMI = 22.0
    val tinggiM = tinggiCm / 100.0
    val beratTarget = goalBMI * tinggiM * tinggiM
    val selisihBerat = beratTarget - beratSekarang
    val totalKalori = selisihBerat * 7700
    val kebutuhanKaloriMaintenance = beratSekarang * 30
    val kaloriHarian = if (selisihBerat < 0) kebutuhanKaloriMaintenance - 500 else kebutuhanKaloriMaintenance + 500
    val defisitKaloriPerHari = kebutuhanKaloriMaintenance - kaloriHarian
    if (defisitKaloriPerHari == 0.0) return Int.MAX_VALUE
    val waktuHari = totalKalori / defisitKaloriPerHari
    return kotlin.math.abs(waktuHari).toInt()
}


@Composable
private fun UserHealthData(userProfile: UserProfile) {
    val berat = userProfile.weight ?: 0.0
    val tinggi = userProfile.height ?: 0.0

    val waktuHari = prediksiWaktuGoalBMI(
        beratSekarang = berat,
        tinggiCm = tinggi
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Perkiraan goal terpenuhi : $waktuHari hari",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            UserHealthItem(
                label = "Weight",
                value = "${userProfile.weight.toInt()} kg"
            )
            UserHealthItem(
                label = "Height",
                value = "${userProfile.height.toInt()} cm"
            )
            UserHealthItem(
                label = "Age",
                value = "${userProfile.age} years"
            )
            UserHealthItem(
                label = "Diabetes Risk",
                value = if (userProfile.isDiabetesRisk) "At risk" else "Not at risk",
                boolean = userProfile.isDiabetesRisk
            )

        }
    }
}

//isi dari UserHealthItem
@Composable
private fun UserHealthItem(
    label: String,
    value: String, boolean: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (boolean) Color.Red else Color.Gray
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    trailingIcon: ImageVector = Icons.Default.KeyboardArrowRight
) {

    Spacer(modifier = Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
            )
        }
    }
}

data class UserProfile(
    val name: String,
    val image: String,
    val email: String,
    val weight: Double,
    val height: Double,
    val age: Int,
    val isDiabetesRisk: Boolean
)