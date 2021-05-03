package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText username, password;
    public static String user;
    public static String valido = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }

    //Función para el botón login
    public void login(View view) throws JSONException, InterruptedException {
        Map<String, String> parametros = new LinkedHashMap<>();

        parametros.put("url", "moodle/mod/exam/android/android.php");
        parametros.put("activity", "Main");
        parametros.put("username", username.getText().toString());
        parametros.put("password", password.getText().toString());

        HttpConn http = new HttpConn();
        http.conn(parametros);

        if (valido.equals("true")) {
            Intent login = new Intent(this, Course.class);
            startActivity(login);
        }else{
            //Usuario y/o contraseña incorrectos
            System.out.print("Usuario y/o contraseña incorrectos");
        }
        System.out.println("completed");
        /*
        Query coger el usuario
        Query para comprobar si el usuario tiene huella
        if (huella){
            Intent login = new Intent(this, Course.class);
        }
        else{
            Intent login = new Intent(this, RegistroHuella.class);
        }

        Intent login = new Intent(this, Course.class);
        startActivity(login);
         */
    }
}