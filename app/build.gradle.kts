
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
     alias(libs.plugins.hilt)
    kotlin("kapt")

}

android {
    namespace = "com.bih.applicationsmurfforyou"
    compileSdk = 35
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.bih.applicationsmurfforyou"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
    buildFeatures {
        viewBinding = true
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            excludes += "META-INF/*"

            pickFirsts += setOf(
                "mozilla/public-suffix-list.txt"
            )
        }
    }

}


dependencies {
    // Core & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    //implementation(libs.androidx.navigation.animation)
    implementation(libs.material) // For M2 components if needed
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.material3)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase (Bill of Materials)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.google.firebase.appcheck.playintegrity)
    implementation(libs.firebase.crashlytics.buildtools)

    // Google Ads
    implementation("com.google.android.gms:play-services-ads:23.1.0")

    // Data & Settings
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Networking
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)

    // Image Loading
    implementation(libs.coil.compose)

    // AI & ML
    implementation(libs.google.cloud.vertexai)
    implementation(libs.tasks.vision.image.generator)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    // Misc
    implementation(libs.protobuf.javalite)
}

kapt {
    correctErrorTypes = true
}

configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-java")
}


