package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Exam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
    }

    //Función para la selección de examen
    public void SelectExam(View view) {
        Intent SelectExam = new Intent(this, Foto.class);
        startActivity(SelectExam);
    }

}