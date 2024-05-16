package com.abdelhak.dridi.myacademy.tools.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.callbacks.ItemCallback;
import com.abdelhak.dridi.myacademy.tools.classes.Course;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {
    Context context;
    ArrayList<Course> courses;
    ItemCallback callback;

    public CourseAdapter(Context context, ArrayList<Course> courses, ItemCallback callback) {
        this.context = context;
        this.courses = courses;
        this.callback = callback;
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_course, parent, false);
        return new CourseHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position) {
        Course course = courses.get(position);

        holder.titleTV.setText(course.getTitle());
        holder.priceTV.setText(Functions.formatPrice(course.getPrice()));
        holder.profNameTV.setText(course.getProfName());
        holder.academyTV.setText(course.getAcademyName());

        if(!course.getImagePath().isEmpty()){
            try {
                Picasso.get().load(course.getImagePath()).into(holder.imageView);
            }catch (Exception e){
                Toast.makeText(context, "There is a problem with image download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView priceTV, titleTV, profNameTV, academyTV;
        public CourseHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            titleTV = itemView.findViewById(R.id.title);
            priceTV = itemView.findViewById(R.id.price);
            profNameTV = itemView.findViewById(R.id.prof_name);
            academyTV = itemView.findViewById(R.id.academy_name);

            itemView.setOnClickListener(v->{
                callback.onClick(getAdapterPosition());
            });
        }
    }
}
