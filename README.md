# ArduinoDroid Lite

一个面向旧安卓设备的 Arduino/ESP 移动端 IDE（对标 ArduinoDroid 的轻量复刻）。

## 特性

- 代码编辑器（含示例 sketch）
- 支持开发板：Arduino Uno / Nano / Mega 2560 / **Leonardo / Micro / Pro Micro (32U4)** / ESP8266 / ESP32
- 联网编译（arduino-cli 后端）
- USB OTG 串口烧录（AVR 走 avrdude，ESP 走 esptool）
- 串口监视器

## 编译后端（需自行部署）

App 默认请求 `http://192.168.1.100:8080/compile`，接口约定：

```
POST /compile
body: {"fqbn":"arduino:avr:uno", "code":"void setup(){} void loop(){}"}
resp: {"ok":true, "firmware":"...", "type":"hex", "log":"..."}
```

后端可用 `arduino-cli` 实现，示例见 `server/` 目录（待补充）。

## 构建 APK

用 Android Studio 打开本项目，或命令行：

```bash
./gradlew assembleDebug
```

产物在 `app/build/outputs/apk/debug/`。

## 说明

烧录协议主干已就绪（STK500 / avr109 / esptool 握手框架），需在真机 OTG 串口上调通，尤其是 32U4 的 1200bps 触发 bootloader 逻辑。

> 本项目仅供学习交流，非官方 Arduino 团队产品。
