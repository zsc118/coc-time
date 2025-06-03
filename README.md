# 部落冲突倒计时器

## 项目概述

部落冲突倒计时器是一个 Android 应用程序，用于管理和加速特定项目的时间。该应用允许用户添加、编辑和删除项目，设置不同的加速参数，如学徒、助手和钟楼的加速效果。

## 项目结构
```
.gitignore
build.gradle.kts
proguard-rules.pro
src/
|-- androidTest/
|-- main/
|-- test/
```

### 主要文件和目录说明
- `.gitignore`: Git 忽略文件，用于指定不需要版本控制的文件和目录。
- `build.gradle.kts`: Gradle 构建脚本，用于配置项目的构建过程。
- `proguard-rules.pro`: ProGuard 混淆规则文件，用于保护应用程序代码。
- `src/`: 源代码目录，包含以下子目录：
  - `androidTest/`: 安卓仪器化测试代码目录。
  - `main/`: 主要源代码目录，包含 `AndroidManifest.xml`、Java 代码和资源文件。
  - `test/`: 单元测试代码目录。

## 主要功能模块
### 主界面 (`MainActivity.java`)
- 加载和保存设置文件 `settings.ser`。
- 显示项目列表，支持添加、编辑、删除项目。
- 提供加速功能，如钟楼加速、学徒加速和助手加速。
- 提供设置界面入口，用于设置加速参数。
- 自动请求通知权限
- 为每个项目设置定时通知
- 当项目时间到达时触发通知

### 项目编辑界面 (`ItemEditActivity.java`)
- 允许用户编辑项目的账户类型、项目名称、时间和类型。
- 支持保存和取消操作。

### 设置界面 (`SettingsActivity.java`)
- 允许用户设置建筑、实验室和钟楼的加速等级。
- 支持保存和取消操作。

### 适配器 (`Adapter.java`)
- 用于将项目列表数据绑定到 `ListView` 上。

### 项目类 (`Item.java`)
- 表示一个项目，包含账户类型、项目名称、时间和类型等属性。
- 提供时间解析和格式化方法。

## 运行和测试
### 运行项目
1. 确保你已经安装了 Android Studio 和相应的 Android SDK。
2. 打开 Android Studio，导入项目 。
3. 连接 Android 设备或启动模拟器。
4. 点击 Android Studio 中的运行按钮，选择目标设备，运行项目。

### 测试项目
- 仪器化测试：在 `src/androidTest/java/com/example/coctime/ExampleInstrumentedTest.java` 中编写和运行仪器化测试。
- 单元测试：在 `src/test/java` 目录下编写和运行单元测试。

## 注意事项
- 该应用使用了 Java 8 的 `LocalDateTime` 类，因此需要 Android API 级别 26 或更高版本。
- 确保 `settings.ser` 文件的读写权限正确，否则可能会导致设置加载和保存失败。