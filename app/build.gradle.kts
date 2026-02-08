plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.pocket.notifier"
    compileSdk = 34

    defaultConfig {
        // 应用 ID，应修改
        applicationId = "top.uika.pocket_notifier"

        // 版本数字，可修改
        versionCode = 1
        // 版本号，可修改
        versionName = "0.0.1"

        minSdk = 24
        targetSdk = 34
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true      // 启用 R8 压缩
            isShrinkResources = true    // 移除未使用资源
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = false        // 显式关闭 debuggable 让 debug 也能压缩
            isMinifyEnabled = true      // 启用 R8 压缩
            isShrinkResources = true    // 移除未使用资源
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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