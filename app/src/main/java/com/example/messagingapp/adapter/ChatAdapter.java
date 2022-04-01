package com.example.messagingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.Models.MessageModel;
import com.example.messagingapp.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModel> messageModels;
    Context context;

//    Because we have 2 viewHolders
    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    String senderRoom;
    String receiverRoom;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String senderRoom, String receiverRoom) {
        this.messageModels = messageModels;
        this.context = context;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message,parent,false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message,parent,false);
            return new ReceiverViewHolder(view);
        }
    }
//For ViewHolders
    @Override
    public int getItemViewType(int position) {
//        Identifiying on which view type(sender,receiver or anything) which work should be done
//        sender is the one who has logged in the device so it will return sender id
        if(messageModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=messageModels.get(position);  //Whichever message comes first will be set first
        int reactions[] =new int[]{
                R.drawable.ic_angry,
                R.drawable.ic_laughing,
                R.drawable.ic_love,
                R.drawable.ic_sad,
                R.drawable.ic_surprised,
                R.drawable.ic_like
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).feelings.setImageResource(reactions[pos]);
                ((SenderViewHolder)holder).feelings.setVisibility(View.VISIBLE);
            }else {
                ((ReceiverViewHolder)holder).feelingr.setImageResource(reactions[pos]);
                ((ReceiverViewHolder)holder).feelingr.setVisibility(View.VISIBLE);
            }
            messageModel.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference().child("Chats").child(senderRoom).child("messages").child(messageModel.getMessageId()).setValue(messageModel);
            FirebaseDatabase.getInstance().getReference().child("Chats").child(receiverRoom).child("messages").child(messageModel.getMessageId()).setValue(messageModel);
            return true; // true is closing popup, false is requesting a new selection
        });

//        Data is set hare
//        Identifying sender and receiver messages

        if(holder.getClass() == SenderViewHolder.class){
            if(messageModel.getMessage().equals("Photo")){
                ((SenderViewHolder)holder).images.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageurl()).placeholder(R.drawable.ic_profile).into(((SenderViewHolder)holder).images);
            }
            long lastTime = messageModel.getTimestamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder)holder).times.setText(dateFormat.format(new Date(lastTime)));

            if(messageModel.getFeeling()>=0){
                ((SenderViewHolder)holder).feelings.setImageResource(reactions[(int) messageModel.getFeeling()]);
//                messageModel.setFeeling(reactions[(int) messageModel.getFeeling()]);
                ((SenderViewHolder)holder).feelings.setVisibility(View.VISIBLE);
            }else {
                ((SenderViewHolder)holder).feelings.setVisibility(View.GONE);
            }
            ((SenderViewHolder)holder).senderMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

        }else{
            if(messageModel.getMessage().equals("Photo")){
                ((ReceiverViewHolder)holder).imager.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageurl()).placeholder(R.drawable.ic_profile).into(((ReceiverViewHolder)holder).imager);
            }
            long lastTime = messageModel.getTimestamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            ((ReceiverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder)holder).timer.setText(dateFormat.format(new Date(lastTime)));

            if(messageModel.getFeeling()>=0){
//                messageModel.setFeeling(reactions[(int)messageModel.getFeeling()]);
                ((ReceiverViewHolder)holder).feelingr.setImageResource(reactions[(int) messageModel.getFeeling()]);
                ((ReceiverViewHolder)holder).feelingr.setVisibility(View.VISIBLE);

            }else {
                ((ReceiverViewHolder)holder).feelingr.setVisibility(View.GONE);
            }

            ((ReceiverViewHolder)holder).receiverMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
            ((ReceiverViewHolder)holder).imager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

//            Time can also be set here
        }
    }
    @Override
    public int getItemCount() {
        return messageModels.size();
    }
//we have 2 layouts 1) sender 2) receiver
//Therefore there will be 2 viewholder

//    Receivers viewHolder
    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, timer;
        ImageView feelingr, imager;
         public ReceiverViewHolder(@NonNull View itemView) {
              super(itemView);
              receiverMsg = itemView.findViewById(R.id.receiverId);
              timer = itemView.findViewById(R.id.timer);
             feelingr = itemView.findViewById(R.id.feelingr);

             imager = itemView.findViewById(R.id.imager);

        }
    }
//Sender ViewHolder
    public class SenderViewHolder extends RecyclerView.ViewHolder {
         TextView senderMsg, times;
         ImageView feelings , images;
         public SenderViewHolder(@NonNull View itemView) {
               super(itemView);
               senderMsg = itemView.findViewById(R.id.senderId);
               feelings = itemView.findViewById(R.id.feeling);
             times = itemView.findViewById(R.id.times);
               images = itemView.findViewById(R.id.image);

         }
    }
}
