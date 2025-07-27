plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-android")


    // Nav
    alias(libs.plugins.kotlin.serialization)

    // Hilt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.amikom.sweetlife"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amikom.sweetlife"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // API BASE URL
        buildConfigField("String", "BASE_URL_DEV", "\"https://api.sweetlife.my.id/\"")
        buildConfigField("String", "BASE_URL_PROD", "\"https://api.sweetlife.my.id/\"")
        buildConfigField("String", "BASE_URL_CHAT", "\"https://n8n.sweetlife.my.id/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.paging.common.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //room
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")


    // SplashScreen
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)

    //coil
    implementation("io.coil-kt:coil-compose:2.2.2")


    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation ("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    //gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    implementation("androidx.compose.material3:material3:1.2.1")

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation ("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation ("com.google.dagger:hilt-android:2.51.1")
    kapt ("com.google.dagger:hilt-compiler:2.51.1")


    implementation(libs.androidx.navigation.compose)
    implementation("androidx.navigation:navigation-compose:2.8.4")

    implementation ("me.rmyhal.contentment:contentment:2.0.1")

    // Ui Controller
    implementation(libs.accompanist.systemuicontroller)

    // Retrofit Api Communication
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Compose LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.runtime)

    //DATEpICKER
    implementation ("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")

    // Bottom Nav
    implementation ("com.github.0xRahad:RioBottomNavigation:1.0.2")

    //multipart
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // camerax
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")
    implementation("androidx.camera:camera-extensions:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.compose.ui:ui:1.7.5")
    implementation("androidx.compose.ui:ui-tooling:1.7.5")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    implementation("androidx.compose.foundation:foundation:1.7.6")
}

kapt {
    correctErrorTypes = true
}
