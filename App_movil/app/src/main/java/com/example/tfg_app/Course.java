package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Course extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
    }

    //Función para la selección de curso
    public void SelectCourse(View view){
        Intent SelectCourse = new Intent(this, Exam.class);
        startActivity(SelectCourse);
    }

}