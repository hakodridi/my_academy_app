package com.abdelhak.dridi.myacademy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final int IMAGE_PICKER_REQUEST = 598;
    private FirebaseAuth mAuth;
    private TextView nameTV, emailTV;
    RoundedImageView profileImage;
    private DatabaseReference mDatabase;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    User userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        nameTV = findViewById(R.id.name_tv);
        emailTV = findViewById(R.id.email_tv);
        profileImage = findViewById(R.id.profile_image);


        userData = null;


        findViewById(R.id.edit_name_btn).setOnClickListener(v -> {
            showEditDialog();
        });

        findViewById(R.id.edit_password_btn).setOnClickListener(v -> {
            showEditPasswordDiaog("", false);
        });

        findViewById(R.id.logout_btn).setOnClickListener(v -> {
            showConfirmationDialog();
        });

        findViewById(R.id.edit_image_btn).setOnClickListener(v -> {
            startImagePicker();
        });








        findViewById(R.id.home_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        });

        findViewById(R.id.courses_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, CoursesActivity.class));
        });

        findViewById(R.id.academies_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, AcademiesActivity.class));
        });


        getData();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                hideProgress();
                finish();
            }
        }, 1000);
    }

    private void showEditPasswordDiaog(String oldPassword, boolean isError){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_edit_password);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText oldPasswordET = dialog.findViewById(R.id.old_password_input);
        EditText password1ET = dialog.findViewById(R.id.password_input_1);
        EditText password2ET = dialog.findViewById(R.id.password_input_2);

        if(isError) {
            oldPasswordET.setText(oldPassword);
            oldPasswordET.setError("Incorrect password");
            oldPasswordET.requestFocus();
        }

        dialog.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            String oldPass = oldPasswordET.getText().toString();
            String pass1 = password1ET.getText().toString();
            String pass2 = password2ET.getText().toString();

            if(oldPass.isEmpty()){
                oldPasswordET.setError("Enter you current password");
                oldPasswordET.requestFocus();
                return;
            }

            if(pass1.isEmpty()){
                password1ET.setError("Enter new password");
                password1ET.requestFocus();
                return;
            }

            if(pass2.isEmpty()){
                password2ET.setError("Repeat the new password");
                password2ET.requestFocus();
                return;
            }

            if(!pass1.equals(pass2)){
                password2ET.setError("Passwords do not match");
                password2ET.requestFocus();
                return;
            }

            dialog.dismiss();
            showProgress();
            updatePassword(oldPass, pass1);
        });
    }

    private void updatePassword(String oldPass, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(userEmail, oldPass);
        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User successfully reauthenticated
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Password updated successfully
                                        hideProgress();
                                        Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle password update failure
                                        hideProgress();
                                        Toast.makeText(ProfileActivity.this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgress();
                        showEditPasswordDiaog(oldPass, true);
                    }
                });
    }

    private void getData() {
        showProgress();
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
//                    String name = snapshot.child("name").getValue(String.class);
//                    String email = snapshot.child("email").getValue(String.class);
                    userData = snapshot.getValue(User.class);
                    assert userData != null;
                    emailTV.setText(userData.getEmail());
                    nameTV.setText(userData.getName());

                    if (!userData.getImagePath().isEmpty()){
                        try {
                            Picasso.get().load(userData.getImagePath()).into(profileImage);
                        }catch (Exception e){
                            Toast.makeText(ProfileActivity.this, "There is a problem with image download", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
                Toast.makeText(ProfileActivity.this, "Error , try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_edit);
        AlertDialog dialog = builder.create();
        dialog.show();

        View cancelBtn, confirmBtn;
        EditText nameET = dialog.findViewById(R.id.name_input);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);
        confirmBtn = dialog.findViewById(R.id.confirm_btn);

        if(cancelBtn != null)cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if(confirmBtn != null)confirmBtn.setOnClickListener(v -> {
            String name = nameET.getText().toString().trim();
            if(name.isEmpty()){
                nameET.setError("Enter your name please");
                nameET.requestFocus();
                return;
            }

            dialog.dismiss();
            showProgress();
            updateName(name);
        });


    }

    private void updateName(String name) {
        mDatabase.child("users").child(mAuth.getUid())
                .child("name").setValue(name).addOnCompleteListener(runnable -> {
                    if(runnable.isSuccessful()){
                        Toast.makeText(this, "Name changed", Toast.LENGTH_SHORT).show();
                        nameTV.setText(name);
                    }else{
                        Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show();
                    }
                    hideProgress();
                });
    }

    private void startImagePicker() {

        ImagePicker.with(ProfileActivity.this)
                .cropSquare()
                .compress(1024)
                .saveDir(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name)))
                .start(IMAGE_PICKER_REQUEST);
    }

    private void uploadImageToFirebaseStorage(Uri selectedImageUri) {
        showProgress();
        StorageReference storageRef = storageReference.child("profiles/"+ FirebaseAuth.getInstance().getUid()+".jpg");
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadImagetoUserData(uri, selectedImageUri);
                    });
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "There is a problem, check your internet an retry", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImagetoUserData(Uri imageUri, Uri localUri) {
        mDatabase.child("users").child(mAuth.getUid()).child("imagePath")
                .setValue(imageUri.toString()).addOnCompleteListener(runnable -> {
                    if(runnable.isSuccessful()){
                        setProfileImage(localUri);
                    }else{
                        Toast.makeText(this, "There is a problem, check your internet an retry", Toast.LENGTH_SHORT).show();
                    }
                    hideProgress();
                });
    }

    private void setProfileImage(Uri selectedImageUri) {
        if(selectedImageUri == null) return;
        // Set the selected image in ImageView
        profileImage.setImageURI(selectedImageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image uri
            uploadImageToFirebaseStorage(data.getData());
        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideProgress() {
        Functions.dismissProgressDialog();
    }

    private void showProgress() {
        Functions.showProgressDialog(ProfileActivity.this);
    }

}