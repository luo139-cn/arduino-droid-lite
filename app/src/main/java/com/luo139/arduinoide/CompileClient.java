package com.luo139.arduinoide;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 联网编译客户端：把源码 + FQBN 发给编译服务器，拿回固件(hex/bin)。
 * 服务器端可基于 arduino-cli 实现，接口约定：
 *   POST {server}/compile  body: {"fqbn":"...", "code":"..."}
 *   返回: {"ok":true, "firmware":"base64或者hex文本", "type":"hex|bin", "log":"..."}
 */
public class CompileClient {

    public interface CallbackFn {
        void onResult(String firmware, String type, String log);
        void onError(String message);
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void compile(String serverUrl, String fqbn, String code, CallbackFn cb) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        try {
            JSONObject body = new JSONObject();
            body.put("fqbn", fqbn);
            body.put("code", code);
            RequestBody rb = RequestBody.create(body.toString(), JSON);
            Request req = new Request.Builder()
                    .url(serverUrl + "/compile")
                    .post(rb)
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cb.onError("编译请求失败：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String resp = response.body().string();
                        JSONObject obj = new JSONObject(resp);
                        if (obj.optBoolean("ok", false)) {
                            String firmware = obj.optString("firmware", "");
                            String type = obj.optString("type", "hex");
                            String log = obj.optString("log", "");
                            cb.onResult(firmware, type, log);
                        } else {
                            cb.onError("编译错误：" + obj.optString("log", resp));
                        }
                    } catch (Exception e) {
                        cb.onError("解析响应失败：" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            cb.onError("请求构造失败：" + e.getMessage());
        }
    }
}
