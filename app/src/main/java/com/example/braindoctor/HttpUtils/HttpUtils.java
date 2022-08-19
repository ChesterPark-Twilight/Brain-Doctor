package com.example.braindoctor.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.braindoctor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class HttpUtils {

    private static final String LOGIN = "http://172.24.21.239:5000/login";
    private static final String REGISTER = "http://172.24.21.239:5000/register";
    private static final String QUERY = "http://172.24.21.239:5000/query";
    private static final String UPDATE = "http://172.24.21.239:5000/update_info";
    private static final String DOWNLOAD = "http://172.24.21.239:5000/download";
    private static final String ANALYSE = "http://172.24.21.239:5000/analyse";

//    private static final String LOGIN = "http://120.55.64.125:5000/login";
//    private static final String REGISTER = "http://120.55.64.125:5000/register";
//    private static final String QUERY = "http://120.55.64.125:5000/query";
//    private static final String UPDATE = "http://120.55.64.125:5000/update_info";
//    private static final String DOWNLOAD = "http://120.55.64.125:5000/download";
//    private static final String ANALYSE = "http://120.55.64.125:5000/analyse";

    public static void Login(String account, String password, String accountType, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LOGIN)
                .post(generateAccountBody(account, password, accountType, "", ""))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Register(String account, String password, String accountType, String name, String doctorName, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(REGISTER)
                .post(generateAccountBody(account, password, accountType, name, doctorName))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Query(String uid, String accountType, Callback callback) {
        Log.d("Query", uid + " " + accountType);
        OkHttpClient okHttpClient  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(QUERY)
                .post(generateInfoBody(uid, accountType, "", "", ""))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Update(String uid, String accountType, String name, String age, String sex, Callback callback) {
        OkHttpClient okHttpClient  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UPDATE)
                .post(generateInfoBody(uid, accountType, name, age, sex))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Download(final String uid, final String type, final Context context) {
        String URL = DOWNLOAD + "/" + uid + "/" + type;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().toString();
                Log.d("Response", string);
                InputStream inputStream = null;
                byte[] buffer = new byte[2048];
                int length;
                FileOutputStream fileOutputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    long total = response.body().contentLength();
                    String fileName = uid + type +".nii.gz";
                    fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE);
                    long sum = 0;
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                        sum += length;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //progress表示下载进度
                        Log.d("DownLoad", "Progress: " + progress);
                    }
                    fileOutputStream.flush();
                    Log.d("DownLoad", "Success");
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void DownloadReport(final String uid, final Context context) {
        String URL = DOWNLOAD + "/" + uid + "/pdf";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().toString();
                Log.d("Response", string);
                InputStream inputStream = null;
                byte[] buffer = new byte[2048];
                int length;
                FileOutputStream fileOutputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    long total = response.body().contentLength();
                    String fileName = uid + "Report.jpg";
                    fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE);
                    long sum = 0;
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                        sum += length;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //progress表示下载进度
                        Log.d("DownLoad", "Progress: " + progress);
                    }
                    fileOutputStream.flush();
                    Log.d("DownLoad", "Success");
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    public static void Analyse()

    private static RequestBody generateAccountBody(String account, String password, String accountType, String name, String doctorName) {
        JSONObject JSON = new JSONObject();
        try {
            JSON.put("account", account);
            JSON.put("passwd", password);
            JSON.put("account_type", accountType);
            JSON.put("name", name);
            JSON.put("doctor_name", doctorName);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        return RequestBody.create(mediaType, ""+JSON.toString());
    }

    private static RequestBody generateInfoBody(String uid, String accountType, String name, String age, String sex) {
        JSONObject JSON = new JSONObject();
        try {
            JSON.put("uid", uid);
            JSON.put("account_type", accountType);
            if (!name.equals("")) {
                JSON.put("name", name);
            }
            if (!age.equals("")) {
                JSON.put("age", age);
            }
            if (!sex.equals("")) {
                JSON.put("sex", sex);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        return RequestBody.create(mediaType, ""+JSON.toString());
    }
}
