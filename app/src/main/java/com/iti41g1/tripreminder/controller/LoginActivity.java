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
    //private FirebaseAuth.AuthStateListener listner;  //listener to check if signed in or not
    private List<AuthUI.IdpConfig> providers; //to get sign in  with email and google
    private FirebaseUser user;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(listner);
//        Log.i(Constants.LOG_TAG,"onStart");
//    }

//    @Override
//    protected void onStop() {
//        if(listner != null) {
//            firebaseAuth.removeAuthStateListener(listner);
//            Log.i(Constants.LOG_TAG,"onStop");
//        }
//        super.onStop();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(Constants.LOG_TAG,"onCreate");
        initializeSignInProcess();

    }


    private void initializeSignInProcess() {
        Log.i(Constants.LOG_TAG,"initializeProcess");

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        firebaseAuth = FirebaseAuth.getInstance();
        Log.i(Constants.LOG_TAG,"checkUser");
        user = firebaseAuth.getCurrentUser();
        if(user != null){
            Log.i(Constants.LOG_TAG,"signed in + "+user.getEmail());  //already signed in
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else{
            Log.i(Constants.LOG_TAG,"sign in process");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                    setIsSmartLockEnabled(false)
                    .setTheme(R.style.AuthenticationTheme)
                    .setAvailableProviders(providers).build(),Constants.AUTH_REQUEST_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(Constants.LOG_TAG,"onActivityResult");
        if (requestCode == Constants.AUTH_REQUEST_CODE) {
            Log.i(Constants.LOG_TAG,"Auth request code is correct");
            if (resultCode == RESULT_OK) {
                Log.i(Constants.LOG_TAG,"signed in successfully");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Log.i(Constants.LOG_TAG,"not signed in successfully");
                finish();
            }
        }
    }

}