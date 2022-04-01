package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;


import com.example.messagingapp.Models.MessageModel;
import com.example.messagingapp.adapter.ChatAdapter;
import com.example.messagingapp.databinding.ActivityChatDatailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class ChatDatail extends AppCompatActivity {
   ActivityChatDatailBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String receiveId,senderId,receiverRoom,senderRoom,m,filepath,randomKey;
    static final EncryDecry encryDecry = new EncryDecry();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDatailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Pic....");
        dialog.setCancelable(false);

       senderId = auth.getUid();// Get sender ID
//        From UserAdapter onBind
        receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("ProfilePic");

        binding.userName2.setText(userName);
//        load Profile Picture
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_profile).into(binding.profileimage);
        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDatail.this,MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        senderRoom = senderId + receiveId;
        receiverRoom = receiveId + senderId;
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this, senderRoom, receiverRoom);


        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        binding.chatRecycler.setAdapter(chatAdapter);
        binding.chatRecycler.setLayoutManager(layoutManager);
//        ((LinearLayoutManager)binding.chatRecycler.getLayoutManager()).setStackFromEnd(true);



//        Take data from firebase and add it show on recyclerview
            database.getReference().child("Chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageModels.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                        MessageModel model = snapshot1.getValue(MessageModel.class);
                        model.setMessageId(snapshot1.getKey());
//                        if(!model.getMessage().equals("Photo")) {
                            m = model.getMessage();
                            try {
                                m = encryDecry.Decrypt(m, "secretKey");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            model.setMessage(m);
//                            model.setImageurl(filepath);
                        messageModels.add(model);
                    }
                    chatAdapter.notifyDataSetChanged();//update recyclerview at runtime
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



//        send data to firebase and save
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!binding.sdMessage.getText().toString().isEmpty()) {
                    String message = binding.sdMessage.getText().toString();
                    String message1 = null;
                    try {
                        message1 = encryDecry.Encrypt(message, "secretKey");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Date date = new Date();
                    final MessageModel model = new MessageModel(senderId, message, date.getTime());
                    final MessageModel modele = new MessageModel(senderId, message1, date.getTime());

                    binding.sdMessage.setText("");//Empty the text bar once message is send
                    randomKey = database.getReference().push().getKey();
                    HashMap<String, Object> lastMsg = new HashMap<>();
                    lastMsg.put("lastMsg", model.getMessage());
                    lastMsg.put("lastMsgTime", date.getTime());

                    database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsg);
                    database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsg);

                    database.getReference().child("Chats").child(senderRoom).child("messages").child(randomKey).setValue(modele).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {
//                        Log.d("Database sender", model.getMessage());
                            database.getReference().child("Chats").child(receiverRoom).child("messages").child(randomKey).setValue(modele).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });


                        }
                    });


            }
        });


//        for sending pictures
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==25){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedImg = data.getData();
                    Calendar calendar =Calendar.getInstance();
                    StorageReference reference =storage.getReference().child("Pictures").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(@NonNull Uri uri) {
                                        filepath = uri.toString();
                                        String message= binding.sdMessage.getText().toString();
                                        Date date = new Date();

                                        final MessageModel model= new MessageModel(senderId,message,date.getTime());
                                         message = "Photo";
////                                        String ph1 =null;
//                                        try {
//                                            message = encryDecry.Encrypt(message,"secretKey");
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }


                                        model.setMessage(message);

                                        model.setImageurl(filepath);
                                        binding.sdMessage.setText("");//Empty the text bar once message is send
                                        String randomKey = database.getReference().push().getKey();
                                        HashMap<String, Object> lastMsg = new HashMap<>();
                                        lastMsg.put("lastMsg",model.getMessage());
                                        lastMsg.put("lastMsgTime",date.getTime());

                                        database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsg);
                                        database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsg);

                                        database.getReference().child("Chats").child(senderRoom).child("messages").child(randomKey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("Chats").child(receiverRoom).child("messages").child(randomKey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }
}