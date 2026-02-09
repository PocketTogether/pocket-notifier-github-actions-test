<div align="center">
  <img src="./resources/icon1-notif.png" style="width: 100px; height: 100px;">
</div>

<h1 align="center">
  PocketNotifier
</h1>

<p align="center">
  <!-- Kotlin -->
  <a href="https://kotlinlang.org/" target="_blank"><img src="https://img.shields.io/badge/Kotlin-7f52ff.svg?logo=kotlin&logoColor=white&style=for-the-badge" alt="Kotlin"/></a>
  <!-- Android -->
  <a href="https://developer.android.com/" target="_blank"><img src="https://img.shields.io/badge/Android-34a853?logo=android&logoColor=white&style=for-the-badge" alt="Android"/></a>
  <!-- PocketBase -->
  <a href="https://pocketbase.io/" target="_blank"><img src="https://img.shields.io/badge/PocketBase-0E83CD?style=for-the-badge&logo=pocketbase&logoColor=white" alt="PocketBase"></a>
  <!-- License -->
  <a href="https://opensource.org/licenses/MIT" target="_blank"><img src="https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge" alt="MIT License"></a>
  </br>
  <!-- Discord -->
  <a href="https://discord.gg/aZq6u3Asak"><img alt="Discord" src="https://img.shields.io/discord/1192346949635026944?logo=discord&logoColor=white&label=Discord&color=4285F4&style=for-the-badge"></a>
  <!-- Telegram -->
  <a href="https://t.me/PocketTogether"><img alt="Telegram" src="https://img.shields.io/badge/Telegram-JOIN-188FCA.svg?logo=telegram&logoColor=white&style=for-the-badge"></a>
</p>

<p align="center">
  <a href="./README_EN.md">English</a> | 简体中文
</p>

