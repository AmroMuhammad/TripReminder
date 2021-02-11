package com.iti41g1.tripreminder.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.Fragments.HistoryFragment;
import com.iti41g1.tripreminder.controller.activity.AddTripActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.List;

public class TripHistoryRecyclerAdapter extends RecyclerView.Adapter<TripHistoryRecyclerAdapter.MyViewHolder> {
    List tripList;
    Context context;
    Activity activity;
    Handler handler;
    public TripHistoryRecyclerAdapter(Context context, List TrripInfoList, Activity ac) {
        this.context = context;
        this.tripList=TrripInfoList;
        activity =ac;
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
                customTwoButtonsDialog(((Trip) tripList.get(position)),position);
                handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        int finishedTripsNum = msg.arg1;
                        if(finishedTripsNum==0)
                        {

                            HistoryFragment.historyMapBtn.setVisibility(View.GONE);
                        }
                    }
                };
                tripList.remove(((Trip) tripList.get(position)));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
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

    public void customTwoButtonsDialog(Trip trip , int position){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_permission_dialog,(ConstraintLayout) activity.findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Constants.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Do you want to delete this trip ?");
        ((Button)view.findViewById(R.id.btnCancel)).setText(Constants.PER_DIALOG_CANCEL);
        ((Button)view.findViewById(R.id.btnOk)).setText(Constants.PER_DIALOG_CONFIRM);
        ((ImageView)view.findViewById(R.id.imgTitle)).setImageResource(R.drawable.ic_baseline_hourglass_bottom_24);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HomeActivity.database.tripDAO().deleteById(HomeActivity.fireBaseUseerId,trip.getId());
                        //   tripList.remove(trip);
                        int finishesTripNum=HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId,"finished");
                        Message msg=new Message();
                        msg.arg1  = finishesTripNum;
                        handler.sendMessage(msg);

                    }
                }).start();
                //  notifyItemRemoved(position);
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });


        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


}