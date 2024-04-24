package com.abdelhak.dridi.myacademy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText emailET, password1ET, password2ET, nameET;
    RadioButton studentRadio, academyRadio;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(mAuth.getCurrentUser() != null){
            gotoHomePage();
        }


        emailET = findViewById(R.id.email_edit_text);
        nameET = findViewById(R.id.name_edit_text);
        password1ET = findViewById(R.id.password1_edit_text);
        password2ET = findViewById(R.id.password2_edit_text);
        studentRadio = findViewById(R.id.student_radio_btn);
        academyRadio = findViewById(R.id.academy_radio_btn);


        findViewById(R.id.goto_login_btn).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        findViewById(R.id.signup_btn).setOnClickListener(v -> {
            signUp();
        });

    }

    private void gotoHomePage() {
        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        finish();
    }


    private void signUp() {
        String name = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password1 = password1ET.getText().toString();
        String password2 = password2ET.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameET.setError("Enter you name please");
            nameET.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Functions.isValidEmail(email)) {
            emailET.setError("Enter a valid email please");
            emailET.requestFocus();
            return;
        }

        if (password1.length() < 6) {
            password1ET.setError("Enter a valid password please");
            password1ET.requestFocus();
            return;
        }

        if (!password2.equals(password1)) {
            password2ET.setError("Passwords do not match");
            password2ET.requestFocus();
            return;
        }

        Functions.showProgressDialog(RegisterActivity.this);

        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        User userToSave = new User(userId, name, email, academyRadio.isChecked());
                        mDatabase.child("users").child(userId).setValue(userToSave).addOnCompleteListener(task1 -> {
                            Log.w(Functions.TAG, "registerUserData: start");
                            if (task1.isSuccessful()) {
                                Log.d(Functions.TAG, "registerUserData: done");
                                Functions.dismissProgressDialog();
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Log.e(Functions.TAG, "registerUserData: Failed");
                                Functions.dismissProgressDialog();
                                Toast.makeText(RegisterActivity.this, "Registration failed. Error saving user data.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Functions.dismissProgressDialog();
                        Toast.makeText(RegisterActivity.this, "Registration failed. " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });



    }
}