package com.amikom.sweetlife.ui.screen.profile.editProfile

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.EditProfileModel
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.ConfirmSave
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.screen.profile.UserProfile
import com.amikom.sweetlife.util.countAgeFromDate
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onEditProfile: () -> Unit,
    navController: NavController
) {
    val updateStatus by viewModel.updateStatus.observeAsState()
    val isLoading by viewModel.loading.observeAsState(false)
    val profileRawData by viewModel.profileData.observeAsState()
    val showDialog = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val userProfile by viewModel.userProfile.observeAsState()
    val profileLoadState by viewModel.profileLoadState.collectAsState()

    val context = LocalContext.current
    val profileData: EditProfileModel

    val selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Tambahkan observasi status upload
    val imageUploadStatus by viewModel.imageUploadStatus.observeAsState()

    val imageUploadState by viewModel.imageUploadState.collectAsState()

    // Handle status upload
    imageUploadStatus?.let { status ->
        if (status == "success") {
            Log.d("Success", "Image uploaded successfully.")
            Log.d("Success", "Image URL: ")
            viewModel.uploadProfileImage(selectedImage!!, context)
        } else {
            Log.e("Error", "Failed to upload image. Please try again.")
            Toast.makeText(context, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    // Handle state upload
    LaunchedEffect(imageUploadState) {
        when (val state = imageUploadState) {
            is EditProfileViewModel.ImageUploadState.Loading -> {
                // Tampilkan loading
//                CircularProgressIndicator()
                Toast.makeText(
                    context,
                    "Uploading image...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is EditProfileViewModel.ImageUploadState.Success -> {
                // Tampilkan toast sukses
                Toast.makeText(
                    context,
                    "Image uploaded successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Optional: Reset state
                viewModel.resetImageUploadState()
            }

            is EditProfileViewModel.ImageUploadState.Error -> {
                // Tampilkan pesan error
                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_SHORT,
                ).show()

                // Optional: Reset state
                viewModel.resetImageUploadState()
                Log.e("Error", "Failed to upload image: ${state.message}")
            }

            is EditProfileViewModel.ImageUploadState.Idle -> {
                // State awal, tidak perlu tindakan
            }
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                viewModel.uploadProfileImage(bitmap, context)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            viewModel.uploadProfileImage(it, context)
        }
    }


    var showPickerDialog by remember { mutableStateOf(false) }

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text("Select Image Source") },
            confirmButton = {
                Column {
                    Button(
                        onClick = {
                            showPickerDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick from Gallery")
                    }
                    Button(
                        onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                cameraLauncher.launch(null)
                            }
                            else {
                                cameraPermissionState.launchPermissionRequest()
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Take Photo")
                    }
                }
            },
            text = {
                Text("Please select image source")
            },
            dismissButton = {
                Button(
                    onClick = {
                        showPickerDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Avatar(
            image = (userProfile?.image ?: R.drawable.bapak),
            onEditClick = {
                showPickerDialog = true
            }
        )

        // Nama Lengkap
        EditItem(
            icon = Icons.Default.Person,
            label = "Full Name",
            value = userProfile?.name ?: "",
            onValueChange = { viewModel.onNameChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        EditItemEmail(
            label = "Email",
            value = userProfile?.email ?: "",
            onValueChange = { viewModel.onEmailChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tanggal Lahir
        EditDate(
            icon = Icons.Default.DateRange,
            label = "Date of Birth",
            value = userProfile?.dateOfBirth ?: "",
            isDate = true,
            onDateChange = { viewModel.onDateChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        //gender
        genderDropdown(
            viewModel = viewModel,
            label = "Gender",
            value = userProfile?.gender ?: "",
            onValueChange = { viewModel.onGenderChange(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Button
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(if (isLoading) "Loading..." else "Save")
            if (!isLoading) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "")
            }
        }
        if (showDialog.value) {
            ConfirmSave(
                openDialogCustom = showDialog,
                title = "Confirm Save",
                message = "Are you sure you want to save changes?",
                buttons = listOf(
                    "Yes, Confirm" to {
                        showDialog.value = false
                        viewModel.updateProfile()
                        showSuccessDialog.value = true
                    },
                    "No, Cancel" to {
                        showDialog.value = false
                    }
                )
            )
        }

        if (showSuccessDialog.value) {
            CustomDialog(
                openDialogCustom = showSuccessDialog,
                icon = R.drawable.baseline_check_circle_outline_24,
                title = "Success!",
                message = "Your data has been successfully saved.",
                buttons = listOf(
                    "Okay" to {
                        showSuccessDialog.value = false
                        navController.navigate(Route.DashboardScreen) {
                            popUpTo<Route.EditProfileScreen> { inclusive = false }
                        }
                    }
                )
            )
        }

    }

    // Handle update status
    updateStatus?.let { success ->
        Text(
            text = if (success) "Profile updated successfully!" else "Failed to update profile. Please try again.",
            color = if (success) Color.Green else Color.Red
        )
    }
}

@Composable
private fun Avatar(
    image: Any?,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(120.dp), // Ukuran gambar sebagai batas luar
            contentAlignment = Alignment.BottomEnd // Posisi relatif IconButton
        ) {
            // Avatar Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .placeholder(R.drawable.bapak)
                    .error(R.drawable.bapak)
                    .build(),
                contentDescription = "User Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            // Edit Icon
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(24.dp) // Ukuran tombol ikon
                    .offset(x = (-8).dp, y = (-8).dp) // Menggeser tombol sedikit ke dalam gambar
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = Color.Gray)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditDate(
    icon: ImageVector,
    label: String,
    value: String,
    isDate: Boolean = false,
    onDateChange: (String) -> Unit = {}
) {
    val dateDialogState = rememberMaterialDialogState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(15.dp))
            .clickable {
                if (isDate) dateDialogState.show()
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (value.isNotEmpty()) value else label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Dialog Tanggal
    if (isDate) {
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton("Select")
                negativeButton("Cancel")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Select Date"
            ) { selectedDate ->
                val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                onDateChange(formattedDate)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditItem(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {}
) {

    OutlinedTextField(
        value = value,
        label = { Text(label) },
        onValueChange = {
            onValueChange(it)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
    )
}


@Composable
private fun EditItemEmail(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var errorMessage: String? = null

    // Fungsi untuk validasi email
    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(email)
    }

    // Validasi email setiap kali ada perubahan
    if (!validateEmail(value) && value.isNotEmpty()) {
        errorMessage = "Email tidak valid"
    }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        label = { Text(label) },
        isError = errorMessage != null,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon",
            )
        }
    )

    // Pesan error
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
fun genderDropdown(
    viewModel: EditProfileViewModel,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")
    var selectedGender by remember { mutableStateOf(value) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
            .border(0.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(15.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Gender Icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (selectedGender.isNotEmpty()) selectedGender else value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        genderOptions.forEach { type ->
            DropdownMenuItem(
                text = { Text(type) },
                onClick = {
                    selectedGender = type
                    onValueChange(type)
                    expanded = false
                }
            )
        }
    }
}


data class UserProfiles(
    val image: String,
    val name: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String
)


