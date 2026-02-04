
# PocketNotifier

> 一个极简、稳定、可 fork 的 Android 后台轮询通知 App  
> 使用 Kotlin + OkHttp + ForegroundService + GitHub Actions 自动构建

---

## 📌 项目简介

PocketNotifier 是一个用于 **定时轮询 HTTP/HTTPS 请求并发送通知** 的 Android 应用。

特点：

- 后台稳定轮询（ForegroundService）
- OkHttp + 协程，可靠且轻量
- 每次轮询后发送成功/失败通知
- 单页面 UI，根据状态切换图片
- 配置集中化（类似前端 config.ts）
- GitHub Actions 自动构建 Release APK
- 结构极简，适合 fork、二次开发、私有化部署

---

## 🏗 项目结构

```

PocketNotifier/ ├── app/ │ ├── src/main/ │ │ ├── java/com/pocket/notifier/ │ │ │ ├── ui/ # MainActivity（UI） │ │ │ ├── service/ # PollingService（轮询服务） │ │ │ ├── notification/ # NotificationHelper（通知系统） │ │ │ ├── config/ # Config.kt（集中配置） │ │ │ └── store/ # StatusStore（状态存储） │ │ ├── res/ # 图片、布局、图标 │ │ └── AndroidManifest.xml │ ├── build.gradle.kts ├── .github/workflows/android.yml # GitHub Actions 自动构建 ├── build.gradle.kts # Project 级配置 ├── settings.gradle.kts └── README.md

```

---

## ⚙️ 修改配置（常改位置）

所有配置集中在：

```

app/src/main/java/com/pocket/notifier/config/Config.kt

````

你可以修改：

```kotlin
const val POLLING_INTERVAL_SECONDS = 60   // 轮询间隔
const val REQUEST_TIMEOUT_SECONDS = 10    // 超时时间
const val REQUEST_URL = "https://..."     // 请求路径
````

---

## 🎨 修改应用名 / 图标 / 应用 ID（常改位置）

### 应用名

```
app/src/main/AndroidManifest.xml
```

```xml
android:label="PocketNotifier"   <!-- TODO: 修改应用名 -->
```

### 应用图标

```
app/src/main/res/mipmap-*/ic_launcher.png
```

替换所有 mipmap 图标即可。

### 应用 ID（包名）

```
app/build.gradle.kts
```

```kotlin
applicationId = "com.pocket.notifier"   // TODO: 修改应用 ID
namespace = "com.pocket.notifier"       // TODO: 修改 namespace
```

---

## 🛠 构建 APK

本地构建：

```bash
./gradlew assembleRelease
```

构建产物位置：

```
app/build/outputs/apk/release/app-release.apk
```

---

## 🚀 GitHub Actions 自动构建

仓库包含自动构建脚本：

```
.github/workflows/android.yml
```

推送到 `main` 分支后，会自动：

- 构建 Release APK
- 上传到 GitHub Actions Artifacts

如需签名 APK，可在仓库 Secrets 中添加：

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_ALIAS_PASSWORD`

并在 workflow 中启用 keystore 解码步骤。

---

## ▶️ 如何运行轮询服务

在 `MainActivity` 或 `Application` 中调用：

```kotlin
ServiceStarter.start(context)
```

服务会：

- 启动 ForegroundService
- 每 60 秒轮询一次
- 写入成功/失败状态
- 发送通知

---

## 🍴 如何 Fork / 二次开发

1. Fork 仓库
2. 修改应用 ID（强烈建议）
3. 修改应用名、图标
4. 修改 `Config.kt` 中的 API 地址
5. 构建 APK 或使用 GitHub Actions 自动构建

本项目结构极简，适合作为：

- 私有通知 App
- 内网监控 App
- 服务器心跳监控
- 个人自动化提醒工具

---

## 📄 License

MIT License  
你可以自由 fork、修改、商用。

---

## ✨ 作者

PocketTogether / Haruki  
欢迎提交 PR 或 Issue。

