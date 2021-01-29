package com.iti41g1.tripreminder.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.model.Constants;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listner;  //listener to check if signed in or not
    private List<AuthUI.IdpConfig> providers; //to get sign in  with email and google
    private FirebaseUser user;
    private Button btnSignOut;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listner);
    }

    @Override
    protected void onStop() {
        if(listner != null)
            firebaseAuth.removeAuthStateListener(listner);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignOut = findViewById(R.id.btnSignOut);
        initializeSignInProcess();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
    }


    private void initializeSignInProcess() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        firebaseAuth = FirebaseAuth.getInstance();
        listner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    if(user != null){
                        Log.i(Constants.LOG_TAG,"signed in + "+user.getEmail());  //already signed in
                    }else{
                        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                                setIsSmartLockEnabled(false)
                                .setTheme(R.style.AuthenticationTheme)
                                .setAvailableProviders(providers).build(),Constants.AUTH_REQUEST_CODE);
                    }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTH_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Log.i(Constants.LOG_TAG,"signed in successfully");  //already signed in
            } else {
                Log.i(Constants.LOG_TAG,"not signed in successfully");  //already signed in
                finish();
            }
        }
    }

}