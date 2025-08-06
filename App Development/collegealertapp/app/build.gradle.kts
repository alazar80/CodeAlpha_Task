plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.collegealertapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.collegealertapp"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
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
}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.5.1")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.7.0")
    implementation ("androidx.core:core-ktx:1.8.0")

       // Firebase In-App Messaging (if you still want it)
       implementation(libs.firebase.inappmessaging)
    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:24.1.1")        // or latest

    // Play-Services base (broker layer)
    implementation("com.google.android.gms:play-services-base:18.2.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}