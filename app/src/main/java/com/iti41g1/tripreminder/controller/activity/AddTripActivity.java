package com.iti41g1.tripreminder.controller.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;

import com.iti41g1.tripreminder.controller.Fragments.FragmentAddNotes;
import com.iti41g1.tripreminder.controller.Fragments.FragmentAddTrip;
import com.iti41g1.tripreminder.database.Trip;

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

        //Toast.makeText(this,key +"key  ID"+ID,Toast.LENGTH_LONG).show();
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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", fragmentB);
        Log.i(FragmentAddTrip.TAG, "onSaveInstanceState:Ac ");
    }

    private void errorWarningForNotGivingDrawOverAppsPermissions(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_warning,(ConstraintLayout) findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Constants.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Unfortunately the display over other apps permission" +
                " is not granted so the application might not behave properly \nTo enable this permission kindly restart the application");
        ((Button)view.findViewById(R.id.btnOk)).setText(Constants.PER_DIALOG_OK);
        ((ImageView)view.findViewById(R.id.imgTitle)).setImageResource(R.drawable.ic_baseline_warning_24);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();


        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}