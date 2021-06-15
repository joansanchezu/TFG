
package com.example.tfg_app;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConn extends AppCompatActivity {
    String data = "";
    String activity = "";
    String connstr = "http://192.168.1.40//";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    JSONObject json;

    public void conn(Map<String, String> parametros) throws InterruptedException {
        Thread t = new Thread(() -> {
            //doInBackground
            doConnection(parametros);

            switch (activity){
                case "Login":
                    try {
                        MainActivity.valido = json.getString("valido");
                        MainActivity.user = json.getJSONObject("user");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Huella":
                    try {
                        if (json.getString("huella") != "null") {
                            MainActivity.huella = json.getString("huella");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Registro":
                    try {
                        RegistroHuella.valido = json.getString("challenge");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Cursos":
                    try {
                        for(int i=0; i<json.length(); i++){
                            Course.cursos.put(String.valueOf(i), json.getJSONObject(String.valueOf(i)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Examenes":
                    try {
                        for(int i=0; i<json.length(); i++){
                            Exam.examenes.put(String.valueOf(i), json.getJSONObject(String.valueOf(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Reconocimiento":
                    try {
                        Exam.valido = json.getString("challenge");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            json = new JSONObject();
        });

        t.start();
        try{
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doConnection(Map<String, String> parametros) {
        try {
            String urlstr = "";
            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String, String> parametro : parametros.entrySet()) {
                if (parametro.getKey() == "url") {
                    urlstr = connstr + parametro.getValue();
                } else {
                    if (parametro.getKey() == "activity") {
                        activity = parametro.getValue();
                    }
                    else {
                        if (postData.length() != 0) {
                            postData.append('&');
                        }
                        postData.append(URLEncoder.encode(parametro.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(parametro.getValue()), "UTF-8"));
                    }
                }
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL url = new URL(urlstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            http.setDoOutput(true);
            http.setDoInput(true);
            http.getOutputStream().write(postDataBytes);

            InputStream inputStream = http.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                if (line != null){
                    data = data + line;
                }
            }

            json = new JSONObject(data);
            data = "";

            inputStream.close();
            bufferedReader.close();
            http.disconnect();
        } catch (MalformedURLException e) {
            String result = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}