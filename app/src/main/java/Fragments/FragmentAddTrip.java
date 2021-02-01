package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.iti41g1.tripreminder.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import Adapters.AdapterAddNote;
import Models.NoteModel;
import Models.TripModel;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentAddTrip extends Fragment {
    EditText editTextTripName;
    EditText editTextStartPoint;
    EditText editTextEndPoint;
    TextView textViewTripName;
    TextView textViewDate;
    TextView textViewDate2;
    Button   btnAddNotes;
    Button   btnSaveTrip;
    ImageButton imageButtonDate;
    ImageButton imageButtonDate2;
    TextView    textViewTime;
    TextView    textViewTime2;
    ImageButton imageButtonTime;
    ImageButton imageButtonTime2;
    RadioButton radioButtonOneDirection;
    RadioButton radioButtonRoundTrip;
    ConstraintLayout constraintLayoutNotes;
    ConstraintLayout constraintLayoutRoundTrip;
    RecyclerView recyclerView;
    AdapterAddNote adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<NoteModel>notes;

    private static final String TAG = "AddTripFragment";
    private static final  String apiKey="AIzaSyAKXUZsOm7RLbPEAQQxp6TZsU9YWLeh5Pg";
    private static final  int AUTOCOMPLETE_REQUEST_CODE_STARTPOINT=1;
    private static final  int AUTOCOMPLETE_REQUEST_CODE_ENDPOINT=2;
    Calendar calender = Calendar.getInstance();
    final int hour = calender.get(Calendar.HOUR_OF_DAY);
    final int minute = calender.get(Calendar.MINUTE);
    final int year = calender.get(Calendar.YEAR);
    final int month = calender.get(Calendar.MONTH);
    final int day = calender.get(Calendar.DAY_OF_MONTH);
    Boolean isRound=false;
    TripModel tripModel=new TripModel();
    Place place;
    public FragmentAddTrip() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(),apiKey);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_trip, container, false);
        editTextTripName=view.findViewById(R.id.ediTxt_tripName);
        textViewTripName=view.findViewById(R.id.txtView_tripName);
        textViewDate=view.findViewById(R.id.txtView_date);
        imageButtonDate=view.findViewById(R.id.btn_date);
        textViewTime=view.findViewById(R.id.txtview_time);
        imageButtonTime=view.findViewById(R.id.btn_time);
        editTextStartPoint=view.findViewById(R.id.editTxt_startPoint);
        editTextStartPoint.setFocusable(false);
        editTextEndPoint=view.findViewById(R.id.editTxt_endPoint);
        editTextEndPoint.setFocusable(false);
        textViewTime2=view.findViewById(R.id.txtView_Time2);
        textViewDate2=view.findViewById(R.id.txtView_Date2);
        imageButtonDate2=view.findViewById(R.id.btn_date2);
        imageButtonDate2.setFocusable(false);
        imageButtonTime2=view.findViewById(R.id.btn_time2);
        imageButtonTime2.setFocusable(false);
        radioButtonOneDirection=view.findViewById(R.id.radioBtn_oneDirection);
        radioButtonRoundTrip=view.findViewById(R.id.radioBtn_roundTrip);
        btnSaveTrip=view.findViewById(R.id.btn_saveTrip);
        btnAddNotes=view.findViewById(R.id.btn_addNotes);
        recyclerView=view.findViewById(R.id.recyclerView);
        notes=new ArrayList<>();
        adapter=new AdapterAddNote(notes,getContext());
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        constraintLayoutNotes=view.findViewById(R.id.layoutOfNotes);
        constraintLayoutRoundTrip=view.findViewById(R.id.constraintLayoutAddRound);

        Log.i(TAG, "onCreateView: ");

        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              constraintLayoutNotes.setVisibility(View.VISIBLE);
                Log.i(TAG, "onClick:add button ");
                notes.add(new NoteModel("", false));
                Log.i(TAG, notes.toString());
                adapter.notifyDataSetChanged();
            }
        });

        radioButtonOneDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRound=onRadioButtonClicked(v);
            }
        });
        radioButtonRoundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRound=onRadioButtonClicked(v)) {
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
                calenderDate(textViewDate);
            }
        });
        imageButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderTime(textViewTime);
            }
        });
        imageButtonDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderDate(textViewDate2);
            }
        });
        imageButtonTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderTime(textViewTime2);
            }
        });
        btnSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData(v);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_STARTPOINT) {
            if (resultCode == RESULT_OK) {
                 place = Autocomplete.getPlaceFromIntent(data);

                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if(place.getLatLng()!=null) {
                    editTextStartPoint.setText(place.getName());
                    Log.i(TAG, "Place: " + place.getLatLng());
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ENDPOINT) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                editTextEndPoint.setText(place.getName());
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

    private void placesAutocompletes(int flag){
        Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .build(getContext());
        startActivityForResult(intent,flag);
    }
    public boolean isValidDate(String pDateString)  {
        Date date = null;
        try {
            date = new SimpleDateFormat("MM/dd/yyyy").parse(pDateString);
            Log.i(TAG, "isValidDate: "+date);
        } catch (ParseException e) {
            Log.i(TAG, "error");
            e.printStackTrace();
        }
        return new Date().before(date);
    }
    public boolean isValidTime(String pDateString)  {
    //    Calendar calendar = Calendar.getInstance();
        Date time = null;
        try {
            time = new SimpleDateFormat("hh/mm").parse(pDateString);
            Log.i(TAG, "isValidTime: "+time);

        } catch (ParseException e) {
            Log.i(TAG, "isValidTime:error");
            e.printStackTrace();
        }
       return new Date().before(time);
    }
    public void calenderDate(TextView textViewDate){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.i(TAG, "i:" + year + " i1: " + month + " i2: " + day);
                //    trip.setDate(new TripDate(day, month, year));
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, month);
                calender.set(Calendar.DAY_OF_MONTH, day);
                String s = month+1 + "/"+ day + "/" + year;
                Log.i(TAG, "onDateSet: "+s);
                Boolean validDate = isValidDate(s);
                Log.i(TAG, "onCreateView: " + validDate);
                if (validDate==true)
                {
                    textViewDate.setText(day+"/"+(month+1)+"/"+year);
                }else {
                    Toast.makeText(getContext(),"Please enter valid date after today",Toast.LENGTH_LONG).show();
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    public void calenderTime(TextView textViewTime){
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHours, int selectedMinute) {
                Log.i(TAG, "hours: " + selectedHours + " minutes: " + selectedMinute);
                // trip.setTime(new TripTime(selectedHours, selectedMinute));
                calender.set(Calendar.HOUR_OF_DAY, selectedHours);
                calender.set(Calendar.MINUTE, selectedMinute);
                calender.set(Calendar.SECOND, 0);
                String s=selectedHours+"/"+selectedMinute;
                textViewTime.setText(selectedHours+"/"+selectedMinute);
                /*
                Boolean validTime=isValidTime(s);
                Log.i(TAG, "onCreateView: " + validTime);
                if (validTime==true)
                {
                    textViewTime.setText(hour+"/"+minute);
                }else {
                    Toast.makeText(getContext(),"Please enter valid Time after current time",Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "onTimeSet: "+s);
**/
            }

        }, hour, minute, false);
        timePickerDialog.show();
    }
    public Boolean onRadioButtonClicked(View view){
        // Is the button now checked?
        //return true if checked is round
        boolean checked = ((RadioButton) view).isChecked();
        Boolean isRound=false;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioBtn_oneDirection:
                if (checked)
                    Toast.makeText(getContext(),"you choose  one Direction"+isRound,Toast.LENGTH_LONG).show();
                    break;
            case R.id.radioBtn_roundTrip:
                if (checked)
                    isRound=true;
                    Toast.makeText(getContext(),"you choose  Round Trip"+isRound,Toast.LENGTH_LONG).show();
                    break;
        }
        Log.i(TAG, "onRadioButtonClicked: "+((RadioButton) view).getText());
        return isRound;
    }
    public  void checkData(View v){
        Log.i(TAG, "checkData: ");
        if(!TextUtils.isEmpty( editTextTripName.getText().toString())){
            Log.i(TAG, "checkData: name n"+editTextTripName.getText());
            if(!TextUtils.isEmpty( editTextStartPoint.getText().toString())
                    &&!TextUtils.isEmpty( editTextEndPoint.getText().toString())){
                if(!TextUtils.isEmpty( textViewDate.getText().toString())&&!TextUtils.isEmpty( textViewTime.getText().toString())){
                    if(adapter.getNotesList()!=null){
                        tripModel.setNotes(adapter.getNotesList());
                    }
                    tripModel.setTripName(editTextTripName.getText().toString());
                    tripModel.setLocation(new TripModel.Location(place.getLatLng().latitude,place.getLatLng().longitude));
                    tripModel.setDate(new TripModel.Date(day,month,year));
                    tripModel.setTime2(new TripModel.Time(hour,minute));
                    Log.i(TAG, "checkData: trip ");
                     if(!isRound){
                     tripModel.setTripType("one Direction");
                     tripModel.setDate2(null);
                     tripModel.setTime2(null);
                     }else if(!TextUtils.isEmpty( textViewDate2.getText().toString())
                             &&!TextUtils.isEmpty( textViewTime2.getText().toString())){
                         tripModel.setTripType("round Trip");
                         tripModel.setDate2(new TripModel.Date(day,month,year));
                         tripModel.setTime2(new TripModel.Time(hour,minute));
                     }else {
                         textViewDate2.setError("Required");
                         textViewTime.setError("Required");
                         Toast.makeText(getContext(),"Please Enter Date and Time",Toast.LENGTH_LONG).show();
                     }
                    Log.i(TAG, "checkData: "+tripModel.getTripName().toString()+"/"+tripModel.getLocation());
                }else{
                    textViewDate.setError("Required");
                    textViewTime.setError("Required");
                    Toast.makeText(getContext(),"Please Enter Date and Time",Toast.LENGTH_LONG).show();
                }
            }else {
                editTextStartPoint.setError("Required");
                editTextEndPoint.setError("Required");
                Toast.makeText(getContext(),"Please Enter Start and End Point",Toast.LENGTH_LONG).show();
            }
            Log.i(TAG, "checkData: "+tripModel);
        }else {
            Toast.makeText(getContext(),"Please Enter Valid data",Toast.LENGTH_LONG).show();
        }
    }

}