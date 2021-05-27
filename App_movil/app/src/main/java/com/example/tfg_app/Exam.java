package com.example.tfg_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Exam extends AppCompatActivity {
    private static final int REGISTRO_HUELLA = 2;
    public static JSONObject examenes = new JSONObject();
    public static String id_exam;
    public static String valido;
    private Signature sign;
    private byte[] signature;
    private String signEncoded;
    private PrivateKey priv;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        Map<String, String> parametros = new LinkedHashMap<>();

        parametros.put("url", "moodle/mod/exam/android/examenes.php");
        parametros.put("activity", "Examenes");
        parametros.put("id_curso", Course.id_curso);

        HttpConn http = new HttpConn();
        try {
            http.conn(parametros);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (examenes != null){
            LinearLayout linearLayout = findViewById(R.id.linear_layout_exams);
            //Adding TextViews
            for (int i = 0; i < examenes.length(); i++) {
                try {
                    // Creamos y asignamos TextView del curso.
                    TextView textView = new TextView(this);
                    JSONObject j = examenes.getJSONObject(String.valueOf(i));
                    textView.setText(j.getString("name") + "   Acaba el " + j.getString("cerrado"));

                    // Configuramos las propiedades del TextView
                    setTextViewAttributes(textView);

                    int finalI = i;
                    textView.setOnClickListener(v -> {
                        try {
                            id_exam = examenes.getJSONObject(String.valueOf(finalI)).getString("id");
                            SelectExam(v);

                        } catch (JSONException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException | InvalidKeyException | SignatureException e) {
                            e.printStackTrace();
                        }
                    });
                    // Añadimos el TextView a la pantalla.
                    linearLayout.addView(textView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        examenes = new JSONObject();
    }

    // Método para configurar las características de los TextViews
    private void setTextViewAttributes(TextView textView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(16),
                convertDpToPixel(16),
                0, 0
        );

        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(params);
    }

    // Método para convertir los píxeles de densidad a píxeles
    private int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    //Función para la selección de examen
    private void SelectExam(View view) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        priv = (PrivateKey) keyStore.getKey(MainActivity.username.getText().toString(), null);

        sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(priv);
        sign.update("test".getBytes());


        Intent SelectExam = new Intent(this, ReconocimientoHuella.class);
        // Intent SelectExam = new Intent(this, Foto.class);
        startActivityForResult(SelectExam, REGISTRO_HUELLA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REGISTRO_HUELLA ) {
            if (resultCode == RESULT_OK) {
                try {
                    signature = sign.sign();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
                signEncoded = new String(Base64.encode(signature, Base64.DEFAULT));

                Map<String, String> parametros = new LinkedHashMap<>();
                HttpConn http = new HttpConn();

                parametros.put("url", "moodle/mod/exam/android/autenticacio.php");
                parametros.put("activity", "Reconocimiento");
                parametros.put("signature", signEncoded);
                parametros.put("data", "test");
                try {
                    parametros.put("userid", MainActivity.user.getString("id"));
                    http.conn(parametros);
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
                if (valido.equals("true")) {
                    valido = "false";
                    Intent foto = new Intent(Exam.this, Foto.class);
                    startActivity(foto);
                } else {
                    Intent course = new Intent(Exam.this, Course.class);
                    startActivity(course);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                System.out.println("ERROR!!");
            }
        }
    }
}