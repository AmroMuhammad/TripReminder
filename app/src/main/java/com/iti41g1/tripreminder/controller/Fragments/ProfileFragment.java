package com.iti41g1.tripreminder.controller.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iti41g1.tripreminder.Adapters.TripUpcomingRecyclerAdapter;
import com.iti41g1.tripreminder.Models.AlarmReceiver;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.controller.activity.LoginActivity;
import com.iti41g1.tripreminder.database.Trip;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class ProfileFragment extends Fragment {
    ImageView imgLogout;
    ImageView imgSync;
    ImageView imgAboutAs;
    ImageView imgHowToUse;
    ImageView imgTripReport;
    TextView txtLogout;
    TextView txtSync;
    TextView txtAboutAs;
    TextView txtHowToUse;
    TextView txtTripsReport;
    TextView txtName;
    TextView txtEmail;
    CircleImageView imgProfilePhoto;
    String reportTripMessage;
public static final String TAG="profile";
   // public DatabaseReference databaseRef =FirebaseDatabase.getInstance().getReference();
     List<Trip>trips;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetCountTrips().execute();
        imgLogout = view.findViewById(R.id.imgLogout);
        imgSync = view.findViewById(R.id.imgSync);
        imgHowToUse = view.findViewById(R.id.imgHowToUse);
        imgAboutAs = view.findViewById(R.id.imgAboutAs);
        imgTripReport=view.findViewById(R.id.imgTripsReport);
        txtLogout = view.findViewById(R.id.txtLogout);
        txtSync = view.findViewById(R.id.txtSync);
        txtHowToUse = view.findViewById(R.id.txtHowToUse);
        txtAboutAs = view.findViewById(R.id.txtAboutAs);
        txtTripsReport = view.findViewById(R.id.txtTripsReport);
        txtName = view.findViewById(R.id.name_txt);
        txtEmail =view.findViewById(R.id.email_txt);
        imgProfilePhoto=view.findViewById(R.id.imgProfilePhoto);
        txtEmail.setText(HomeActivity.fireBaseEmail);
        txtName.setText(HomeActivity.fireBaseUserName);
        if(HomeActivity.fireBaseUserPhotoUri!=null) {

            Picasso.get().load(HomeActivity.fireBaseUserPhotoUri).into(imgProfilePhoto);
        }
        new readData().execute();
        initializeLogOut();
        getTripsReport();
        intializeAboutUs();
        intializeHowToUse();

        imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             writeOnFireBase(trips);
            }
        });
        txtSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeOnFireBase(trips);
            }
        });
    }
    
    public void initializeLogOut() {
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new UnregisterData().execute();
                        writeOnFireBase(trips);
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new UnregisterData().execute();
                        writeOnFireBase(trips);
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
            }
        });
    }
    public void initializeSync(){

    }
    private class UnregisterData extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return HomeActivity.database.tripDAO().selectUpcomingTrip(HomeActivity.fireBaseUseerId, "upcoming");
        }
        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            for(int i=0;i<trips.size();i++){
                if(trips.get(i).getTripStatus().equals("upcoming")){
                    unregisterAlarm(trips.get(i));
                }
            }
        }
    }
    public void unregisterAlarm(Trip trip) {
        Intent notifyIntent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (getContext(),trip.getId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(notifyPendingIntent);
        }
    }
    public void writeOnFireBase(List<Trip>trips){
        if(isNetworkAvailable(getContext())) {
            Trip trip;
            LoginActivity.databaseRef.child("TripReminder").child("userID").child(HomeActivity.fireBaseUseerId).child("trips").removeValue();

            for (int i = 0; i < trips.size(); i++) {
                trip = new Trip(trips.get(i).getUserID(),trips.get(i).getTripName(),trips.get(i).getStartPoint(),
                        trips.get(i).getStartPointLat(),trips.get(i).getStartPointLong(),trips.get(i).getEndPoint(),
                        trips.get(i).getEndPointLat(),trips.get(i).getEndPointLong(),trips.get(i).getDate(),
                        trips.get(i).getTime(),trips.get(i).getTripImg(),trips.get(i).getTripStatus(),
                        trips.get(i).getCalendar(), trips.get(i).getNotes());
                Log.i(TAG, "writeOnFireBase: " + trip.getTripName() + trip.getId() + trip.getStartPoint()+trip.getNotes());
                 LoginActivity.databaseRef.child("TripReminder").child("userID").child(HomeActivity.fireBaseUseerId).child("trips").push().setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getContext(), "Success Store Data in FireBase", Toast.LENGTH_SHORT).show();
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getContext(), "Failure Store Data in FireBase", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
        else{
            Toast.makeText(getContext(), "No Internet ", Toast.LENGTH_SHORT).show();
        }
       Log.i(TAG, "writeOnFireBase: ");
    }

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class readData extends AsyncTask<Void, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return  HomeActivity.database.tripDAO().selectAll(HomeActivity.fireBaseUseerId);
        }
        @Override
        protected void onPostExecute(List<Trip> tripsl) {
            super.onPostExecute(trips);
            trips=tripsl;
        }
    }


    private void getTripsReport() {
        txtTripsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openDaialog(reportTripMessage);
            }
        });

        imgTripReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaialog(reportTripMessage);
            }
        });
    }
    private void intializeAboutUs() {
        txtAboutAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaialog("Amr \n Nermeen \n Donia");
            }
        });

        imgAboutAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaialog(" Amr \n Nermeen \n Donia");
            }
        });
    }
    private void intializeHowToUse() {
        txtHowToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaialog(" How to Use ? ");
            }
        });

        imgHowToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDaialog(" How to Use ? ");
            }
        });
    }
    public void openDaialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
     class GetCountTrips extends AsyncTask<Void, Void,String> {

        @Override
        protected  String doInBackground(Void... voids) {
            String reportMsg = " Total of upcoming trips : ";
            reportMsg += HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId, "upcoming");
            reportMsg +="\n Total of finished trips : ";
            reportMsg += HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId, "finished");
            reportMsg +="\n Total of cancelled trips : ";
            reportMsg += HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId, "cancelled");
            reportMsg +="\n Total of missed trips : ";
            reportMsg += HomeActivity.database.tripDAO().getCountTripType(HomeActivity.fireBaseUseerId, "missed");
             return reportMsg;
        }

        @Override
        protected void onPostExecute(String reportMsg) {
            super.onPostExecute(reportMsg);
            reportTripMessage=reportMsg;

        }
    }
}