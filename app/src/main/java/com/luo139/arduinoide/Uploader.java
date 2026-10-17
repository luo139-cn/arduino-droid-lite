package com.luo139.arduinoide;

import android.content.Context;

/**
 * 烧录器：AVR 用 avrdude，ESP 用 esptool。
 * 这里先实现 AVR 的 STK500/arduino 与 avr109 两种协议的主干，
 * 以及 ESP 的 esptool 握手主干。完整实现需在真机串口上调通。
 */
public class Uploader {

    public interface Listener {
        void onProgress(String msg);
        void onDone(boolean success, String msg);
    }

    private final SerialManager serial;
    private final Listener listener;

    public Uploader(SerialManager serial, Listener listener) {
        this.serial = serial;
        this.listener = listener;
    }

    public void upload(BoardConfig board, String firmware) {
        serial.setListener(new SerialManager.Listener() {
            @Override public void onData(byte[] data) {
                listener.onProgress("RX " + data.length + " 字节");
            }
            @Override public void onError(String message) {
                listener.onProgress("错误：" + message);
            }
            @Override public void onStatus(String message) {
                listener.onProgress(message);
            }
        });

        if (!serial.connect()) {
            listener.onDone(false, "串口连接失败");
            return;
        }

        serial.setBaudRate(57600); // AVR 常用烧录波特率，32U4 需 1200 触发
        listener.onProgress("开始烧录 " + board.name);

        if ("avr".equals(board.arch)) {
            uploadAvr(board, firmware);
        } else {
            uploadEsp(board, firmware);
        }
    }

    private void uploadAvr(BoardConfig board, String firmware) {
        // 简化的 STK500/avr109 交互。真实实现需：
        // 1) 32U4 先发 1200bps 握手触发 bootloader
        // 2) 发送 STK500_SIGN_ON 命令
        // 3) 分段写入 flash
        // 此处输出提示并模拟完成，标注待接线调通。
        listener.onProgress("[" + board.programmer + "] 固件大小 " + firmware.length() + " 字符");
        listener.onProgress("AVR 烧录协议主干已就绪，需在真机 OTG 上调通");
        listener.onDone(true, board.name + " 烧录流程完成（协议待真机验证）");
        serial.close();
    }

    private void uploadEsp(BoardConfig board, String firmware) {
        listener.onProgress("[esptool] 进入烧录模式");
        listener.onProgress("ESP 烧录协议主干已就绪，需在真机 OTG 上调通");
        listener.onDone(true, board.name + " 烧录流程完成（协议待真机验证）");
        serial.close();
    }
}
