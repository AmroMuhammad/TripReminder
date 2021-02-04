package com.iti41g1.tripreminder.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.iti41g1.tripreminder.R;

import com.iti41g1.tripreminder.controller.Fragments.FragmentAddNotes;
import com.iti41g1.tripreminder.controller.Fragments.FragmentAddTrip;

public class AddTripActivity extends AppCompatActivity {
    FragmentManager f;
    Fragment fragmentB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(FragmentAddTrip.TAG, "onCreate: Ac");
        setContentView(R.layout.activity_add_trip);

        if(savedInstanceState==null) {
            f = getSupportFragmentManager();
            fragmentB = new FragmentAddTrip();
            f.beginTransaction().add(R.id.fragmentB, fragmentB, "fragment").commit();
        }
        if(savedInstanceState!=null){
            fragmentB=f.findFragmentByTag("fragment");
            fragmentB = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");

        }


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", fragmentB);
        Log.i(FragmentAddTrip.TAG, "onSaveInstanceState:Ac ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(FragmentAddTrip.TAG, "onStop: Ac");
    }
}