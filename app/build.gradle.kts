
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
    implementation("androidx.datastore:datastore-preferences:1.1.1") // For persisting settings
    implementation("com.google.android.gms:play-services-ads:23.1.0") 
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("androidx.compose.material:material:1.7.0-beta01")
    implementation(libs.google.firebase.appcheck.playintegrity)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.ai)
    implementation(platform(libs.firebase.bom))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.google.cloud.vertexai)

    implementation(platform(libs.firebase.bom))      // you already have this

    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)


    // For handling images
    implementation(libs.androidx.core.ktx.v1150)
    implementation(libs.coil)

    implementation(libs.accompanist.navigation.animation)

    kapt(libs.dagger.hilt.compiler)

    //AI
     implementation(libs.tasks.vision.image.generator)

    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.android)

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

    implementation(libs.protobuf.javalite)

}

kapt {
    correctErrorTypes = true
}

configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-java")
}


