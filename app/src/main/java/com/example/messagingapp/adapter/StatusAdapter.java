package com.example.messagingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.Models.Users;
import com.example.messagingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder>{

    ArrayList<Users> list;
    Context context;

    public StatusAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_status,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  @NotNull ViewHolder holder, int position) {
        Users users =list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.ic_profile).into(holder.image);
        holder.userName.setText(users.getUserName());
        FirebaseDatabase.getInstance().getReference().child("Users").child(users.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.child("status").getValue(String.class);
                    holder.status.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image ;
        TextView userName, status;

        public ViewHolder(@NonNull  @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profileImage1);
            userName = itemView.findViewById(R.id.username3);
            status = itemView.findViewById(R.id.status);

        }
    }
}
