package com.example.tfg_app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;

public class ReconocimientoHuella extends AppCompatActivity {
    static final int REQUEST_RECOGNITION = 1000;

    public static JSONObject data;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt.CryptoObject cryptoObject;
    private BiometricManager biometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento_huella);

        enrolFingerprints();
        // secretKeyName = getString(R.string.secret_key_name);
        biometricPrompt = createBiometricPromt();
        promptInfo = createPromptInfo();

        Button biometricLoginButton = findViewById(R.id.btn_auth);
        biometricLoginButton.setOnClickListener(view -> {
            // Cipher cipher = cryptoObject.getCipher();
            // biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void generateSecretKey() {

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

    // Función que crea un BiometricPrompt
    private BiometricPrompt createBiometricPromt() {
        executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(ReconocimientoHuella.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                        Intent SelectExam = new Intent(ReconocimientoHuella.this, Exam.class);
                        startActivity(SelectExam);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                        Intent Foto = new Intent(ReconocimientoHuella.this, Foto.class);
                        startActivity(Foto);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return prompt;
    }

    // Función que crea la información del prompt
    private BiometricPrompt.PromptInfo createPromptInfo() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .setAllowedAuthenticators(BIOMETRIC_STRONG | BIOMETRIC_WEAK)
                .build();

        return promptInfo;
    }

    //Función para el reconocimiento de huella
    public void Reconigtion(View view) {
        // Intent SelectExam = new Intent(this, Foto.class);
        // startActivity(SelectExam);
    }

}