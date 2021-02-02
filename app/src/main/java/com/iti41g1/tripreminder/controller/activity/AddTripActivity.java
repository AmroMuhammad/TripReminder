package com.iti41g1.tripreminder.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.iti41g1.tripreminder.R;

import com.iti41g1.tripreminder.controller.Fragments.FragmentAddTrip;

public class AddTripActivity extends AppCompatActivity {
    FragmentManager f;
    Fragment fragmentB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        f=getSupportFragmentManager();
        if(savedInstanceState==null){
            fragmentB = new FragmentAddTrip();
            f.beginTransaction().add(R.id.fragmentB, fragmentB, "fragment").commit();
        }
        else
        {
            fragmentB= (FragmentAddTrip) f.findFragmentByTag("fragment");
        }
    }


}