package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.messagingapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setTitle("Login ");
        progressDialog.setMessage("Login into your account");
        FirebaseUser user = auth.getCurrentUser();

//        if(!user.isEmailVerified()){
//            binding.resend.setVisibility(View.VISIBLE);
//            binding.verifymsg.setVisibility(View.VISIBLE);
//
//            binding.resend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(SignIn.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("tag","onFailure: Email not sent "+e.getMessage());
//                        }
//                    });
//                }
//            });
//        }



        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.iemail.getText().toString().isEmpty()){
                    binding.iemail.setError("Enter your email");
                    return;
                }
                if(binding.ipassword.getText().toString().isEmpty()){
                    binding.ipassword.setError("Enter your password");
                    return;
                }
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.iemail.getText().toString(),binding.ipassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            if(auth.getCurrentUser().isEmailVerified() ){
                                Intent intent = new Intent(SignIn.this,MainActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(SignIn.this, "Please verify your email address" , Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(SignIn.this, "Error!! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.inew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
            }
        });
            if(user!=null && user.isEmailVerified()){
                Intent intent = new Intent(SignIn.this,MainActivity.class);
                startActivity(intent);
            }
    }
}