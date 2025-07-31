package com.amikom.sweetlife.ui.screen.scan

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.util.showToastMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    viewModel: CameraScanViewModel,
    onBackPressed: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    var camera: Camera? by remember { mutableStateOf(null) }
    var showDialogAwal by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val scanData by viewModel.scanData.observeAsState()

    when (scanData) {
        is Result.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = MaterialTheme.colorScheme.onBackground)
            }
        }

        is Result.Success -> {
            val result = (scanData as Result.Success).data
            if (result.foodList.isNullOrEmpty()) {
                showDialog = true
            } else {
                val jsonData = Gson().toJson(result.foodList)
//                sharedViewModel.setFoodList(result.foodList)
                viewModel.resetResult()
                cameraExecutor.shutdown()
                navController.navigate(Route.ResultScanScreen(jsonData)) {
                    popUpTo<Route.DashboardScreen> { inclusive = false }
                }
            }
        }

        is Result.Error -> {
            showDialog = true
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("SOMETHING WENT WRONG.", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }

    if (showDialog && scanData !is Result.Empty) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetResult()
                showDialog = false
            },
            title = { Text(if (scanData is Result.Error) "Failed" else "Info") },
            text = {
                Text(
                    if (scanData is Result.Success && (scanData as Result.Success).data.foodList.isNullOrEmpty()) {
                        "No food detected on this image, try capture with better light"
                    } else if (scanData is Result.Error) {
                        (scanData as Result.Error).error
                    } else {
                        (scanData as? Result.Success)?.data?.foodList.toString()
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetResult()
                    showDialog = false
                }) {
                    Text("Try again")
                }
            }
        )
    }

    if (showDialogAwal) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showDialogAwal = false },
            title = { Text("Confirm Image") },
            text = {
                if (isLoading) {
                    Text("Uploading, please wait...")
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Do you want to upload this image?")
                        capturedImageBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Captured Image Preview",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(150.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (!isLoading) {
                        isLoading = true
                        capturedImageBitmap?.let { bitmap ->
                            viewModel.uploadScan(context, bitmap)
                        } ?: run {
                            isLoading = false
                            showDialogAwal = false
                        }
                    }
                }) {
                    Text(if (isLoading) "Uploading..." else "Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (!isLoading) {
                        capturedImageBitmap = null
                        showDialogAwal = false
                    }
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (!isUserLoggedIn) {
        CustomDialog(
            icon = R.drawable.baseline_info_outline_24,
            title = "Info",
            message = "Your session is ended. Please login again",
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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                lifecycleOwner.lifecycleScope.launch {
                    val imageBitmap = withContext(Dispatchers.IO) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    }
                    capturedImageBitmap = imageBitmap
                    showDialogAwal = true
                }
            }
        }
    )

    // Bind Kamera ke Lifecycle Owner
    fun bindCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
                Log.d("CameraScreen", "Camera bound successfully: $camera")
            } catch (e: Exception) {
                Log.e("CameraScreen", "Failed to bind camera", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    DisposableEffect(Unit) {
        bindCamera()
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    // Handle tombol back
    BackHandler(onBack = onBackPressed)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // Preview Kamera
        if (capturedImageBitmap == null) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(0.75f, false)
            )
        } else {
            Image(
                bitmap = capturedImageBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
        }

        Image(
            painter = painterResource(id = R.drawable.scan_guide),
            contentDescription = "Camera Guide",
            modifier = Modifier
                .align(Alignment.Center)
                .size(400.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                imagePickerLauncher.launch("image/*")
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery),
                    contentDescription = "Pick Image",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = {
                isFlashOn = !isFlashOn
                camera?.cameraControl?.enableTorch(isFlashOn)
            }) {
                Icon(
                    painter = painterResource(
                        if (isFlashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off,
                    ),
                    contentDescription = "Flash Toggle",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = {
                isFrontCamera = !isFrontCamera
                bindCamera()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_flip_camera),
                    contentDescription = "Flip Camera",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                // Konversi gambar ke Bitmap
                                val bitmap = imageProxyToBitmap(image)
                                capturedImageBitmap = bitmap
                                showDialogAwal = true // Tampilkan dialog konfirmasi
                                image.close()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraScreen", "Photo capture failed", exception)
                            }
                        }
                    )
                },
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                )
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
