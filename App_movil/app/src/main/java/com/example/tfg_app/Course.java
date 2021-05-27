package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

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

public class Course extends AppCompatActivity {
    public static JSONObject cursos = new JSONObject();
    public static String id_curso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Map<String, String> parametros = new LinkedHashMap<>();
        HttpConn http = new HttpConn();

        parametros.put("url", "moodle/mod/exam/android/cursos.php");
        parametros.put("activity", "Cursos");
        try {
            parametros.put("id_user", MainActivity.user.getString("id"));
            http.conn(parametros);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

        if (cursos != null) {
            LinearLayout linearLayout = findViewById(R.id.linear_layout_course);
            //Adding TextViews
            for (int i = 0; i < cursos.length(); i++) {
                try {
                    TextView textView = new TextView(this);
                    JSONObject j = cursos.getJSONObject(String.valueOf(i));
                    textView.setText(j.getString("name"));

                    // Configuramos las propiedades del TextView
                    setTextViewAttributes(textView);
                    int finalI = i;

                    textView.setOnClickListener(v -> {
                        try {
                            id_curso = cursos.getJSONObject(String.valueOf(finalI)).getString("id");
                            SelectCourse(v);
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

    //Función para la selección de curso
    public void SelectCourse(View view){
        Intent SelectCourse = new Intent(this, Exam.class);
        startActivity(SelectCourse);
    }
}