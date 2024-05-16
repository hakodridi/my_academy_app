package com.abdelhak.dridi.myacademy.activities.academy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.activities.CoursesActivity;
import com.abdelhak.dridi.myacademy.activities.HomeActivity;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.adapters.CourseAdapter;
import com.abdelhak.dridi.myacademy.tools.classes.Course;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AcademyHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<Course> courses, filteredCourses;
    EditText searchET;
    CourseAdapter courseAdapter;
    RecyclerView listView;
    User userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academy_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        searchET = findViewById(R.id.search_input);
        listView = findViewById(R.id.courses_list_view);

        courses = new ArrayList<>();
        filteredCourses = new ArrayList<>();

        courseAdapter = new CourseAdapter(this, filteredCourses, position -> {
            showDetailsDialog(position);
        });
        listView.setAdapter(courseAdapter);






        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showProgress();
                    filterCourses();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.profile_menu_btn).setOnClickListener(v->{
            startActivity(new Intent(AcademyHomeActivity.this, AcademyProfileActivity.class));
        });

        findViewById(R.id.floating_add_btn).setOnClickListener(v->{
            Intent intent = new Intent(AcademyHomeActivity.this, AcademyAddCourseActivity.class);
            long newId = courses.isEmpty()?0:courses.get(courses.size()-1).getId()+1;
            intent.putExtra("post_id", newId);
            startActivity(intent);
        });

//        getCourses();
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
                        getCourses();
                    }else {
                        Toast.makeText(AcademyHomeActivity.this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AcademyHomeActivity.this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showDetailsDialog(int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(AcademyHomeActivity.this);
        dialog.setContentView(R.layout.dialog_course_details);

        RoundedImageView imageView = dialog.findViewById(R.id.image);

        TextView titleTV = dialog.findViewById(R.id.title);
        TextView priceTV = dialog.findViewById(R.id.price);
        TextView profNameTV = dialog.findViewById(R.id.prof_name);
        TextView durationTV = dialog.findViewById(R.id.duration);
        TextView dateTV = dialog.findViewById(R.id.date);
        TextView academyNameTV = dialog.findViewById(R.id.academy_name);
        TextView descriptionTV = dialog.findViewById(R.id.description);
        TextView requirementsTV = dialog.findViewById(R.id.requirements);
        TextView linkTV = dialog.findViewById(R.id.link);
        RoundedImageView userImageView = dialog.findViewById(R.id.user_image_view);
        dialog.findViewById(R.id.like_btn).setVisibility(View.GONE);

        Course course = courses.get(position);

        linkTV.setOnClickListener(v->{

            if(!( course.getLink().startsWith("http://") || course.getLink().startsWith("https://")) || !course.getLink().contains(".")){
                Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(course.getLink()));
            startActivity(i);
        });


        titleTV.setText(course.getTitle());
        priceTV.setText(Functions.formatPrice(course.getPrice()));
        profNameTV.setText(course.getProfName());
        durationTV.setText(course.getDuration()+ " Hours");
        dateTV.setText(course.getStartDate());
        academyNameTV.setText(course.getAcademyName());
        descriptionTV.setText(course.getDescription());
        requirementsTV.setText(course.getRequirements());
        linkTV.setText(course.getLink());

        if(userData!=null && !userData.getImagePath().isEmpty()){
            try {
                Picasso.get().load(userData.getImagePath()).into(userImageView);
            }catch (Exception e){
                Log.e(Functions.TAG, "show academy image in detail: ", e);
            }
        }

        try {
            Picasso.get().load(course.getImagePath()).into(imageView);
        }catch (Exception e){
            Log.e(Functions.TAG, "show academy image in detail: ", e);
        }

        dialog.show();
    }

    private void getCourses() {
        showProgress();

        mDatabase.child("courses").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                courses.clear();
                if(snapshots.hasChildren()){
                    for (DataSnapshot snapshot : snapshots.getChildren()){
                        try {
                            courses.add(snapshot.getValue(Course.class));
                        }catch (Exception e){
                            Log.e(Functions.TAG, "on get course : ", e);
                        }
                    }
                }
                filterCourses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
                Toast.makeText(AcademyHomeActivity.this, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
                filterCourses();
            }
        });
    }

    private void filterCourses() {
        filteredCourses.clear();
        String searchText = searchET.getText().toString().toLowerCase();
        if(searchText.isEmpty()) filteredCourses.addAll(courses);
        else for(Course course : courses){
            if(checkCourseWithSearchText(course, searchText)) filteredCourses.add(course);
        }
        hideProgress();

        if (filteredCourses.isEmpty()) showEmpty();
        else hideEmpty();

        courseAdapter.notifyDataSetChanged();

    }

    private boolean checkCourseWithSearchText(Course course, String searchText) {
        return course.getTitle().toLowerCase().contains(searchText) ||
                searchText.contains(course.getTitle().toLowerCase()) ||
                course.getProfName().toLowerCase().contains(searchText) ||
                searchText.contains(course.getProfName().toLowerCase()) ||
                course.getDescription().toLowerCase().contains(searchText) ||
                searchText.contains(course.getDescription().toLowerCase()) ||
                course.getRequirements().toLowerCase().contains(searchText) ||
                searchText.contains(course.getRequirements().toLowerCase()) ;
    }


    private void showProgress(){
        Functions.showProgressDialog(AcademyHomeActivity.this);
    }

    private void hideProgress(){
        Functions.dismissProgressDialog();
    }

    private void showEmpty(){
        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
    }

    private void hideEmpty(){
        findViewById(R.id.empty_view).setVisibility(View.GONE);
    }
}