package com.abdelhak.dridi.myacademy.activities.academy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.activities.CompleteDataActivity;
import com.abdelhak.dridi.myacademy.activities.HomeActivity;
import com.abdelhak.dridi.myacademy.activities.SplashActivity;
import com.abdelhak.dridi.myacademy.tools.DateTextWatcher;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.classes.Course;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AcademyAddCourseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final int IMAGE_PICKER_REQUEST = 598;
    private Uri selectedImageUri;
    RoundedImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private long postId;
    User userData;

    EditText titleET, priceET, profET, linkET, descET, requirementsET, durationET, startDateET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy_add_course);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        imageView = findViewById(R.id.place_image);
        titleET = findViewById(R.id.title_edit_text);
        priceET = findViewById(R.id.price_edit_text);
        profET = findViewById(R.id.prof_name_edit_text);
        linkET = findViewById(R.id.link_edit_text);
        descET = findViewById(R.id.desc_edit_text);
        requirementsET = findViewById(R.id.requirements_edit_text);
        durationET = findViewById(R.id.duration_edit_text);
        startDateET = findViewById(R.id.date_edit_text);

        startDateET.addTextChangedListener(new DateTextWatcher(startDateET));

        postId = getIntent().getLongExtra("post_id", 0);

        findViewById(R.id.add_image_btn).setOnClickListener(v->{
            startImagePicker();
        });

        findViewById(R.id.cancel_image_btn).setOnClickListener(v->{
            removePlaceImage();
        });

        findViewById(R.id.confirm_btn).setOnClickListener(v->{
            checkData();
        });
        getData();

    }

    private void getData() {
        showProgress();
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    userData = snapshot.getValue(User.class);
                    if(userData != null){
                        hideProgress();
                    }else {
                        Toast.makeText(AcademyAddCourseActivity.this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AcademyAddCourseActivity.this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkData() {
        if( selectedImageUri == null ){
            Toast.makeText(this, "Insert a image for course please", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleET.getText().toString().trim();
        String priceS = priceET.getText().toString().trim();
        String profName = profET.getText().toString().trim();
        String link = linkET.getText().toString().trim();
        String durationS = durationET.getText().toString().trim();
        String date = startDateET.getText().toString().trim();
        String desc = descET.getText().toString().trim();
        String requirements = requirementsET.getText().toString().trim();

        if(title.isEmpty()){
            titleET.setError("Enter the title please");
            titleET.requestFocus();
            return;
        }

        if (priceS.isEmpty()) {
            priceET.setError("Enter the price please");
            priceET.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceS);
        } catch (NumberFormatException e) {
            priceET.setError("Enter a valid price");
            priceET.requestFocus();
            return;
        }

        if (profName.isEmpty()) {
            profET.setError("Enter the professor's name please");
            profET.requestFocus();
            return;
        }

        if (link.isEmpty()) {
            linkET.setError("Enter the link please");
            linkET.requestFocus();
            return;
        }

        if (durationS.isEmpty()) {
            durationET.setError("Enter the duration please");
            durationET.requestFocus();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationS);
        } catch (NumberFormatException e) {
            durationET.setError("Enter a valid duration");
            durationET.requestFocus();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        Date startDate;
        try {
            startDate = dateFormat.parse(date);
            Date currentDate = new Date();
            if (startDate.before(currentDate)) {
                startDateET.setError("Enter a date greater than or equal to today");
                startDateET.requestFocus();
                return;
            }
        } catch (ParseException e) {
            startDateET.setError("Enter a valid date in the format DD-MM-YYYY");
            startDateET.requestFocus();
            return;
        }

        showProgress();
        Course newCourse = new Course(
                postId,
                mAuth.getUid(),
                userData.getName(),
                title,
                price,
                profName,
                link,
                duration,
                date
        );
        newCourse.setDescription(desc);
        newCourse.setRequirements(requirements);

        uploadImageToFirebaseStorage(newCourse);

    }

    private void uploadImageToFirebaseStorage(Course course) {
        StorageReference storageRef = storageReference
                .child("courses/"+ mAuth.getUid() + "/course-" + postId +".jpg");
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        course.setImagePath(uri.toString());
                        uploadCourse(course);
                    });
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadCourse(Course course) {
        mDatabase.child("courses").child(mAuth.getUid()).child(String
                .valueOf(postId)).setValue(course).addOnCompleteListener(runnable -> {
            if(runnable.isSuccessful()){
                Toast.makeText(this, "Course uploaded successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AcademyAddCourseActivity.this, AcademyHomeActivity.class));
                finish();
            }else{
                Toast.makeText(this, "Failed to upload course. Please try again.", Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        });
    }

    private void startImagePicker() {

        ImagePicker.with(AcademyAddCourseActivity.this)
                .crop(3f, 2f)
                .compress(1024)
                .saveDir(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name)))
                .start(IMAGE_PICKER_REQUEST);
        findViewById(R.id.image_progress).setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image uri
            selectedImageUri = data.getData();

            setPlaceImage();
        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.image_progress).setVisibility(View.GONE);
    }

    private void setPlaceImage() {
        if(selectedImageUri == null) return;
        // Set the selected image in ImageView
        imageView.setImageURI(selectedImageUri);
        imageView.setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_image_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.image_progress).setVisibility(View.GONE);
    }

    private void removePlaceImage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Are you sure you want to remove the image?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageView.setVisibility(View.GONE);
                findViewById(R.id.cancel_image_btn).setVisibility(View.GONE);
                selectedImageUri = null;
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showProgress(){
        Functions.showProgressDialog(AcademyAddCourseActivity.this);
    }

    private void hideProgress(){
        Functions.dismissProgressDialog();
    }

}