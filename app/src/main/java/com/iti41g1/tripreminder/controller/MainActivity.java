package com.iti41g1.tripreminder.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.iti41g1.tripreminder.R;

import Fragments.FragmentAddTrip;

public class MainActivity extends AppCompatActivity {


    FragmentManager f;
    Fragment fragmentB;
    Button btnSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         f=getSupportFragmentManager();
        if(savedInstanceState==null){
            fragmentB = new FragmentAddTrip();
            f.beginTransaction().add(R.id.fragmentB, fragmentB, "fragment").commit();
        }
        else
        {
            fragmentB= (FragmentAddTrip) f.findFragmentByTag("fragment");
        }
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}