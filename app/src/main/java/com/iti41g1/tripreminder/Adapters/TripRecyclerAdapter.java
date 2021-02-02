package com.iti41g1.tripreminder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.database.Trip;

import java.util.List;

public class TripRecyclerAdapter extends RecyclerView.Adapter<TripRecyclerAdapter.MyViewHolder> {
    List TrripInfoList;
    Context context;
    public TripRecyclerAdapter(Context context, List TrripInfoList) {
        this.context = context;
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
        Trip trip= (Trip) TrripInfoList.get(position);
        holder.nameTxt.setText(trip.getTripName());
        holder.timeTxt.setText(trip.getTime());
        holder.dateTxt.setText(trip.getDate());
        holder.sourceTxt.setText(trip.getStartPoint());
        holder.destinationTxt.setText(trip.getEndPoint());
        holder.dateTxt.setText(trip.getDate());
//        holder.trip_img.setImageResource(trip.getTripImg());
        holder.start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dont forget to change status in database
                initMap(((Trip) TrripInfoList.get(position)).getEndPointLat(),((Trip) TrripInfoList.get(position)).getEndPointLong());
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

    public void initMap(double latitude, double longtitude){
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination="+latitude+","+longtitude+"&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }
}
