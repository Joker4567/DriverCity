plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
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

    buildFeatures {
        viewBinding = true
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
    implementation(project(":drawable"))

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.google.guava:guava:27.0.1-android")
    implementation("org.litepal.android:core:1.4.1")
    //Navigation
    val navigation = rootProject.extra["navigation_version"]
    implementation("android.arch.navigation:navigation-fragment-ktx:$navigation")
    implementation("android.arch.navigation:navigation-fragment:$navigation")
    implementation("android.arch.navigation:navigation-ui:$navigation")
    implementation("android.arch.navigation:navigation-ui-ktx:$navigation")
    //Navigation viewmodel
    val navigationView = rootProject.extra["navigation_viewmodel_version"]
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationView")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationView")
    //Dagger-Hilt
    val dagger = rootProject.extra["dagger_version"]
    val daggerViewModel = rootProject.extra["dagger_viewmodel_version"]
    implementation("com.google.dagger:hilt-android:$dagger")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$daggerViewModel")
    kapt("com.google.dagger:hilt-android-compiler:$dagger")
    kapt("androidx.hilt:hilt-compiler:$daggerViewModel")
    //Retrofit
    val retrofit = rootProject.extra["retrofit_version"]
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    //Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    //Anko
    implementation("org.jetbrains.anko:anko:0.10.8")
    //Lifecycle
    val lifecycle = rootProject.extra["lifecycle_version"]
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")//2.3.0
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle")//2.3.0
    //Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:4.3.0")
}