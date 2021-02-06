package com.iti41g1.tripreminder.Models;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iti41g1.tripreminder.controller.activity.ActivityForAlert;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentOutgoing = new Intent(context, ActivityForAlert.class);
        intentOutgoing.putExtra(Constants.TRIP_NAME,intent.getExtras().getString(Constants.TRIP_NAME));
        intentOutgoing.putExtra(Constants.TRIP_ID,intent.getExtras().getInt(Constants.TRIP_ID));
        intentOutgoing.putExtra(Constants.TRIP_USER_ID,intent.getExtras().getString(Constants.TRIP_USER_ID));
        intentOutgoing.putExtra(Constants.TRIP_LATITUDE,intent.getExtras().getDouble(Constants.TRIP_LATITUDE));
        intentOutgoing.putExtra(Constants.TRIP_LONGITUDE,intent.getExtras().getDouble(Constants.TRIP_LONGITUDE));
        intentOutgoing.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startActivity(intentOutgoing);
    }
}
