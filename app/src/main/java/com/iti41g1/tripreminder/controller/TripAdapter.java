package com.iti41g1.tripreminder.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.R;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {
    List TrripInfoList;
    public TripAdapter(List TrripInfoList) {
        this.TrripInfoList=TrripInfoList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
        TripInfo trip= (TripInfo) TrripInfoList.get(position);
        holder.nameTxt.setText(trip.getName());
        holder.timeTxt.setText(trip.getTime());
        holder.dateTxt.setText(trip.getDate());
        holder.sourceTxt.setText(trip.getSource());
        holder.destinationTxt.setText(trip.getDestination());
        holder.dateTxt.setText(trip.getDate());
        holder.trip_img.setImageResource(trip.getTripImg());
        holder.start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }


    @Override
    public int getItemCount() {
        return TrripInfoList.size();
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
}
