package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.messagingapp.adapter.FragAdapter;
import com.example.messagingapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth =FirebaseAuth.getInstance();

        binding.viewPager.setAdapter((new FragAdapter(getSupportFragmentManager())));
        binding.tabLayout.setupWithViewPager(binding.viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()) {
           case R.id.setting:
               Intent i = new Intent(MainActivity.this,Setting.class);
               startActivity(i);
               Toast.makeText(this, "This is setting", Toast.LENGTH_SHORT).show();
               break;

           case R.id.logout:
               auth.signOut();
               Intent intent=new Intent(MainActivity.this,SignIn.class);
               startActivity(intent);
               break;

       }
        return super.onOptionsItemSelected(item);
    }
}