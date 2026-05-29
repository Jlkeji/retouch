# Retouch - Photo & Video Retouch App

完整的 Android 照片/视频修复及广告变现应用，支持物体移除、水印去除、会员权益管理和 AdMob 变现。

## 功能特性

### 📸 照片编辑
- ✅ 物体/瑕疵移除（涂抹遮罩+边界采样+模糊填充）
- ✅ 克隆工具（复制区域像素）
- ✅ 撤销/重做功能
- ✅ 高清导出（会员/激励广告解锁）
- ✅ 一键分享

### 🎬 视频处理
- ✅ 视频水印去除（区域模糊处理）
- ✅ H.264/AAC MP4 硬编码处理
- ✅ 无限制导出（会员权益）

### 💳 会员系统
- ✅ 本地会员状态管理
- ✅ 激励广告解锁权益（1小时期限）
- ✅ 会员免广告体验

### 📺 广告变现
- ✅ AdMob Banner 广告（首页+编辑页）
- ✅ Interstitial 插屏广告（导出后触发，90秒频控）
- ✅ Rewarded 激励广告（高清/视频导出解锁）
- ✅ UMP 隐私同意流程

## 项目结构

```
app/
├── src/main/
│   ├── java/com/dfjk/retouch/
│   │   ├── MainActivity.kt              # 首页
│   │   ├── ads/
│   │   │   └── AdManager.kt             # AdMob 管理
│   │   ├── member/
│   │   │   ├── MembershipStore.kt       # 会员状态
│   │   │   └── MembershipActivity.kt    # 会员页
│   │   ├── photo/
│   │   │   ├── MaskCanvasView.kt        # 涂抹画布
│   │   │   ├── RetouchEngine.kt         # 处理引擎
│   │   │   └── PhotoEditorActivity.kt   # 编辑页
│   │   └── video/
│   │       └── VideoWatermarkActivity.kt
│   ├── res/layout/                      # 布局文件
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## 快速开始

### 编译
```bash
./gradlew clean :app:assembleDebug
```

### 安装
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 运行
```bash
adb shell am start -n com.dfjk.retouch/.MainActivity
```

## 核心技术

- **图像处理**: Bitmap 边界采样 + 高斯模糊 + 克隆修补
- **视频处理**: MediaCodec 硬编码 (H.264/AAC)
- **广告变现**: AdMob + UMP 隐私同意
- **会员系统**: SharedPreferences 本地存储

## 参数配置

| 参数 | 值 |
|-----|-----|
| Min SDK | 23 |
| Target SDK | 35 |
| Package | com.dfjk.retouch |
| JDK | 11+ |

## 下一步

1. 申请真实 AdMob ID
2. 集成 Google Billing (IAP)
3. 优化图像算法效果
4. 上线 Google Play

## 许可证

MIT License