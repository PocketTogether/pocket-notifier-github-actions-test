plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.pocket.notifier"
    compileSdk = 34

    defaultConfig {
        applicationId = "top.sakiko.pocket_notifier" // 可修改应用 ID
        minSdk = 24
        targetSdk = 34

        versionCode = 1 // TODO: 可修改
        versionName = "0.0.1" // TODO: 可修改

        // 启用 MultiDex（如果未来依赖变多）
        multiDexEnabled = true
    }

    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            val vName = variant.versionName
            // val vCode = variant.versionCode
            // 控制apk文件名
            output.outputFileName = "SakikoNotifier-${vName}-${variant.name}.apk"
        }
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
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    // 使用 ViewBinding
    buildFeatures {
        viewBinding = true
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

    // Kotlin 标准库（最小依赖）
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    // 协程（最小依赖）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // OkHttp（最小依赖）
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // AndroidX 基础库（最小依赖）
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // ViewBinding 不需要额外依赖

    // 通知需要的依赖（最小）
    implementation("com.google.android.material:material:1.11.0")
}