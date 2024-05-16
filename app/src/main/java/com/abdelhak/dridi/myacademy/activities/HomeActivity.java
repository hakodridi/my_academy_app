package com.abdelhak.dridi.myacademy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.adapters.AcademyAdapter;
import com.abdelhak.dridi.myacademy.tools.adapters.CourseAdapter;
import com.abdelhak.dridi.myacademy.tools.classes.Course;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {
    User userData;
    ArrayList<User> academies;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<Course> courses, filteredCourses;
    EditText searchET;
    CourseAdapter courseAdapter;
    RecyclerView coursesListView, academiesListView;
    AcademyAdapter academyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        searchET = findViewById(R.id.search_input);
        coursesListView = findViewById(R.id.courses_list_view);
        academiesListView = findViewById(R.id.academies_list_view);

        courses = new ArrayList<>();
        filteredCourses = new ArrayList<>();

        courseAdapter = new CourseAdapter(this, filteredCourses, position -> {
//            showDetailsDialog(position);
        });
        coursesListView.setAdapter(courseAdapter);

        academies = new ArrayList<>();
        academyAdapter = new AcademyAdapter(this, academies, position -> {

        });
        academiesListView.setAdapter(academyAdapter);


        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    showProgress();
//                    filterCourses();
                    return true;
                }
                return false;
            }
        });




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

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    private void getUserData() {
        Functions.showProgressDialog(HomeActivity.this);
        mDatabase.child("users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    userData = snapshot.getValue(User.class);
                    getAcademies();
                }catch (Exception e){
                    Log.e(Functions.TAG, "onDataChange: ", e);
                    Functions.showToastError(HomeActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Functions.showToastError(HomeActivity.this);
            }
        });
    }

    private void getAcademies() {
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                academies.clear();
                if(snapshots.hasChildren())for (DataSnapshot snapshot : snapshots.getChildren()){
                    try {
                        User u = snapshot.getValue(User.class);
                        if(u.isAcademy() && u.isComplete())academies.add(u);
                    }catch (Exception e){
                        Log.e(Functions.TAG, "onDataChange: ", e);
                    }
                }

                if(academies.isEmpty()) findViewById(R.id.empty_academies).setVisibility(View.VISIBLE);
                else findViewById(R.id.empty_academies).setVisibility(View.GONE);

                academyAdapter.notifyDataSetChanged();
                
                getCourses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Functions.showToastError(HomeActivity.this);
            }
        });
    }

    private void getCourses() {
        Functions.showProgressDialog(HomeActivity.this);
        mDatabase.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                courses.clear();
                if(snapshots.hasChildren()){
                    for (DataSnapshot snapshot : snapshots.getChildren()){
                        for(DataSnapshot courseSnapshot : snapshot.getChildren()){
                            try {
                                courses.add(courseSnapshot.getValue(Course.class));
                            }catch (Exception e){
                                Log.e(Functions.TAG, "on get course : ", e);
                            }
                        }
                    }
                }
                Collections.shuffle(courses);
                filterCourses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Functions.showToastError(HomeActivity.this);
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
        Functions.dismissProgressDialog();

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

    private void showEmpty(){
        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
    }

    private void hideEmpty(){
        findViewById(R.id.empty_view).setVisibility(View.GONE);
    }
}