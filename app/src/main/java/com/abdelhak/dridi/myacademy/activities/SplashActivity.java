package com.abdelhak.dridi.myacademy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.activities.academy.AcademyHomeActivity;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            getData();
        }else startIntent(new Intent(SplashActivity.this, LoginActivity.class));



    }

    private void getData() {
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    User userData = snapshot.getValue(User.class);
                    assert userData != null;
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    if (userData.isAcademy()){
                        if (userData.isComplete()) intent = new Intent(SplashActivity.this, AcademyHomeActivity.class);
                        else intent = new Intent(SplashActivity.this, CompleteDataActivity.class);
                    }

                    startIntent(intent);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void startIntent(Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}