package com.abdelhak.dridi.myacademy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class CompleteDataActivity extends AppCompatActivity {
    EditText phoneET, communeET, addressET;
    AutoCompleteTextView wilayaSpinner;
    ArrayAdapter<String> adapter;
    User userData;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coplete_data);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        wilayaSpinner = findViewById(R.id.wilaya_input);
        adapter = new ArrayAdapter<>(this, R.layout.item_dropdown_menu_popup, Functions.wilayas);
        wilayaSpinner.setAdapter(adapter);

        wilayaSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wilayaSpinner.setError(null);
            }
        });

        phoneET = findViewById(R.id.phone_edit_text);
        communeET = findViewById(R.id.commune_edit_text);
        addressET = findViewById(R.id.address_edit_text);


        findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            checkInputs();
        });

        getData();
    }

    private void getData() {
        Functions.showProgressDialog(CompleteDataActivity.this);
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    userData = snapshot.getValue(User.class);
                    if(userData !=  null){
                        Functions.dismissProgressDialog();

                    }
                    else Toast.makeText(CompleteDataActivity.this, "There is problem ,Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompleteDataActivity.this, "There is problem ,Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkInputs() {
        String phone = phoneET.getText().toString().trim();
        String commune = communeET.getText().toString().trim();
        String address = addressET.getText().toString().trim();
        String wilaya = wilayaSpinner.getText().toString();


        if(phone.isEmpty()){
            phoneET.setError("Insert your Phone number please");
            phoneET.requestFocus();
            return;
        }

        if(wilaya.isEmpty()){
            wilayaSpinner.setError("Insert the Wilaya please");
            wilayaSpinner.requestFocus();
            return;
        }

        if(commune.isEmpty()){
            communeET.setError("Insert the Commune please");
            communeET.requestFocus();
            return;
        }

        Functions.showProgressDialog(CompleteDataActivity.this);
        userData.setPhone(phone);
        userData.setWilaya(wilaya);
        userData.setCommune(commune);
        userData.setComplete(true);
        userData.setAddress(address);
        uploadData();
    }

    private void uploadData() {
        mDatabase.child("users").child(mAuth.getUid()).setValue(userData).addOnCompleteListener(task1 -> {
            Log.w(Functions.TAG, "registerUserData: start");
            if (task1.isSuccessful()) {
                Log.d(Functions.TAG, "registerUserData: done");
                Functions.dismissProgressDialog();
                startActivity(new Intent(CompleteDataActivity.this, AcademyHomeActivity.class));
                finish();
            } else {
                Log.e(Functions.TAG, "registerUserData: Failed");
                Functions.dismissProgressDialog();
                Toast.makeText(CompleteDataActivity.this, "Registration failed. Error saving user data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}