
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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
            buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", "\"ca-app-pub-8342448049337544/2909767462\"")
        }
        debug {
            buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation(libs.material)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview.android)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase (Bill of Materials)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai) {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.google.firebase.appcheck.playintegrity)
    implementation(libs.firebase.crashlytics.buildtools)


    implementation(libs.firebase.analytics)

    // Google Ads
    implementation(libs.play.services.ads)


    // Data & Settings
    implementation(libs.androidx.datastore.preferences)

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
    implementation(libs.google.cloud.vertexai) {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }
    implementation(libs.tasks.vision.image.generator)
    implementation(libs.tensorflow.lite)


    // Misc
    implementation(libs.protobuf.javalite)

 }
