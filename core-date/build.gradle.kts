plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)

    defaultConfig {
        minSdkVersion(rootProject.extra["minSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["compileSdkVersion"] as Int)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles("proguard-rules.pro")
        }
    }
    val API_ENDPOINT = "API_ENDPOINT"
    val PROD_SERVER_DOMAIN = "81.176.226.132/"
    val PROD_REST_SERVER = "\"http://$PROD_SERVER_DOMAIN\""

    flavorDimensions("default")
    productFlavors {
        create("production") {
            dimension("default")
            buildConfigField("String",API_ENDPOINT,PROD_REST_SERVER)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(project(":utils"))

    //SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("androidx.preference:preference-ktx:1.1.1")
    //EventBus
    implementation("org.greenrobot:eventbus:3.1.1")

    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.3.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")

    // Coroutine
    val kotlin = rootProject.extra["kotlin_coroutines"]
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin")

    //Kotlin
    val kotlin_version = rootProject.extra["kotlin_version"]
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Dagger-Hilt
    val dagger = rootProject.extra["dagger_version"]
    implementation("com.google.dagger:hilt-android:$dagger")
    kapt("com.google.dagger:hilt-android-compiler:$dagger")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha02")

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
