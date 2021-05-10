package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Exam extends AppCompatActivity {

    public static JSONObject examenes = new JSONObject();
    public static String id_exam;

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

        if (examenes !=null){
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

                        } catch (JSONException e) {
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
    public void SelectExam(View view) {
        Intent SelectExam = new Intent(this, ReconocimientoHuella.class);
        // Intent SelectExam = new Intent(this, Foto.class);
        startActivity(SelectExam);
    }

}