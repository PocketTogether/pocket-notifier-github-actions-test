```
我想开始研究kotlin开发安卓app
我想做一个很简单的安卓app，用于轮询http/https请求，要保证轮询稳定性
只有一个页面，居中显示一个图片，如果上次请求是失败的则显示另一个图片
每次轮询后，会让手机有通知

给我
完整的kotlin安卓app项目
完整的 OkHttp 轮询服务（ForegroundService）
完整的通知系统
完整的 GitHub Actions 自动打包脚本
完整的 Gradle 配置（尽量小的APK）
完整的项目目录结构

轮询间隔和请求路径，要写在配置文件类似前端项目的config.ts中以方便管理
轮询间隔60秒
请求超时时间10秒
请求路径 https://sakiko.top/api/collections/messages/records?page=1&perPage=40&expand=author&sort=-created%2Cid&skipTotal=true

给我标明常改的地方，如应用id、应用名、应用图标

项目地址 https://github.com/PocketTogether/pocket-notifier
应用名为 PocketNotifier
短一点的应用名为 Notifier

你已经理解我想要实现的功能与目标
基于这些前提，可以开始内容生成

因为你并不能一次生成太长太长的内容，所以我希望你给我多个 线性指挥生成口令
为了让我之后可以用线性的、可执行的、详细的口令来指挥你一次次生成内容

线性指挥生成口令
- 长一点、详细一点的指挥口令
- 口令必须包含：
  - 编号
  - 这一次的目标
  - 这一次包含的文件
  - 这一次某些规则或注意事项
- 口令必须足够详细，让我未来只需要说“按xxx口令执行 + 口令内容”你就能生成对应内容
```

太好了，你的目标非常清晰，而且完全符合你一贯的工程风格：**机制化、可复用、可扩展、可 fork、可自动化**。  
你要的是 **一整套可线性执行的“指挥口令”**，未来你只需要说：

> 按 X 号口令执行

我就会生成对应那一批文件。

为了让整个项目生成过程 **可控、可分段、可复现、可继续扩展**，我会为你设计 **8～12 个线性指挥口令**，每个口令都包含：

- **编号**
- **本次目标**
- **本次要生成的文件列表**
- **本次生成规则与注意事项**
- **你未来只需说“按 X 号口令执行”即可生成对应内容**

下面我先给你 **完整的线性指挥口令列表**。  
你确认后，我再开始逐条为你生成内容。

---

# ✅ PocketNotifier 项目：线性指挥生成口令（总览）

以下是我为你设计的 **完整线性生成流程**，每条都足够详细，未来你只需说“按 X 号口令执行”。

---

# **📦 口令 1 — 初始化项目结构**

**编号：1**

**目标：**  
生成整个 Android 项目的基础目录结构（空文件 + 占位符），包括 app 模块、配置文件、资源目录、服务目录、通知目录等。

**包含文件：**

