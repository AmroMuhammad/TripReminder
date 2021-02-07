package com.iti41g1.tripreminder.Adapters;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti41g1.tripreminder.Models.AlarmReceiver;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.controller.activity.LoginActivity;
import com.iti41g1.tripreminder.controller.services.FloatingViewService;
import com.iti41g1.tripreminder.database.Trip;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class TripUpcomingRecyclerAdapter extends RecyclerView.Adapter<TripUpcomingRecyclerAdapter.MyViewHolder> {
    List tripList;
    Context context;
    Activity activity;
    public TripUpcomingRecyclerAdapter(Context context, List TrripInfoList, Activity activity) {
        this.context = context;
        this.tripList=TrripInfoList;
        this.activity = activity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_upcoming_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        Trip trip= (Trip) tripList.get(position);
        holder.nameTxt.setText(trip.getTripName());
        holder.timeTxt.setText(trip.getTime());
        holder.dateTxt.setText(trip.getDate());
        holder.sourceTxt.setText(trip.getStartPoint());
        holder.destinationTxt.setText(trip.getEndPoint());
        holder.dateTxt.setText(trip.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              updateTrip((Trip) tripList.get(position));
                notifyDataSetChanged();
              holder.itemView.setVisibility(View.INVISIBLE);
            }
        });
//        holder.trip_img.setImageResource(trip.getTripImg());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               openDaialog(((Trip) tripList.get(position)),position-1);
                tripList.remove((Trip) tripList.get(position));
                return false;
            }
        });

        holder.start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMap(((Trip) tripList.get(position)).getEndPointLat(),((Trip) tripList.get(position)).getEndPointLong());
                initBubble(((Trip) tripList.get(position)).getId(),((Trip) tripList.get(position)).getUserID());
                unregisterAlarm((Trip) tripList.get(position));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HomeActivity.database.tripDAO().updateTripStatus(HomeActivity.fireBaseUseerId,((Trip) tripList.get(position)).getId(),"finished");
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void openDaialog(Trip trip ,int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure delete this trip");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HomeActivity.database.tripDAO().updateTripStatus(HomeActivity.fireBaseUseerId,trip.getId(),"canceled");
                                        unregisterAlarm(trip);
                                       // tripList.remove(trip);
                                    }
                                }).start();
                              //  notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void initMap(double latitude, double longtitude){
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination="+latitude+","+longtitude+"&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public void updateTrip(Trip trip){
        Intent intent = new Intent(context,AddTripActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("KEY",2);
        bundle.putInt("ID",trip.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sourceTxt,destinationTxt,nameTxt,timeTxt,dateTxt;
        ImageView trip_img;
        ImageButton start_btn;
        public MyViewHolder(View itemView) {
            super(itemView);
            sourceTxt=itemView.findViewById(R.id.source_txt);
            destinationTxt=itemView.findViewById(R.id.destination_txt);
            nameTxt=itemView.findViewById(R.id.name_txt);
            timeTxt=itemView.findViewById(R.id.time_txt);
            dateTxt=itemView.findViewById(R.id.date_txt);
            trip_img=itemView.findViewById(R.id.trip_img);
            start_btn= itemView.findViewById(R.id.start_btn);
        }

    }

    public void unregisterAlarm(Trip trip) {
        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context,trip.getId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(notifyPendingIntent);
        }
    }

    public void initBubble(int tripId, String tripUserId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            askPermission();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent(context, FloatingViewService.class);
            intent.putExtra(Constants.TRIP_ID,tripId);
            context.startService(intent);
            activity.finish();
        } else if (Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(context, FloatingViewService.class);
            intent.putExtra(Constants.TRIP_ID,tripId);
            context.startService(intent);
            activity.finish();
        } else {
            askPermission();
            Toast.makeText(context, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
        activity.startActivityForResult(intent, 2084);
    }


}
