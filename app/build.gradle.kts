plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)
    defaultConfig {
        applicationId = "com.anufriev.driver"
        minSdkVersion(rootProject.extra["minSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["compileSdkVersion"] as Int)
        versionCode = 7
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }
    }
    lintOptions {
        isIgnoreTestSources = true
    }
    kapt {
        generateStubs = true
        correctErrorTypes = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":core-date"))
    implementation(project(":utils"))
    implementation(project(":drawable"))

    implementation("androidx.appcompat:appcompat:1.2.0")

    //Dagger-Hilt
    val dagger = rootProject.extra["dagger_version"]
    val daggerWork = rootProject.extra["hilt_work_version"]
    implementation("com.google.dagger:hilt-android:$dagger")
    kapt("com.google.dagger:hilt-android-compiler:$dagger")
    implementation("androidx.hilt:hilt-work:$daggerWork")
    kapt("androidx.hilt:hilt-compiler:$daggerWork")
    //WorkManager
    implementation("androidx.work:work-runtime-ktx:2.5.0")

    //Retrofit
    val retrofit = rootProject.extra["retrofit_version"]
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    //Stetho
    implementation("com.facebook.stetho:stetho-okhttp3:1.5.1")
    //Okhttp
    val okhttp = rootProject.extra["okHttp_version"]
    implementation("com.squareup.okhttp3:okhttp:$okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")
}
