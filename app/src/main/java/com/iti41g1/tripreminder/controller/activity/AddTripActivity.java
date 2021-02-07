package com.iti41g1.tripreminder.controller.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.iti41g1.tripreminder.R;

import com.iti41g1.tripreminder.controller.Fragments.FragmentAddNotes;
import com.iti41g1.tripreminder.controller.Fragments.FragmentAddTrip;

public class AddTripActivity extends AppCompatActivity {
    FragmentManager f;
    Fragment fragmentB;
    public static int key;
    public static int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Settings.canDrawOverlays(this)) {
            errorWarningForNotGivingDrawOverAppsPermissions();
        }
        Log.i(FragmentAddTrip.TAG, "onCreate: Ac");
        setContentView(R.layout.activity_add_trip);
        Bundle bundle=getIntent().getExtras();
        key=bundle.getInt("KEY");
        ID=bundle.getInt("ID");

        Toast.makeText(this,key +"key  ID"+ID,Toast.LENGTH_LONG).show();
        if(key==3){
            f = getSupportFragmentManager();
            fragmentB = new FragmentAddNotes();
            f.beginTransaction().add(R.id.fragmentB, fragmentB, "fragment").commit();
        }else{
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



    }


    private void errorWarningForNotGivingDrawOverAppsPermissions(){
        new AlertDialog.Builder(this).setTitle("Warning").setCancelable(false).setMessage("Unfortunately the display over other apps permission" +
                " is not granted so the application might not behave properly \nTo enable this permission kindly restart the application" )
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", fragmentB);
        Log.i(FragmentAddTrip.TAG, "onSaveInstanceState:Ac ");
    }
}