- `settings.gradle.kts`
- `build.gradle.kts`（Project 级）
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/`（空目录结构）
- `app/src/main/java/.../`（空包结构）
- `app/src/main/assets/`（空）
- `.gitignore`

**规则与注意事项：**

- 应用 ID 使用占位符：`com.pocket.notifier`（你之后可修改）
- 项目名：`PocketNotifier`
- 目录结构必须完整、可直接导入 Android Studio
- 不生成任何 Kotlin 代码，只生成结构与空文件
- 标注所有未来会修改的关键位置（如 applicationId）

---

# **📦 口令 2 — 生成 Config.kt（类似前端 config.ts）**

**编号：2**

**目标：**  
生成集中配置文件 `Config.kt`，包含轮询间隔、请求路径、超时时间等。

**包含文件：**

- `app/src/main/java/.../config/Config.kt`

**规则与注意事项：**

- 轮询间隔：60 秒
- 超时时间：10 秒
- 请求路径写死为你提供的 URL
- 所有配置必须是 `const val`
- 文件顶部标注“常改位置”

---

# **📦 口令 3 — 生成 MainActivity + UI（单页面 + 图片切换）**

**编号：3**

**目标：**  
生成一个简单 Activity，居中显示图片，根据上次请求成功/失败切换。

**包含文件：**

- `MainActivity.kt`
- `activity_main.xml`
- `res/drawable/success.png`（占位说明）
- `res/drawable/failure.png`（占位说明）

**规则与注意事项：**

- 使用 ViewBinding
- 图片资源只写占位说明，不生成真实图片
- Activity 不包含业务逻辑，只负责 UI

---

# **📦 口令 4 — 生成 ForegroundService（OkHttp 轮询服务）**

**编号：4**

**目标：**  
生成完整的 ForegroundService，使用 OkHttp 轮询 HTTP 请求，保证稳定性。

**包含文件：**

- `PollingService.kt`
- `ServiceStarter.kt`（用于启动服务）
- `AndroidManifest.xml`（追加 service 声明）

**规则与注意事项：**

- 使用协程 + OkHttp
- 每 60 秒轮询一次
- 超时 10 秒
- 请求失败时写入 SharedPreferences
- 请求成功时写入 SharedPreferences
- Service 必须是 ForegroundService（Android 13+ 兼容）

---

# **📦 口令 5 — 生成通知系统（Notification Channel + 通知内容）**

**编号：5**

**目标：**  
生成通知系统，每次轮询后发送通知。

**包含文件：**

- `NotificationHelper.kt`
- `res/layout/notification_small.xml`（可选）
- `AndroidManifest.xml`（追加权限）

**规则与注意事项：**

- Android 8+ 必须创建 NotificationChannel
- 通知内容包含：成功/失败状态 + 时间戳
- ForegroundService 的通知与轮询通知分开

---

# **📦 口令 6 — 生成 SharedPreferences 状态存储**

**编号：6**

**目标：**  
生成一个简单的状态存储，用于记录上次请求是否成功。

**包含文件：**

- `StatusStore.kt`

**规则与注意事项：**

- 使用 `Context.getSharedPreferences`
- 提供 `getLastStatus()` 和 `setLastStatus()`

---

# **📦 口令 7 — 生成 Gradle 配置（最小 APK）**

**编号：7**

**目标：**  
生成完整 Gradle 配置，优化 APK 体积。

**包含文件：**

- `build.gradle.kts`（Project）
- `app/build.gradle.kts`

**规则与注意事项：**

- 启用 R8
- 启用 minifyEnabled
- 使用最小依赖
- Kotlin + OkHttp + Coroutines
- 标注可修改的 applicationId、versionName、versionCode

---

# **📦 口令 8 — 生成 GitHub Actions 自动打包脚本**

**编号：8**

**目标：**  
生成 GitHub Actions CI/CD，用于自动构建 APK。

**包含文件：**

- `.github/workflows/android.yml`

**规则与注意事项：**

- 使用 actions/setup-java
- 使用 gradlew assembleRelease
- 输出 APK 到 artifacts
- 标注可修改的 keystore 位置（可选）

---

# **📦 口令 9 — 生成 README.md（项目说明）**

**编号：9**

**目标：**  
生成 README，包含：

- 项目介绍
- 目录结构
- 如何修改配置
- 如何构建 APK
- 如何运行服务
- 如何 fork

**包含文件：**

- `README.md`

**规则与注意事项：**

- 结构清晰
- 标注常改位置（应用名、图标、ID）

---

# **📦 口令 10 — 生成最终项目目录树（含所有文件）**

**编号：10**

**目标：**  
生成最终完整目录树，便于你对照 GitHub 仓库。

**包含文件：**

- 整个项目结构（树状）

**规则与注意事项：**

- 必须与前面所有口令生成的文件一致
- 标注可修改位置

---

# 🎉 现在请你确认：

你是否要我开始按顺序生成？

如果你回复：

> 按 1 号口令执行

我就会开始生成 **项目初始化结构**。

你也可以说：

> 我想调整口令内容

我可以继续优化。