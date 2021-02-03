package com.iti41g1.tripreminder.controller.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.Models.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.iti41g1.tripreminder.Adapters.AdapterAddNote;
import com.iti41g1.tripreminder.Models.NoteModel;
import com.iti41g1.tripreminder.controller.activity.HomeActivity;
import com.iti41g1.tripreminder.database.Trip;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentAddTrip extends Fragment {
    EditText editTextTripName;
    EditText editTextStartPoint;
    EditText editTextEndPoint;
    TextView textViewTripName;
    TextView textViewDate;
    TextView textViewDate2;
    Button btnAddNotes;
    Button btnSaveTrip;
    ImageButton imageButtonDate;
    ImageButton imageButtonDate2;
    TextView textViewTime;
    TextView textViewTime2;
    ImageButton imageButtonTime;
    ImageButton imageButtonTime2;
    RadioButton radioButtonOneDirection;
    RadioButton radioButtonRoundTrip;
    ConstraintLayout constraintLayoutNotes;
    ConstraintLayout constraintLayoutRoundTrip;
    RecyclerView recyclerView;
    AdapterAddNote adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<NoteModel> notes;
    boolean isDateCorrect = false;
    boolean isTimeCorrect = false;
    boolean isDateCorrectRoundTrip = false;
    boolean isTimeCorrectRoundTrip = false;
    boolean isDateToday=false;
    boolean isDateTodayRoundTrip=false;
    boolean isFirstTimeSeleceted=false;

    private static final String TAG = "AddTripFragment";
    private static final String apiKey = "AIzaSyAKXUZsOm7RLbPEAQQxp6TZsU9YWLeh5Pg";
    private static final int AUTOCOMPLETE_REQUEST_CODE_STARTPOINT = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE_ENDPOINT = 2;
    Calendar calender = Calendar.getInstance();
    final int hour = calender.get(Calendar.HOUR_OF_DAY);
    final int minute = calender.get(Calendar.MINUTE);
    final int year = calender.get(Calendar.YEAR);
    final int month = calender.get(Calendar.MONTH);
    final int day = calender.get(Calendar.DAY_OF_MONTH);
    Boolean isRound = false;
    //Trip tripModel = new Trip();
    Place placeStartPoint;
    Place placeEndPoint;
    FragmentManager f;
    Fragment fragmentB;

    public FragmentAddTrip() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(), apiKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        editTextTripName = view.findViewById(R.id.ediTxt_tripName);
        textViewTripName = view.findViewById(R.id.txtView_tripName);
        textViewDate = view.findViewById(R.id.txtView_date);
        imageButtonDate = view.findViewById(R.id.btn_date);
        textViewTime = view.findViewById(R.id.txtview_time);
        imageButtonTime = view.findViewById(R.id.btn_time);
        editTextStartPoint = view.findViewById(R.id.editTxt_startPoint);
        editTextStartPoint.setFocusable(false);
        editTextEndPoint = view.findViewById(R.id.editTxt_endPoint);
        editTextEndPoint.setFocusable(false);
        textViewTime2 = view.findViewById(R.id.txtView_Time2);
        textViewDate2 = view.findViewById(R.id.txtView_Date2);
        imageButtonDate2 = view.findViewById(R.id.btn_date2);
        imageButtonDate2.setFocusable(false);
        imageButtonTime2 = view.findViewById(R.id.btn_time2);
        imageButtonTime2.setFocusable(false);
        radioButtonOneDirection = view.findViewById(R.id.radioBtn_oneDirection);
        radioButtonRoundTrip = view.findViewById(R.id.radioBtn_roundTrip);
        btnSaveTrip = view.findViewById(R.id.btn_saveTrip);
        btnAddNotes = view.findViewById(R.id.btn_addNotes);
        recyclerView = view.findViewById(R.id.recyclerView);
        notes = new ArrayList<>();
        adapter = new AdapterAddNote(notes, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        constraintLayoutNotes = view.findViewById(R.id.layoutOfNotes);
        constraintLayoutRoundTrip = view.findViewById(R.id.constraintLayoutAddRound);

        Log.i(TAG, "onCreateView: ");

        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                constraintLayoutNotes.setVisibility(View.VISIBLE);
                Log.i(TAG, "onClick:add button ");
                notes.add(new NoteModel("", false));
                Log.i(TAG, notes.toString());
                adapter.notifyDataSetChanged();

/*
                    f=getFragmentManager();
                    getChildFragmentManager();
                    fragmentB = new FragmentAddNotes();
                    f.beginTransaction().add(R.id.fragmentB, fragmentB, "fragment").commit();
*/
            }
        });

        radioButtonOneDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayoutRoundTrip.setVisibility(View.GONE);
                isRound = onRadioButtonClicked(v);
            }
        });
        radioButtonRoundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRound = onRadioButtonClicked(v)) {
                    Log.i(TAG, "onClick: " + isRound);
                    constraintLayoutRoundTrip.setVisibility(View.VISIBLE);


                }
            }
        });
        editTextStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesAutocompletes(AUTOCOMPLETE_REQUEST_CODE_STARTPOINT);
            }
        });
        editTextEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesAutocompletes(AUTOCOMPLETE_REQUEST_CODE_ENDPOINT);
            }
        });

        imageButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderDate(textViewDate, 1);
                textViewTime.setText("");
            }
        });
        imageButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateCorrect)
                    calenderTime(textViewTime, 1);
                else
                    Toast.makeText(getContext(), "Please choose date first", Toast.LENGTH_SHORT).show();

                isFirstTimeSeleceted = true;
            }
        });
        imageButtonDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDateCorrect) {
                    calenderDate(textViewDate2, 2);
                    textViewTime2.setText("");
                }else
                    Toast.makeText(getContext(), "Please choose first trip date first", Toast.LENGTH_SHORT).show();
            }
        });
        imageButtonTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateCorrectRoundTrip && isFirstTimeSeleceted)
                    calenderTime(textViewTime2, 2);
                else
                    Toast.makeText(getContext(), "Please choose Round Trip date first", Toast.LENGTH_SHORT).show();
            }
        });
        btnSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkData(v);
                checkData();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_STARTPOINT) {
            if (resultCode == RESULT_OK) {
                placeStartPoint = Autocomplete.getPlaceFromIntent(data);

                Log.i(TAG, "Place: " + placeStartPoint.getName() + ", " + placeStartPoint.getId());
                if (placeStartPoint.getLatLng() != null) {
                    editTextStartPoint.setText(placeStartPoint.getName());
                    Log.i(TAG, "Place: " + placeStartPoint.getLatLng());
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ENDPOINT) {
            if (resultCode == RESULT_OK) {
                placeEndPoint  = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + placeEndPoint.getName() + ", " + placeEndPoint.getId());
                editTextEndPoint.setText(placeEndPoint.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void placesAutocompletes(int flag) {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .build(getContext());
        startActivityForResult(intent, flag);
    }

    public void calenderDate(TextView textViewDate1, int check) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int nowYear;
                int nowMonth;
                int nowDay;
                month = month + 1;
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Log.i(Constants.LOG_TAG,date);
                if(check == 1) {
                    String[] dateTotal = date.split("-");
                     nowYear = Integer.valueOf(dateTotal[2]);
                     nowMonth = Integer.valueOf(dateTotal[1]);
                     nowDay = Integer.valueOf(dateTotal[0]);
                }
                else{
                    String oneTripDate = textViewDate.getText().toString();
                    String[] oneTripDateSplits = oneTripDate.split("-");
                    nowYear = Integer.valueOf(oneTripDateSplits[2]);
                    nowMonth = Integer.valueOf(oneTripDateSplits[1]);
                    nowDay = Integer.valueOf(oneTripDateSplits[0]);
                }
                if(year == nowYear && month == nowMonth && day== nowDay){
                    if(check==1)
                        isDateToday=true;
                    else
                        isDateTodayRoundTrip=true;
                }else{
                    if(check==1)
                        isDateToday=false;
                    else
                        isDateTodayRoundTrip=false;
                }
                if (year >= nowYear) {
                    if (month >= nowMonth) {
                        if (day >= nowDay) {
                            Toast.makeText(getContext(), "Date is Choosen", Toast.LENGTH_SHORT).show();
                            if (check == 1) {
                                isDateCorrect = true;
                            } else {
                                isDateCorrectRoundTrip = true;
                            }
                            textViewDate1.setText(day + "-" + month + "-" + year);
                        } else {
                            Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                            if (check == 1) {
                                isDateCorrect = false;
                            } else {
                                isDateCorrectRoundTrip = false;
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                        if (check == 1) {
                            isDateCorrect = false;
                        } else {
                            isDateCorrectRoundTrip = false;
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Date is wrong", Toast.LENGTH_SHORT).show();
                    if (check == 1) {
                        isDateCorrect = false;
                    } else {
                        isDateCorrectRoundTrip = false;
                    }
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void calenderTime(TextView textViewTime1, int check) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHours, int selectedMinute) {
                int nowHour;
                int nowMin;
                Log.i(TAG, "hours: " + selectedHours + " minutes: " + selectedMinute);
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm");
                String localTime = date.format(currentLocalTime);
                if(check==1){
                    String[] localTimeSplit = localTime.split(":");
                    nowHour = Integer.valueOf(localTimeSplit[0]);
                    nowMin =  Integer.valueOf(localTimeSplit[1]);
                }
                else{
                    String[] localTimeFirstTrip = textViewTime.getText().toString().split(":");
                    nowHour = Integer.valueOf(localTimeFirstTrip[0]);
                    nowMin = Integer.valueOf(localTimeFirstTrip[1]);
                }
                if (isDateToday || isDateTodayRoundTrip) {
                    if (selectedHours > nowHour) {
                        if (check == 1) {
                            isTimeCorrect = true;
                        } else {
                            isTimeCorrectRoundTrip = true;
                        }
                        textViewTime1.setText(selectedHours + ":" + selectedMinute);
                    } else {
                        if (selectedHours == nowHour) {
                            if (selectedMinute > nowMin) {
                                Toast.makeText(getContext(), "time is correct", Toast.LENGTH_SHORT).show();
                                if (check == 1)
                                    isTimeCorrect = true;
                                else
                                    isTimeCorrectRoundTrip = true;
                                textViewTime1.setText(selectedHours + ":" + selectedMinute);
                            } else {
                                Toast.makeText(getContext(), "time is not 1Correct", Toast.LENGTH_SHORT).show();
                                if (check == 1)
                                    isTimeCorrect = false;
                                else
                                    isTimeCorrectRoundTrip = false;
                            }
                        } else {
                            Toast.makeText(getContext(), "time is not 2Correct", Toast.LENGTH_SHORT).show();
                            if (check == 1)
                                isTimeCorrect = false;
                            else
                                isTimeCorrectRoundTrip = false;
                        }
                    }
                }else{
                    Toast.makeText(getContext(), "DDDD", Toast.LENGTH_SHORT).show();
                    textViewTime1.setText(selectedHours + ":" + selectedMinute);
                }
            }
        }, 12, 0, false);
        timePickerDialog.show();
    }

    public Boolean onRadioButtonClicked(View view) {
        // Is the button now checked?
        //return true if checked is round
        boolean checked = ((RadioButton) view).isChecked();
        Boolean isRound = false;

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioBtn_oneDirection:
                if (checked)
                    Toast.makeText(getContext(), "you choose  one Direction" + isRound, Toast.LENGTH_LONG).show();
                break;
            case R.id.radioBtn_roundTrip:
                if (checked)
                    isRound = true;
                Toast.makeText(getContext(), "you choose  Round Trip" + isRound, Toast.LENGTH_LONG).show();
                break;
        }
        Log.i(TAG, "onRadioButtonClicked: " + ((RadioButton) view).getText());
        return isRound;
    }

//    public void checkData(View v) {
//        Log.i(TAG, "checkData: ");
//        if (!TextUtils.isEmpty(editTextTripName.getText().toString())) {
//            Log.i(TAG, "checkData: name n" + editTextTripName.getText());
//            if (!TextUtils.isEmpty(editTextStartPoint.getText().toString())
//                    && !TextUtils.isEmpty(editTextEndPoint.getText().toString())) {
//                if (!TextUtils.isEmpty(textViewDate.getText().toString()) && !TextUtils.isEmpty(textViewTime.getText().toString())) {
//                    if (adapter.getNotesList() != null) {
//                        tripModel.setNotes(adapter.getNotesList());
//                    }
//                    tripModel.setTripName(editTextTripName.getText().toString());
//                    tripModel.setLocation(new TripModel.Location(place.getLatLng().latitude, place.getLatLng().longitude));
//                    tripModel.setDate(new TripModel.Date(day, month, year));
//                    tripModel.setTime2(new TripModel.Time(hour, minute));
//                    Log.i(TAG, "checkData: trip ");
//                    if (!isRound) {
//                        tripModel.setTripType("one Direction");
//                        tripModel.setDate2(null);
//                        tripModel.setTime2(null);
//                    } else if (!TextUtils.isEm)
//                            && !TextUtils.isEmpty(textViewTime2.getText().toString())) {
//                        tripModel.setTripType("round Trip");
//                        tripModel.setDate2(new TripModel.Date(day, month, year));
//                        tripModel.setTime2(new TripModel.Time(hour, minute));
//                    } else {
//                        textViewDate2.setError("Required");
//                        textViewTime.setError("Required");
//                        Toast.makeText(getContext(), "Please Enter Date and Time", Toast.LENGTH_LONG).show();
//                    }
//                    Log.i(TAG, "checkData: " + tripModel.getTripName().toString() + "/" + tripModel.getLocation());
//                } else {
//                    textViewDate.setError("Required");
//                    textViewTime.setError("Required");
//                    Toast.makeText(getContext(), "Please Enter Date and Time", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                editTextStartPoint.setError("Required");
//                editTextEndPoint.setError("Required");
//                Toast.makeText(getContext(), "Please Enter Start and End Point", Toast.LENGTH_LONG).show();
//            }
//            Log.i(TAG, "checkData: " + tripModel);
//        } else {
//            Toast.makeText(getContext(), "Please Enter Valid data", Toast.LENGTH_LONG).show();
//        }
//    }

    public void checkData(){
        Log.i(TAG, "checkData: ");
        if(!TextUtils.isEmpty(editTextTripName.getText())){
            if(!TextUtils.isEmpty(editTextStartPoint.getText())){
                if(!TextUtils.isEmpty(editTextEndPoint.getText())){
                    if(!TextUtils.isEmpty(textViewDate.getText())){
                        if(!TextUtils.isEmpty(textViewTime.getText())){
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                //create one obj for one direction
                                assert user != null;
                                Trip trip=new Trip(user.getUid(),editTextTripName.getText().toString(),placeStartPoint.getName(),
                                        placeEndPoint.getName(),placeEndPoint.getLatLng().latitude,placeEndPoint.getLatLng().longitude,
                                        textViewDate.getText().toString(),textViewTime.getText().toString(),R.drawable.preview,
                                        "upcomming");
                            Log.i(TAG, "checkData:mmmmmm "+trip.getTripName()+trip.getDate()+trip.getTime()+
                                    trip.getEndPoint()+trip.getStartPoint()+trip.getTripStatus());
                                insertRoom(trip);

                            if(isRound){
                                if(!TextUtils.isEmpty(textViewDate2.getText())){
                                    if(!TextUtils.isEmpty(textViewTime2.getText())){

                                //create two obj
                                Trip tripRound=new Trip(user.getUid(),editTextTripName.getText().toString()+"Round",placeEndPoint.getName(),
                                        placeStartPoint.getName(),placeStartPoint.getLatLng().latitude,placeStartPoint.getLatLng().longitude,
                                        textViewDate2.getText().toString(),textViewTime2.getText().toString(),R.drawable.preview,
                                        "upcomming");
                                insertRoom(tripRound);
                            }else{
                                        editTextStartPoint.setError("Valid Time");
                                        Toast.makeText(getContext(),"Please, Enter Valid Time for round",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    editTextStartPoint.setError("Valid Date");
                                    Toast.makeText(getContext(),"Please, Enter Valid Date for round",Toast.LENGTH_LONG).show();
                                }}
                        }else{
                            editTextStartPoint.setError("Valid Time");
                            Toast.makeText(getContext(),"Please, Enter Valid Time",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        editTextStartPoint.setError("Please Enter Valid Date");
                        Toast.makeText(getContext(),"Please, Enter Valid Date",Toast.LENGTH_LONG).show();
                    }

                }else{
                    editTextStartPoint.setError(" Required End Point");
                    Toast.makeText(getContext(),"Please, Required End Point",Toast.LENGTH_LONG).show();
                }

            }else{
                editTextStartPoint.setError("Please Enter Valid Start Point");
                Toast.makeText(getContext(),"Please, Required Start Point",Toast.LENGTH_LONG).show();
            }
        }else{
            editTextTripName.setError("Required");
            Toast.makeText(getContext(),"Please, Required Trip Name",Toast.LENGTH_LONG).show();

        }
    }

    private void insertRoom(Trip trip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HomeActivity.database.tripDAO().insert(trip);
            }
        }).start();

        Log.i(TAG, "insertRoom: ");

    }

}