- PocketNotifier 是一个用于实时通知的安卓项目，配套于开源聊天平台 [PocketChat](https://github.com/PocketTogether/pocket-chat)
- 是使用 Kotlin 开发的原生安卓应用，安装包只有 1.6MB 左右，运行时的内存占用也很少
- 在手机后台实时获取聊天新消息并发送通知，延迟视网络情况，一般低于一秒甚至半秒
- 主要通过 SSE (Server-Sent Events) 来实现实时获取，此外还有一个间隔两分半的轮询，保证不会漏消息
- App关闭至App重新打开时，会请求获取历史消息，并通知用户在App关闭期间的新消息数量
- 本项目目前的配置都是针对 [sakiko.top](https://sakiko.top) 的，fork 本项目后可为其他 PocketChat 网站构建其专用的 App
- 已配置 github actions ，不必在本地配置环境或拉取代码，可全程在 github 上修改项目配置并打包 apk
- 项目地址 https://github.com/PocketTogether/pocket-notifier

![](./assets/collage-260208-1748.jpeg)
![](./assets/collage-260208-1749.jpeg)


## 使用说明

在本项目的 Releases 页面，可下载安装包，如 `SakikoNotifier-v0.0.1.apk`

安装并打开App后，可以看到有 主图标 和 设置图标
- 点击主图标可跳转至对应网站。此外主图标还负责表示App运行是否正常
- 点击设置图标能跳转至手机系统的本App信息页面，便于配置通知相关功能


### 设置允许通知

请点击设置图标，将会跳转至手机系统的本App信息页面

请设置其允许通知，还可以调整不同类型的通知的行为

有三种类型的通知
- `New Message Notification` 新消息通知，有新消息时会发送新消息通知
- `Error Notification` 错误通知，在网络错误等错误时会发送错误通知
- `Background Service Notification` App后台运行通知，提示用户App正在后台运行


### 确认App正常运行
通过主图标可判断 App 运行是否正常，正常时主图标是带红点的，不正常时主图标是不带红点的

<table>
  <tr>
    <th><strong>请求成功（正常运行）</strong></th>
    <th><strong>初始请求未完成 / 请求失败</strong></th>
  </tr>
  <tr>
    <td align="center"><img src="./app/src/main/res/drawable/success.png" alt="success" style="width: 100px; height: 100px;" /></td>
    <td align="center"><img src="./app/src/main/res/drawable/failure.png" alt="failure" style="width: 100px; height: 100px;" /></td>
  </tr>
  <tr>
    <td >表示最近一次请求成功，App 处于正常状态，主图标是带红点的</td>
    <td >表示尚未成功获取数据或最近一次请求失败，主图标是不带红点的</td>
  </tr>
</table>


### 确保App不被系统中止
不同厂商的 Android 系统会对后台进程进行额外管理。为了确保 App 能持续运行，建议进行以下配置

#### 1. 在“最近任务”界面将 App **锁定**
打开“最近任务”（上滑手势、长按中键或方形键），找到本应用，在应用卡片上点击“小锁”图标，将其设为**锁定状态**。

锁定后的应用不会被系统的一键清理或后台策略轻易终止。

#### 2. 启用 **自启动权限**
进入系统设置 → 应用管理 → 权限管理 → 自启动  

将本应用加入允许自启动的列表，以便在系统回收后能够自动恢复运行。

#### 3. 允许后台运行 / 关闭电池优化
不同品牌的系统名称略有差异，请根据实际机型找到对应选项：

- **电池 → 后台高耗电 → 允许后台运行**
- **电池管理 → 不优化电池 / 允许后台活动**
- **省电策略 → 允许后台运行**
- **应用信息 → 电池 → 不受限制**

> PocketNotifier 在后台的耗电应该是很少的，不过还是建议允许其在后台高耗电时继续运行

## 开发指南

本项目目前的配置都是针对 https://sakiko.top 的，即只会获取 sakiko.top 这个 PocketChat 网站的通知

fork 本项目后可为其他 PocketChat 网站构建其专用的 App

已配置 github actions ，不必在本地配置环境或拉取代码，可全程在 github 上修改项目配置并打包 apk

下面是fork与项目修改示例，以为 https://uika.top 构建专用的通知App为例：

### 1. fork本项目

fork 本项目 https://github.com/PocketTogether/pocket-notifier

打开自己 fork 后的 pocket-notifier 项目，如 `https://github.com/<USERNAME>/pocket-notifier`


### 2. 项目文件修改
主要有 4 个文件需要修改
1. `app/build.gradle.kts` 修改应用id
2. `app/src/main/AndroidManifest.xml` 修改应用名称
3. `app/src/main/java/com/pocket/notifier/config/Config.kt` 修改网址配置
4. `.github/workflows/android.yml` 修改apk文件的文件名


#### (1) 修改应用id

```
app/build.gradle.kts
```

```kotlin
// ...
android {
    // ...
    defaultConfig {
        // 应用 ID，应修改
        applicationId = "top.uika.pocket_notifier"

        // 版本数字，可修改
        versionCode = 1
        // 版本号，可修改
        versionName = "0.0.1"

        // ...
    }
    // ...
}
```

应用ID（applicationId）
- 必须包含至少一个点号 `.`
- 只能使用字母、数字、下划线 `_` 和点号 `.`
- 不需要是真实域名，也不需要你拥有这个域名
- 只要 applicationId 不同，系统就会把它当成完全独立的应用

版本号（versionCode / versionName）
- versionCode：必须是整数，每次发布新版本都要递增
  例如：1 → 2 → 3  
  Android 用它判断“哪个版本更 新”。
- versionName：给用户看的版本号，格式随意
  例如：`0.0.1`、`1.2.0`、`2025.01.01`  
  只要是字符串就行，没有强制格式。


#### (2) 修改应用名称

```
app/src/main/AndroidManifest.xml
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- ... -->
    <!-- android:label 就是应用名，应修改 -->
    <application
        android:label="UikaNotifier"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.PocketNotifier">
    <!-- ... -->
    </application>
</manifest>
```

`android:label="UikaNotifier"` 就是应用名，应修改。

关于 `android:icon="@mipmap/ic_launcher"` ，这个是App图标，如果想修改的话，替换以下图片文件即可，注意要确保图片尺寸正确
```
48x48px
app/src/main/res/mipmap-mdpi/ic_launcher.png

72x72px
app/src/main/res/mipmap-hdpi/ic_launcher.png

96x96px
app/src/main/res/mipmap-xhdpi/ic_launcher.png

144x144px
app/src/main/res/mipmap-xxhdpi/ic_launcher.png

192x192px
app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
```

关于其他图片文件，存放在 `app/src/main/res/drawable` ，也可以修改
```
主页中，请求失败时显示的图片
app/src/main/res/drawable/failure.png

主页中，请求成功时显示的图片
app/src/main/res/drawable/success.png

通知图标SmallIcon
app/src/main/res/drawable/ic_notify.xml
```


#### (3) 修改网址配置

```
app/src/main/java/com/pocket/notifier/config/Config.kt
```

主要修改 `POCKETCHAT_BASE_URL` 即可

```kotlin
object Config {
    /**
     * 网站url，应修改
     */
    const val POCKETCHAT_BASE_URL: String = "https://uika.top"

    /** 点击主图片时打开的网址 */
    const val CLICK_URL: String = "${POCKETCHAT_BASE_URL}"

    /** 轮询请求路径 */
    const val REQUEST_URL: String =
        "${POCKETCHAT_BASE_URL}/api/collections/messages/records?page=1&perPage=20&expand=author&sort=-created%2Cid&skipTotal=true"

    /** SSE 实时连接地址（GET /api/realtime & POST /api/realtime） */
    const val REALTIME_URL: String = "${POCKETCHAT_BASE_URL}/api/realtime"

    /** 请求超时时间（秒） */
    const val REQUEST_TIMEOUT_SECONDS: Long = 10

    /** 轮询间隔（秒） */
    const val POLLING_INTERVAL_SECONDS: Long = 150

    /**
     * 单次 SSE 会话时长（秒）
     *
     * 浏览器端是约 1 分钟断开重连，好像是cf导致的，这里再稍短一些更可控
     */
    const val REALTIME_SESSION_SECONDS: Long = 55

    /**
     * SSE 订阅字符串
     * options={"query":{"expand":"author"}}
     */
    const val REALTIME_SUBSCRIPTION: String =
        "messages/*?options=%7B%22query%22%3A%7B%22expand%22%3A%22author%22%7D%7D"

    /** 本地最多存储多少条消息（超过则触发清理） */
    const val MESSAGE_STORE_MAX = 200

    /** 清理后保留多少条最新消息 */
    const val MESSAGE_STORE_TRIM_TO = 100

}
```


#### (4) 修改apk文件的文件名

```
.github/workflows/android.yml
```

主要修改 `APK_BASENAME` 即可

```yml
name: Android Release Build

env: 
  # Github Release 中apk文件的文件名，应修改
  # 最终apk的基础文件名，将利用此拼接为如 APK_BASENAME-v0.0.1.apk
  APK_BASENAME: "PocketNotifier"

on:
  release:
    types: [published]
  workflow_dispatch:

# ...
```


### 3. Release与Apk打包

本项目已配置 GitHub Actions，详见 `.github/workflows/android.yml`。

APK 的构建流程会在 **发布 GitHub Release 时自动触发**：

1. 为自己 fork 后的本项目创建一个新的 Release  
2. 设置版本号（例如 `v0.0.1`）  
3. 点击 **Publish release**  
4. GitHub Actions 会自动开始构建 APK  
5. 打开 GitHub 网页顶部的 **Actions** 页面即可查看构建进度  
6. 构建完成后，APK 会自动上传到对应的 Release 页面  

整个流程无需本地环境，完全可以在 GitHub 上完成。


### 📌 补充说明：通过创建 GitHub Organization 来实现“多次 fork”

GitHub 的规则是：**同一个账号对同一个仓库只能 fork 一次。**

但如果你确实需要多个 fork（例如为多个 PocketChat 站点构建多个专用 App），可以通过创建免费的 GitHub Organization（组织） 来实现“多次 fork”

这样你就能拥有多个独立的 fork，每个 fork 都可以：

- 拥有自己的配置
- 拥有自己的 GitHub Actions 构建流程
- 拥有自己的 Release
- 保持与上游仓库的同步能力

这对 PocketNotifier 特别有用，每个站点都能拥有自己的专用通知 App。
