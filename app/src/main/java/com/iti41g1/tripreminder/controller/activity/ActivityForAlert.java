package com.iti41g1.tripreminder.controller.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
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
        showDialog(this);
    }

    private void showDialog(Context context){
        mp.start();
        new AlertDialog.Builder(context).setTitle("Alarm").setCancelable(false).setMessage("Your Trip "+tripName+" is now")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(Constants.LOG_TAG,"hello from start screen");
                initMap();
                initBubble();
                finishTrip();
                finish();
                mp.stop();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(Constants.LOG_TAG,"hello from stop screen");
                cancelTrip();
                finish();
                mp.stop();
            }
        }).setNeutralButton("Snooze", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sendNotification(context);
                Log.i(Constants.LOG_TAG,"hello from neutral screen");
                sendNotification();
                finish();
                mp.stop();
            }
        }).show();
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
                database.tripDAO().updateTripStatus(HomeActivity.fireBaseUseerId,tripId,"finished");
            }
        }).start();
    }

    public void cancelTrip(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.tripDAO().updateTripStatus(HomeActivity.fireBaseUseerId,tripId,"canceled");
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
}

