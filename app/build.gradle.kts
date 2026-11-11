
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.beastforge.game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.beastforge.game"

        // Minimum supported Android version: Android 10
        minSdk = 29

        // Target Android version: Android 15
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    // No external libraries required yet.
}
