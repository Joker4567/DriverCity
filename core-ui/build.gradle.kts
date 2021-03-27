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
    implementation(project(":utils"))
    implementation(project(":drawable"))
    implementation(project(":core-date"))

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.activity:activity-ktx:1.2.1")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.guava:guava:27.0.1-android")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("org.litepal.android:core:1.4.1")
    //Activity result API
    val ktx = rootProject.extra["ktx_version"]
    implementation("androidx.fragment:fragment:$ktx")
    implementation("androidx.fragment:fragment-ktx:$ktx")
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
    //Lifecycle
    val lifecycle = rootProject.extra["lifecycle_version"]
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")//2.3.0
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle")//2.3.0
    //Dagger-Hilt
    val dagger = rootProject.extra["dagger_version"]
    val daggerViewModel = rootProject.extra["dagger_viewmodel_version"]
    val daggerWork = rootProject.extra["hilt_work_version"]
    implementation("com.google.dagger:hilt-android:$dagger")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:$daggerViewModel")
    kapt("com.google.dagger:hilt-android-compiler:$dagger")
    kapt("androidx.hilt:hilt-compiler:$daggerViewModel")
    implementation("androidx.hilt:hilt-work:$daggerWork")
    kapt("androidx.hilt:hilt-compiler:$daggerWork")
    //WorkManager
    implementation("androidx.work:work-runtime-ktx:2.5.0")
    //location
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    //Logs
    implementation("com.jakewharton.timber:timber:4.7.1")
}