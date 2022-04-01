package com.example.messagingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.ChatDatail;
import com.example.messagingapp.Models.Users;
import com.example.messagingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    ArrayList<Users> list;
    Context context;

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Users users = list.get(position);
//        MessageModel model = list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.ic_profile).into(holder.imageView);
        holder.userName.setText(users.getUserName());
//       Last message and time
        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid() + users.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                    long lastTime = snapshot.child("lastMsgTime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    holder.lastMessage.setText(lastMsg);
                    holder.lastTime.setText(dateFormat.format(new Date(lastTime)));
                }
                else{
                    holder.lastMessage.setText("Tap to chat");
                    holder.lastTime.setText("");

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        send user data to chat detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatDatail.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("ProfilePic",users.getProfilepic());
                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView userName, lastMessage, lastTime ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profileimage);
            userName = itemView.findViewById(R.id.username2);
            lastMessage = itemView.findViewById(R.id.LastM);
            lastTime = itemView.findViewById(R.id.lastTime);
        }
    }
}
