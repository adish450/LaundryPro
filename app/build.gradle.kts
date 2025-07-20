plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.dhobikart.app"
    compileSdk = 34 // Changed to a stable version

    defaultConfig {
        applicationId = "com.dhobikart.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("alpha") {
            isMinifyEnabled = true
            isShrinkResources = true
            //isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".alpha"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    /*packaging {
        resources {
            excludes += "/META-INF/licenses/ASM"
            excludes += "/META-INF/AL2.0"
            excludes += "win32-x86-64/attach_hotspot_windows.dll"
            excludes += "win32-x86/attach_hotspot_windows.dll"
            excludes += "/META-INF/LGPL2.1"
        }
    }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Enable View Binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android & Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle, ViewModel, and LiveData for MVVM
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Coroutines for background tasks
    implementation(libs.kotlinx.coroutines.android)
    //implementation(libs.kotlinx.coroutines.debug)

    // RecyclerView for lists
    implementation(libs.androidx.recyclerview)

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Gson for JSON serialization
    implementation(libs.gson)

    // Google Play Services for Location
    implementation(libs.play.services.location)

    // Add the Facebook Shimmer library for loading animations
    implementation(libs.shimmer)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}