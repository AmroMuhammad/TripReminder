package com.iti41g1.tripreminder.controller.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti41g1.tripreminder.Adapters.TripHistoryRecyclerAdapter;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.HistoryMapActivity;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView tripRecyclerView;
    ImageView emptyListImg;
    int finishedTripsNum;
    private TripHistoryRecyclerAdapter tripRecyclerAdapter;
    private List tripsList = new ArrayList<Trip>();
    public static FloatingActionButton historyMapBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadRoomData().execute();
        tripRecyclerView = view.findViewById(R.id.trip_recycleView);
        tripRecyclerAdapter = new TripHistoryRecyclerAdapter(getContext(), tripsList,getActivity());
        tripRecyclerView.setAdapter(tripRecyclerAdapter);
        emptyListImg = view.findViewById(R.id.emptyList_img);
        historyMapBtn = view.findViewById(R.id.map_float_btn);

        historyMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HistoryMapActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    private class LoadRoomData extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            finishedTripsNum=HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId,"finished");
            return HomeActivity.database.tripDAO().selectHistoryTrip(HomeActivity.fireBaseUseerId, "cancelled", "finished","missed");
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            tripsList = trips;
            if (tripsList.isEmpty()) {
                emptyListImg.setVisibility(View.VISIBLE);
                emptyListImg.setImageResource(R.drawable.empty);
                historyMapBtn.setVisibility(View.GONE);
            } else {
                emptyListImg.setVisibility(View.GONE);
            }
            if(finishedTripsNum==0)
            {
                historyMapBtn.setVisibility(View.GONE);
            }
            tripRecyclerAdapter = new TripHistoryRecyclerAdapter(getContext(), tripsList,getActivity());
            tripRecyclerView.setAdapter(tripRecyclerAdapter);
        }
    }
}