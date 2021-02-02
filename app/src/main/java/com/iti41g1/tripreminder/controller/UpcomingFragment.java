package com.iti41g1.tripreminder.controller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.TripAdapter;
import com.iti41g1.tripreminder.controller.TripInfo;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment {
ImageView emptyListImg;
    RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private List tripsList=new ArrayList<TripInfo>(); ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsListPrepare();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripRecyclerView=view.findViewById(R.id.trip_recycleView);
        TripAdapter tripAdapter=new TripAdapter(tripsList);
        tripRecyclerView.setAdapter(tripAdapter);
        emptyListImg=view.findViewById(R.id.emptyList_img);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(tripsList.isEmpty())
        {
            emptyListImg.setVisibility(View.VISIBLE);
            emptyListImg.setImageResource(R.drawable.preview);

        }
        else
        {
            emptyListImg.setVisibility(View.GONE);
        }
    }
    private void tripsListPrepare() {
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
        tripsList.add(new TripInfo(R.drawable.ic_baseline_add_24,"iti","12/12/2021","10:05","assuit","cairo",new String []{"hi","hello"}));
    }
}