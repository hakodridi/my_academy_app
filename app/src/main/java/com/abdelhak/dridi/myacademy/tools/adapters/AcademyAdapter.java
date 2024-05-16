package com.abdelhak.dridi.myacademy.tools.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelhak.dridi.myacademy.R;
import com.abdelhak.dridi.myacademy.tools.Functions;
import com.abdelhak.dridi.myacademy.tools.callbacks.ItemCallback;
import com.abdelhak.dridi.myacademy.tools.classes.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AcademyAdapter extends RecyclerView.Adapter<AcademyAdapter.AcademyHolder> {
    Context context;
    ArrayList<User> academies;
    ItemCallback callback;

    public AcademyAdapter(Context context, ArrayList<User> academies, ItemCallback callback) {
        this.context = context;
        this.academies = academies;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AcademyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_academy_small, parent, false);
        return new AcademyHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AcademyHolder holder, int position) {
        holder.nameTV.setText(academies.get(position).getName());
        if(!academies.get(position).getImagePath().isEmpty())try{
            Picasso.get()
                    .load(academies.get(position).getImagePath())
                    .into(holder.imageView);
        }catch (Exception e){
            Log.e(Functions.TAG, "onBindViewHolder: ", e);
        }
    }

    @Override
    public int getItemCount() {
        return academies.size();
    }


    public class AcademyHolder extends RecyclerView.ViewHolder{
        RoundedImageView imageView;
        TextView nameTV;
        public AcademyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            nameTV = itemView.findViewById(R.id.name);
        }
    }
}
