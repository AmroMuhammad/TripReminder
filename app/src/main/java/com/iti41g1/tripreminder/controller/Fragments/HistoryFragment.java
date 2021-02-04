package com.iti41g1.tripreminder.controller.Fragments;

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

import com.iti41g1.tripreminder.Adapters.TripHistoryRecyclerAdapter;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    RecyclerView tripRecyclerView;
    ImageView emptyListImg;
    private TripHistoryRecyclerAdapter tripRecyclerAdapter;
    private List tripsList = new ArrayList<Trip>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadRoomData().execute();
        tripRecyclerView = view.findViewById(R.id.trip_recycleView);
        tripRecyclerAdapter = new TripHistoryRecyclerAdapter(getContext(), tripsList);
        tripRecyclerView.setAdapter(tripRecyclerAdapter);
        emptyListImg = view.findViewById(R.id.emptyList_img);
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

            return HomeActivity.database.tripDAO().selectHistoryTrip(HomeActivity.fireBaseUseerId, "canceled", "finished");
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            tripsList = trips;
            if (tripsList.isEmpty()) {
                emptyListImg.setVisibility(View.VISIBLE);
                emptyListImg.setImageResource(R.drawable.preview);
            } else {
                emptyListImg.setVisibility(View.GONE);
            }
            tripRecyclerAdapter = new TripHistoryRecyclerAdapter(getContext(), tripsList);
            tripRecyclerView.setAdapter(tripRecyclerAdapter);
        }
    }
}