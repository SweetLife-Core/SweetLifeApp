package com.amikom.sweetlife.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.DiabetesPrediction
import com.amikom.sweetlife.data.model.HealthProfileModel
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.component.getBottomNavButtons
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

    var profileData: ProfileModel
    var healthData: HealthProfileModel

    when (profileRawData) {
        Result.Loading -> {
            profileData = ProfileModel(
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
                "Loading...",
                false,
                "Loading...",
                DiabetesPrediction(
                    0,
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
                false,
                Constants.DEFAULT_ERROR_TEXT,
                DiabetesPrediction(
                    0,
                    Constants.DEFAULT_ERROR_TEXT,
                    Constants.DEFAULT_ERROR_TEXT
                ),
            )
        }
    }

    val userProfile = UserProfile(
        email = profileData.email,
        name = profileData.name,
        weight = healthData.weight,
        height = healthData.weight,
        age = countAgeFromDate(profileData.dateOfBirth),
        isDiabetesRisk = healthData.isDiabetic
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(buttons = buttons)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp, 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 0.dp, 0.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                UserInfo(userProfile = userProfile)
                gmailBox(text = userProfile.email, onClick = { })
                UserHealthData(userProfile = userProfile)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    onClick = { /* TODO: Handle Edit Profile */ }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    text = "Edit Health Data",
                    onClick = { /* TODO: Handle Edit Health Data */ }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                    onClick = {
                        navController.navigate(Route.SettingsScreen) {
                            popUpTo<Route.ProfileScreen> { inclusive = false }
                        }
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    text = "Logout",
                    textColor = Color.Red,
                    onClick = { /* TODO: Handle Logout */ }
                )
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.gendut),
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
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.primary,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
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
                color = textColor,
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
        colors = CardDefaults.cardColors(
            containerColor = Color.Red,
            contentColor = Color.White
        ),
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
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

//wadah userHealthItem
@Composable
private fun UserHealthData(userProfile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserHealthItem(
                label = "Weight",
                value = "${userProfile.weight} kg"
            )
            UserHealthItem(
                label = "Height",
                value = "${userProfile.height} cm"
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
            color = if (boolean) Color.Red else Color.Black
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    trailingIcon: ImageVector = Icons.Default.KeyboardArrowRight
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray
        )
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
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }

}

data class UserProfile(
    val name: String,
    val email: String,
    val weight: Double,
    val height: Double,
    val age: Int,
    val isDiabetesRisk: Boolean
)

