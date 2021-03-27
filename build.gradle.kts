buildscript {
    val kotlin_version by extra("1.4.10")
    val dagger_version by extra("2.31.2-alpha")
    val dagger_viewmodel_version by extra("1.0.0-alpha02")
    val moshi_version by extra("1.9.3")
    val kotlin_coroutines by extra("1.4.1")
    val room_version by extra("2.2.6")
    val retrofit_version by extra("2.9.0")
    val okHttp_version by extra("4.9.0")
    val lifecycle_version by extra("2.2.0")
    val navigation_version by extra("1.0.0-rc02")
    val navigation_viewmodel_version by extra("2.3.1")
    val ktx_version by extra("1.3.1")
    val hilt_work_version by extra("1.0.0-beta01")

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$dagger_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            setUrl("https://maven.google.com/")
            name = "Google"
        }
    }
    val minSdkVersion by extra(23)
    val compileSdkVersion by extra(30)
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}