package com.iti41g1.tripreminder.controller.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iti41g1.tripreminder.R;

import java.util.ArrayList;
import java.util.List;

import com.iti41g1.tripreminder.Adapters.AdapterAddNote;
import com.iti41g1.tripreminder.Models.NoteModel;
public class FragmentAddNotes extends Fragment {
    RecyclerView recyclerView;
    AdapterAddNote adapter;
    LinearLayoutManager linearLayoutManager;
    Button   btnAddNote;
    Button   btnSaveNotes;
    ArrayList<String> notes;
    String date;
    String time;
    String date2;
    String time2;
    ArrayList<String> notesl;

    public static final String TAG="Notes";

    public FragmentAddNotes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("datakey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                if(bundle!=null){
                 date = bundle.getString("date");
                 time=bundle.getString("time");
                 date2 = bundle.getString("date2");
                 time2=bundle.getString("time2");
                Log.i(TAG, "onFragmentResult:  AddNotes"+date+".."+time+".."+date2+".."+time2);
                // Do something with the result
            }}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_notes, container, false);
        btnAddNote=view.findViewById(R.id.btn_addNote);
        btnSaveNotes=view.findViewById(R.id.btn_saveNotes);
        recyclerView=view.findViewById(R.id.recyclerView);
        notes=new ArrayList<>();
        adapter=new AdapterAddNote(notes,getContext());
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        Log.i(TAG, "onCreateView: ");
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick:add button "+notes.toString());
             //   notes.add(new NoteModel("", false));
                notes.add("");
                Log.i(TAG, notes.toString());
                adapter.notifyDataSetChanged();
            }
        });
        btnSaveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle result = new Bundle();
                if(!notes.isEmpty()) {
                    for (int i = 0; i < notes.size(); i++) {
                        Log.i(TAG, "onClick:Savebutton " + notes.get(i));
                    }
                    //   Bundle result = new Bundle();
                    result.putStringArrayList("bundleKey", notes);
                }
                        if(date!="")
                        result.putString("date",date);
                        if(time!="")
                        result.putString("time",time);
                        if(date2!="")
                        result.putString("date2",date2);
                        if(time2!="")
                        result.putString("time2",time2);
                        getParentFragmentManager().setFragmentResult("requestKey", result);
                    Log.i(TAG, "onClick: addnotes"+result);


            }
        });
        return view;
    }


}