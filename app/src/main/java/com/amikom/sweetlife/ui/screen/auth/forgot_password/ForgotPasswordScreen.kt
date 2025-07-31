package com.amikom.sweetlife.ui.screen.auth.forgot_password

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.ForgotPasswordModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.theme.MainBlue
import com.amikom.sweetlife.util.formatDateTime 

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    navController: NavHostController,
    event: (ForgotPasswordEvent) -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var isErrorEmail by remember { mutableStateOf(false) }

    val forgotPasswordResult by viewModel.forgotPasswordResult.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    var hasShownError by remember { mutableStateOf(false) }
    var hasShownSuccess by remember { mutableStateOf(false) }



    val showDialog = remember { mutableStateOf(false) }
    var icon by remember { mutableStateOf(R.drawable.baseline_notifications_none_24) }
    var title by remember { mutableStateOf("Failed!") }
    var message by remember { mutableStateOf("Account not found!") }
    var buttons by remember { mutableStateOf(emptyList<Pair<String, () -> Unit>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Can't Access Your Account?\n\nDon't worry. Enter your email below, and we'll send you a password reset link.",
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(0.dp, 10.dp)
        )

        // Email Address
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isErrorEmail = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.small,
            label = {
                Text(
                    "Email address",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isErrorEmail) MaterialTheme.colorScheme.error else Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon",
                    tint = if (isErrorEmail) MaterialTheme.colorScheme.error else Color.Gray
                )
            },
            isError = isErrorEmail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        if (isErrorEmail) {
            Text(
                text = "Invalid email address",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Continue Button
        Button(
            onClick = {
                if(email.isNotEmpty() && !isErrorEmail) {
                    event(ForgotPasswordEvent.ForgotPassword(email = email))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(48.dp),
            shape = RoundedCornerShape(15.dp),
            enabled = forgotPasswordResult !is Result.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MainBlue
            )
        ) {
            if (forgotPasswordResult is Result.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Continue")
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "")
            }
        }

        when (forgotPasswordResult) {
            is Result.Success -> {
                if (!hasShownSuccess) {

                    navController.navigate(Route.CheckEmailScreen) {
                        popUpTo<Route.ForgotPasswordScreen> { inclusive = false }
                    }

//                    val result = (forgotPasswordResult as Result.Success<ForgotPasswordModel>).data
//
//                    showDialog.value = true
//                    title = "Success!"
//                    message = "We have sent an email to $email, please check your inbox or spam. Link will expire at: ${formatDateTime(result.expire)}"
//                    buttons = listOf(
//                        "Ok, I'll do it later" to { showDialog.value = false },
//                        "Open Gmail App" to {
//                            val emailIntent = Intent(Intent.ACTION_MAIN).apply {
//                                addCategory(Intent.CATEGORY_APP_EMAIL)
//                            }
//
//                            if (emailIntent.resolveActivity(context.packageManager) != null) {
//                                context.startActivity(emailIntent)
//                            } else {
//                                context.startActivity(
//                                    Intent(
//                                        Intent.ACTION_VIEW,
//                                        Uri.parse("https://gmail.com")
//                                    )
//                                )
//                            }
//                            showDialog.value = false
//                        }
//                    )

                    hasShownSuccess = true
                }
            }

            is Result.Error -> {
                if (!hasShownError) {
                    val errorMessage = (forgotPasswordResult as Result.Error).error

                    if (errorMessage == "email not found") {
                        showDialog.value = true
                        icon = R.drawable.baseline_info_outline_24
                        message =
                            "Email is not registered!"
                        buttons = listOf(
                            "Ok" to { showDialog.value = false },
                            "Register Now" to {
                                navController.navigate(Route.SignUpScreen) {
                                    popUpTo<Route.SignUpScreen> { inclusive = false }
                                }
                                showDialog.value = false
                            }
                        )

                    } else if (errorMessage.contains("Key:")) {
                        showDialog.value = true
                        icon = R.drawable.baseline_info_outline_24
                        message = "Fill all field!"
                        buttons = listOf(
                            "Ok" to { showDialog.value = false }
                        )
                    } else {
                        showDialog.value = true
                        icon = R.drawable.baseline_info_outline_24
                        title = "Something went wrong!"
                        message = errorMessage
                        buttons = listOf(
                            "Ok" to { showDialog.value = false }
                        )
                    }


                    hasShownError = true
                }
            }

            else -> {
                // Loading sudah ditangani di button
            }
        }

        if (showDialog.value) {
            CustomDialog(
                openDialogCustom = showDialog,
                icon = icon,
                title = title,
                message = message,
                buttons = buttons
            )
        }
    }

    LaunchedEffect(forgotPasswordResult) {
        if (forgotPasswordResult !is Result.Error) hasShownError = false
        if (forgotPasswordResult !is Result.Success) hasShownSuccess = false
    }

}
