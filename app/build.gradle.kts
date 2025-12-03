
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")

}

//var openAiKey: String by project

android {
    namespace = "com.bih.applicationsmurfforyou"
    compileSdk = 35
    //val openAiKey = "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"
    val openAiKey = "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"
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.bih.applicationsmurfforyou"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
        buildConfigField("String", "REPLICATE_API_TOKEN", "\"${project.findProperty("REPLICATE_API_TOKEN") ?: ""}\"")

      //  buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")

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
        }
    }

}


dependencies {
    implementation(libs.google.firebase.appcheck.playintegrity)
    implementation(libs.accompanist.navigation.animation)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.androidx.ui.tooling.preview.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //AI
     implementation(libs.tasks.vision.image.generator)

    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.android)
    implementation(libs.openai.java)


    implementation(libs.openai)



    // Use the correctly named reference
    implementation(libs.androidx.appcompat)
    // Retrofit / OkHttp
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)

    // Coil
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
}

kapt {
    correctErrorTypes = true
}

configurations.all {
    exclude(group = "com.openai")
}



