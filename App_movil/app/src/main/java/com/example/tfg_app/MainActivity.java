package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_RECOGNITION = 1000;
    public static EditText username;
    EditText password;
    public static JSONObject user;
    public static String valido = "false";
    public static String huella = "false";
    private BiometricManager biometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enrolFingerprints();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }

    // Función que registra el BiometricManager
    private void enrolFingerprints() {
        biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | BIOMETRIC_WEAK);
                startActivityForResult(enrollIntent, REQUEST_RECOGNITION);
                break;
        }
    }

    //Función para el botón login
    public void login(View view) throws JSONException, InterruptedException {
        Map<String, String> parametros = new LinkedHashMap<>();

        parametros.put("url", "moodle/mod/exam/android/manager.php");
        parametros.put("activity", "Login");
        parametros.put("username", username.getText().toString());
        parametros.put("password", password.getText().toString());

        HttpConn http = new HttpConn();
        http.conn(parametros);
        parametros = new LinkedHashMap<>();

        if (valido.equals("true")) {
            parametros.put("url", "moodle/mod/exam/android/huella.php");
            parametros.put("activity", "Huella");
            parametros.put("userid", user.getString("id"));
            http.conn(parametros);

            if (huella.equals("true")) {
                Intent login = new Intent(this, Course.class);
                startActivity(login);
            } else {
                Intent singup = new Intent(this, RegistroHuella.class);
                startActivity(singup);
            }
        } else {
            System.out.println("Error");
        }
    }
}