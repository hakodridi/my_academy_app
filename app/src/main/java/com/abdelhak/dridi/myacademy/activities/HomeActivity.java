package com.abdelhak.dridi.myacademy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.abdelhak.dridi.myacademy.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        findViewById(R.id.courses_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(HomeActivity.this, CoursesActivity.class));
        });

        findViewById(R.id.academies_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(HomeActivity.this, AcademiesActivity.class));
        });

        findViewById(R.id.profile_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });
    }
}