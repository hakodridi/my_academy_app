package com.abdelhak.dridi.myacademy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.abdelhak.dridi.myacademy.R;

public class CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);



        findViewById(R.id.home_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(CoursesActivity.this, HomeActivity.class));
        });

        findViewById(R.id.academies_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(CoursesActivity.this, AcademiesActivity.class));
        });

        findViewById(R.id.profile_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(CoursesActivity.this, ProfileActivity.class));
        });
    }
}