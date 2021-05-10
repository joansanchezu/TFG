package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class HttpConn extends AppCompatActivity {
    String data = "";
    String activity = "";
    String connstr = "http://192.168.1.40//";
    JSONObject json;

    public void conn(Map<String, String> parametros) throws InterruptedException {
        Thread t = new Thread(() -> {
            //doInBackground
            doConnection(parametros);

            //postExecute

            switch (activity){
                case "Main":
                    try {
                        MainActivity.valido = json.getString("valido");
                        MainActivity.user = json.getString("id_user");

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
                case "Examenes":
                    try {
                        for(int i=0; i<json.length(); i++){
                            Exam.examenes.put(String.valueOf(i), json.getJSONObject(String.valueOf(i)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
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
            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String, String> parametro : parametros.entrySet()) {
                if (parametro.getKey().equals("url")) {
                    connstr = connstr + parametro.getValue();
                } else {
                    if (parametro.getKey().equals("activity")) {
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
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            http.setDoOutput(true);
            http.getOutputStream().write(postDataBytes);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                if (line != null){
                    data = data + line;
                }
            }

            json = new JSONObject(data);
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

/*
public class HttpConn extends AsyncTask<Map<String, String>, Void, Void> {
    String data = "";
    String activity = "";
    String connstr = "http://192.168.1.74//moodle/mod/exam/manager.php";
    JSONObject json;
    private Context context;

    @Override
    protected Void doInBackground(Map<String, String>... params) {
        try {
            URL url = new URL(connstr);
            StringBuilder postData = new StringBuilder();
            Map<String, String> parametros = params[0];

            for (Map.Entry<String, String> parametro : parametros.entrySet()) {
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
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            http.setDoOutput(true);
            http.getOutputStream().write(postDataBytes);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

            String line = "";
            while (line != null){
                line = bufferedReader.readLine();
                if (line != null){
                    data = data + line;
                }
            }

            json = new JSONObject(data);
            bufferedReader.close();
            http.disconnect();

        } catch (MalformedURLException e) {
            String result = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
             e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        switch (activity) {
            case "Main":
                Intent intent = new Intent();
                break;
        }
    }
}
*/