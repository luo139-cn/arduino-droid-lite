package com.luo139.arduinoide;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发板配置。AVR(含32U4) + ESP8266 + ESP32。
 */
public class BoardConfig {

    public final String id;
    public final String name;
    public final String arch;       // avr | esp8266 | esp32
    public final String fqbn;       // 交给编译服务器
    public final String programmer; // avrdude -c 协议

    public BoardConfig(String id, String name, String arch, String fqbn, String programmer) {
        this.id = id;
        this.name = name;
        this.arch = arch;
        this.fqbn = fqbn;
        this.programmer = programmer;
    }

    public static List<BoardConfig> all() {
        List<BoardConfig> list = new ArrayList<>();
        // AVR 经典
        list.add(new BoardConfig("uno", "Arduino Uno", "avr", "arduino:avr:uno", "arduino"));
        list.add(new BoardConfig("nano", "Arduino Nano", "avr", "arduino:avr:nano", "arduino"));
        list.add(new BoardConfig("mega2560", "Arduino Mega 2560", "avr", "arduino:avr:mega", "wiring"));
        // 32U4 家族 —— 原生USB CDC
        list.add(new BoardConfig("leonardo", "Arduino Leonardo (32U4)", "avr", "arduino:avr:leonardo", "avr109"));
        list.add(new BoardConfig("micro", "Arduino Micro (32U4)", "avr", "arduino:avr:micro", "avr109"));
        list.add(new BoardConfig("promicro16", "SparkFun Pro Micro 5V (32U4)", "avr", "sparkfun:avr:promicro", "avr109"));
        list.add(new BoardConfig("promicro8", "SparkFun Pro Micro 3.3V (32U4)", "avr", "sparkfun:avr:promicro8", "avr109"));
        // ESP
        list.add(new BoardConfig("esp8266", "ESP8266 (NodeMCU)", "esp8266", "esp8266:esp8266:nodemcuv2", "esptool"));
        list.add(new BoardConfig("esp32", "ESP32 Dev Module", "esp32", "esp32:esp32:esp32", "esptool"));
        return list;
    }

    public static BoardConfig byId(String id) {
        for (BoardConfig b : all()) if (b.id.equals(id)) return b;
        return all().get(0);
    }

    @Override
    public String toString() {
        return name;
    }
}
