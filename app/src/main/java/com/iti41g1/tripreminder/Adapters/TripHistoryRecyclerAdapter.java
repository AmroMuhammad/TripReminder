package com.iti41g1.tripreminder.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.List;

public class TripHistoryRecyclerAdapter extends RecyclerView.Adapter<TripHistoryRecyclerAdapter.MyViewHolder> {
    List tripList;
    Context context;
    public TripHistoryRecyclerAdapter(Context context, List TrripInfoList) {
        this.context = context;
        this.tripList=TrripInfoList;
    }
    @NonNull
    @Override
    public TripHistoryRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_history_item, parent, false);
        return new TripHistoryRecyclerAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(TripHistoryRecyclerAdapter.MyViewHolder holder, int position) {
        Trip trip= (Trip) tripList.get(position);
        holder.nameTxt.setText(trip.getTripName());
        holder.timeTxt.setText(trip.getTime());
        holder.dateTxt.setText(trip.getDate());
        holder.startPointTxt.setText(trip.getStartPoint());
        holder.endPointTxt.setText(trip.getEndPoint());
        holder.stutasTxt.setText(trip.getTripStatus());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openDaialog(((Trip) tripList.get(position)),position);
                tripList.remove(((Trip) tripList.get(position)));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void openDaialog(Trip trip , int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure delete this trip");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HomeActivity.database.tripDAO().deleteById(HomeActivity.fireBaseUseerId,trip.getId());
                             //   tripList.remove(trip);


                            }
                        }).start();
                        notifyItemRemoved(position);
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


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView startPointTxt, endPointTxt,nameTxt,timeTxt,dateTxt,stutasTxt;
        ImageView trip_img;
        public MyViewHolder(View itemView) {
            super(itemView);
            startPointTxt=itemView.findViewById(R.id.source_txt);
            endPointTxt =itemView.findViewById(R.id.destination_txt);
            nameTxt=itemView.findViewById(R.id.name_txt);
            timeTxt=itemView.findViewById(R.id.time_txt);
            dateTxt=itemView.findViewById(R.id.date_txt);
            trip_img=itemView.findViewById(R.id.trip_img);
            stutasTxt= itemView.findViewById(R.id.status_txt);
        }

    }


}

