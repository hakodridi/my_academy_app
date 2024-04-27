package com.abdelhak.dridi.myacademy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.abdelhak.dridi.myacademy.R;

public class AcademiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academies);


        findViewById(R.id.home_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(AcademiesActivity.this, HomeActivity.class));
        });

        findViewById(R.id.courses_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(AcademiesActivity.this, CoursesActivity.class));
        });

        findViewById(R.id.profile_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(AcademiesActivity.this, ProfileActivity.class));
        });
    }
}