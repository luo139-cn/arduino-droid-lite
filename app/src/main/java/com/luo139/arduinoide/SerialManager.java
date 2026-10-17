package com.luo139.arduinoide;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.io.IOException;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

/**
 * USB 串口封装。供烧录和串口监视器共用。
 */
public class SerialManager {

    public interface Listener {
        void onData(byte[] data);
        void onError(String message);
        void onStatus(String message);
    }

    private UsbManager usbManager;
    private UsbSerialPort port;
    private android.hardware.usb.UsbDeviceConnection connection;
    private SerialInputOutputManager ioManager;
    private Listener listener;

    public SerialManager(Context context) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean connect() {
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if (drivers.isEmpty()) {
            status("未找到 USB 串口设备，请检查 OTG 连接");
            return false;
        }
        UsbSerialDriver driver = drivers.get(0);
        if (!usbManager.hasPermission(driver.getDevice())) {
            status("无 USB 权限，请重新插拔授权");
            return false;
        }
        connection = usbManager.openDevice(driver.getDevice());
        if (connection == null) {
            status("无法打开 USB 设备");
            return false;
        }
        port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            status("串口已连接");
            return true;
        } catch (IOException e) {
            status("打开串口失败：" + e.getMessage());
            close();
            return false;
        }
    }

    public void setBaudRate(int baud) {
        if (port == null) return;
        try {
            port.setParameters(baud, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            status("波特率 " + baud);
        } catch (IOException e) {
            status("设置波特率失败：" + e.getMessage());
        }
    }

    public void write(byte[] data) {
        if (port == null) { status("串口未连接"); return; }
        try {
            port.write(data, 1000);
        } catch (IOException e) {
            status("写入失败：" + e.getMessage());
        }
    }

    public void startReading() {
        if (port == null) return;
        ioManager = new SerialInputOutputManager(port, new SerialInputOutputManager.Listener() {
            @Override public void onNewData(byte[] data) {
                if (listener != null) listener.onData(data);
            }
            @Override public void onRunError(Exception e) {
                if (listener != null) listener.onError(e.getMessage());
            }
        });
        ioManager.start();
    }

    public void stopReading() {
        if (ioManager != null) { ioManager.stop(); ioManager = null; }
    }

    public void close() {
        stopReading();
        if (port != null) {
            try { port.close(); } catch (IOException ignored) {}
            port = null;
        }
        if (connection != null) { connection.close(); connection = null; }
    }

    private void status(String msg) {
        if (listener != null) listener.onStatus(msg);
    }
}
