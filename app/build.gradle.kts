plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.pocket.notifier"
    compileSdk = 34

    defaultConfig {
        // 修改应用 ID
        applicationId = "top.uika.pocket_notifier"
        // 修改版本数字
        versionCode = 1
        // 修改版本号
        versionName = "0.0.1"

        minSdk = 24
        targetSdk = 34
        multiDexEnabled = true
    }

    // 使用 ViewBinding
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin JVM 目标
    kotlinOptions {
        jvmTarget = "17"
    }

    // 兼容旧设备
    packaging {
        resources.excludes += "META-INF/*"
    }
}

dependencies {

    // Kotlin 标准库
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // AndroidX 基础库
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // ViewBinding 不需要额外依赖

    // 通知需要的依赖
    implementation("com.google.android.material:material:1.11.0")
}