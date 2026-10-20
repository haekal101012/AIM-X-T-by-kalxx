plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.kalxx.uiassist"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.android.system.uiassist"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        ndk { abiFilters += listOf("armeabi-v7a", "arm64-v8a") }
    }
    buildTypes {
        release { isMinifyEnabled = false }
        debug { applicationIdSuffix = ".debug" }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
