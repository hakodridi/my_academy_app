package com.abdelhak.dridi.myacademy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.activities.academy.AcademyHomeActivity;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText emailET, passwordET;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.email_edit_text);
        passwordET = findViewById(R.id.password_edit_text);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(mAuth.getCurrentUser() != null){
            gotoHomePage();
        }

        findViewById(R.id.goto_register_btn).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        findViewById(R.id.login_btn).setOnClickListener(v -> {
            login();
        });
    }

    private void gotoHomePage() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void login() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if(email.isEmpty()){
            emailET.setError("Insert your email please");
            emailET.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordET.setError("Insert your password please");
            passwordET.requestFocus();
            return;
        }

        Functions.showProgressDialog(LoginActivity.this);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
//                        Functions.dismissProgressDialog();
//                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        getData();
                        finish();
                    } else {
                        Functions.dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "Login failed. " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void getData() {
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    User userData = snapshot.getValue(User.class);

                    if(userData != null){
                        if (userData.isAcademy()){
                            if (userData.isComplete()) startActivity( new Intent(LoginActivity.this, AcademyHomeActivity.class));
                            else startActivity(new Intent(LoginActivity.this, CompleteDataActivity.class));
                        }
                        else startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        finish();
                    }

                    Functions.dismissProgressDialog();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}