package com.iti41g1.tripreminder.controller.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.services.FloatingViewService;
import com.iti41g1.tripreminder.database.Trip;
import com.iti41g1.tripreminder.database.TripDatabase;

public class ActivityForAlert extends AppCompatActivity {
    NotificationManager manager;
    MediaPlayer mp;
    SharedPreferences.Editor sharedEditor;
    SharedPreferences sharedPreferences;
    String tripName;
    int tripId;
    String tripUserId;
    double tripLong;
    double tripLat;
    TripDatabase database;
    String firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences("tripInfo",MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();
        if(getIntent().hasExtra(Constants.TRIP_ID)){
            sharedEditor.putString(Constants.TRIP_NAME,getIntent().getExtras().getString(Constants.TRIP_NAME));
            sharedEditor.putInt(Constants.TRIP_ID,getIntent().getExtras().getInt(Constants.TRIP_ID));
            sharedEditor.putString(Constants.TRIP_USER_ID,getIntent().getExtras().getString(Constants.TRIP_USER_ID));
            sharedEditor.putString(Constants.TRIP_LONGITUDE,getIntent().getExtras().getDouble(Constants.TRIP_LONGITUDE)+"");
            sharedEditor.putString(Constants.TRIP_LATITUDE,getIntent().getExtras().getDouble(Constants.TRIP_LATITUDE)+"");
            sharedEditor.commit();
        }
        tripName = sharedPreferences.getString(Constants.TRIP_NAME,"none");
        tripId = sharedPreferences.getInt(Constants.TRIP_ID,-1);
        tripUserId = sharedPreferences.getString(Constants.TRIP_USER_ID,"none");
        tripLat = Double.parseDouble(sharedPreferences.getString(Constants.TRIP_LATITUDE,"0"));
        tripLong = Double.parseDouble(sharedPreferences.getString(Constants.TRIP_LONGITUDE,"0"));

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mp = MediaPlayer.create(ActivityForAlert.this, R.raw.alarmsound);
        mp.setLooping(true);
        customDialog(this);
    }


    private void sendNotification() {
        Notification.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,"Trip Reminder", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(this,Constants.NOTIFICATION_CHANNEL_ID);
        }
        else
        {
            builder = new Notification.Builder(this);
        }
        Intent intent = new Intent(this, ActivityForAlert.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentTitle("WakeUp");
        builder.setContentText("Your trip "+tripName+" is upcoming");
        builder.setSmallIcon(R.drawable.ic_travel_bag);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        Notification notification =builder.build();
        manager.notify(10,notification);
    }


    public void initMap(){

        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination=" +tripLat+","+tripLong +"&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
    public void finishTrip(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.tripDAO().updateTripStatus(firebaseUser,tripId,"finished");
            }
        }).start();
    }

    public void cancelTrip(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.tripDAO().updateTripStatus(firebaseUser,tripId,"cancelled");
            }
        }).start();
    }
    public void initBubble(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent(this, FloatingViewService.class);
            intent.putExtra(Constants.TRIP_ID,tripId);
            startService(intent);
            finish();
        } else if (Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(this, FloatingViewService.class);
            intent.putExtra(Constants.TRIP_ID,tripId);
            startService(intent);
            finish();
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 2084);
    }

    public void customDialog(Context context){
        mp.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_alarm_dialog,(ConstraintLayout) findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Constants.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Your Trip "+tripName+" is now");
        ((Button)view.findViewById(R.id.btnSnooze)).setText(Constants.PER_DIALOG_SNOOZE);
        ((Button)view.findViewById(R.id.btnCancel)).setText(Constants.PER_DIALOG_CANCEL);
        ((Button)view.findViewById(R.id.btnStart)).setText(Constants.PER_DIALOG_START);
        ((ImageView)view.findViewById(R.id.imgTitle)).setImageResource(R.drawable.ic_baseline_hourglass_bottom_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btnSnooze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendNotification(context);
                Log.i(Constants.LOG_TAG,"hello from neutral screen");
                sendNotification();
                finish();
                mp.stop();
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Constants.LOG_TAG,"hello from stop screen");
                cancelTrip();
                finish();
                mp.stop();
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Constants.LOG_TAG,"hello from start screen");
                initMap();
                initBubble();
                finishTrip();
                finish();
                mp.stop();
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}

