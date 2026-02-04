下面是 **按 1 号口令执行** 后生成的内容。  
完全符合你的工程风格：**结构清晰、可 fork、可扩展、可维护、零业务逻辑、仅结构**。

---

# 📁 PocketNotifier — 初始化项目结构（可直接导入 Android Studio）

以下内容为 **完整目录结构 + 所有空文件内容**。  
所有未来需要修改的关键位置我都已用 `// TODO:` 标注。

---

# 📄 `settings.gradle.kts`

```kotlin
rootProject.name = "PocketNotifier" // TODO: 可修改项目名

include(":app")
```

---

# 📄 `build.gradle.kts`（Project 级）

```kotlin
// 顶级 Gradle 配置（无业务逻辑）

plugins {
    // TODO: 根据需要添加插件（如 Kotlin、Android）
    // 在后续口令中会补全
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

---

# 📄 `app/build.gradle.kts`

> **注意：此处仅创建空文件结构，不包含任何依赖或配置**  
> 完整配置会在 **口令 7** 中生成。

```kotlin
// TODO: 在口令 7 中补全完整的 Android Gradle 配置

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.pocket.notifier" // TODO: 可修改应用 ID
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pocket.notifier" // TODO: 可修改应用 ID
        minSdk = 24
        targetSdk = 34
        versionCode = 1 // TODO: 可修改
        versionName = "1.0.0" // TODO: 可修改
    }
}

dependencies {
    // TODO: 在口令 7 中补全依赖
}
```

---

# 📄 `app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.pocket.notifier"> <!-- TODO: 可修改应用 ID -->

    <application
        android:label="PocketNotifier"  <!-- TODO: 可修改应用名 -->
        android:icon="@mipmap/ic_launcher"> <!-- TODO: 替换应用图标 -->

        <!-- Activity、Service、Receiver 将在后续口令中补充 -->

    </application>

</manifest>
```

---

# 📁 `app/src/main/res/`（空目录结构）

```
res/
 ├── drawable/            # 图片资源（success/failure 图标将在口令 3 中添加）
 ├── layout/              # activity_main.xml 将在口令 3 中生成
 ├── mipmap-anydpi-v26/   # 应用图标（可替换）
 ├── mipmap-hdpi/
 ├── mipmap-mdpi/
 ├── mipmap-xhdpi/
 ├── mipmap-xxhdpi/
 ├── mipmap-xxxhdpi/
 ├── values/
 │    ├── colors.xml      # 默认空
 │    ├── themes.xml      # 默认空
 │    └── strings.xml     # 默认空
```

---

# 📁 `app/src/main/java/com/pocket/notifier/`（空包结构）

```
java/com/pocket/notifier/
 ├── ui/              # MainActivity 将在口令 3 中生成
 ├── service/         # ForegroundService 将在口令 4 中生成
 ├── notification/    # NotificationHelper 将在口令 5 中生成
 ├── config/          # Config.kt 将在口令 2 中生成
 └── store/           # StatusStore.kt 将在口令 6 中生成
```

---

# 📁 `app/src/main/assets/`（空）

```
assets/
  （空）
```

---

# 📄 `.gitignore`

```gitignore
# Gradle
.gradle/
build/

# Local configuration
local.properties

# Android Studio
.idea/
*.iml

# Generated files
captures/
output.json

# APKs
*.apk
*.aab
```

---

# 🎉 初始化项目结构已完成

你现在已经拥有一个 **可直接导入 Android Studio 的完整空项目骨架**。

下一步你可以继续说：

> 按 2 号口令执行

我将为你生成 **Config.kt（类似前端 config.ts）**，并标注所有可修改位置。