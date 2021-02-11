package com.iti41g1.tripreminder.controller.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.iti41g1.tripreminder.Adapters.ViewPagerAdaptor;
import com.iti41g1.tripreminder.Models.AlarmReceiver;
import com.iti41g1.tripreminder.Models.Constants;
import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.Fragments.HistoryFragment;
import com.iti41g1.tripreminder.controller.Fragments.ProfileFragment;
import com.iti41g1.tripreminder.controller.Fragments.UpcomingFragment;
import com.iti41g1.tripreminder.database.Trip;
import com.iti41g1.tripreminder.database.TripDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static TripDatabase database;
    public static String fireBaseUseerId;
    public static String fireBaseUserName;
    public static String fireBaseEmail;
    public static Uri fireBaseUserPhotoUri;
    public ViewPager viewPager;
    public ViewPagerAdaptor adaptor;
    private TabLayout tabLayout;
    private UpcomingFragment upcomingFragment;
    private ProfileFragment profileFragment;
    private HistoryFragment historyFragment;
    private List<Fragment> fragments;
    private List<String> fragmentTitles;
    private int[]tabIcons={
            R.drawable.ic_baseline_upcoming_24,
            R.drawable.ic_baseline_history_24,
            R.drawable.ic_baseline_person_profile_24
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Tripinder");
        getSupportActionBar().setLogo(R.drawable.ic_travel_bag);
        fireBaseUseerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fireBaseUserName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        fireBaseEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        fireBaseUserPhotoUri=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        //initalize DB
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
        //inflating views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        //initializing fragments
        upcomingFragment = new UpcomingFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();

        //initializing viewPager
        tabLayout.setupWithViewPager(viewPager);
        fragmentsinit();
        fragmentTitlesinit();
//        setupTabIcons();
        adaptor = new ViewPagerAdaptor(getSupportFragmentManager(), 0, fragments, fragmentTitles);
        viewPager.setAdapter(adaptor);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!Settings.canDrawOverlays(this)) {
            checkDrawOverAppsPermissionsDialog();
        }
        //runBackgroundPermissions();
        viewPager.getAdapter().notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new registerAlarm().execute();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void checkDrawOverAppsPermissionsDialog(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_permission_dialog,(ConstraintLayout) findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Constants.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Allow Draw Over Apps Permission to be able to use application probably");
        ((Button)view.findViewById(R.id.btnCancel)).setText(Constants.PER_DIALOG_CANCEL);
        ((Button)view.findViewById(R.id.btnOk)).setText(Constants.PER_DIALOG_CONFIRM);
        ((ImageView)view.findViewById(R.id.imgTitle)).setImageResource(R.drawable.ic_baseline_warning_24);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorWarningForNotGivingDrawOverAppsPermissions();
                alertDialog.dismiss();
            }
        });


        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawOverAppPermission();
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }



    // to run broadcast in API == 30
    // add intent.flag(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) in receiver to run app also if app is killed for API >= Marshmellow
    public void drawOverAppPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 80);
        }
    }

    private void errorWarningForNotGivingDrawOverAppsPermissions(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_warning,(ConstraintLayout) findViewById(R.id.dialogLayoutContainer));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.textTitle)).setText(Constants.APP_NAME);
        ((TextView)view.findViewById(R.id.textMessage)).setText("Unfortunately the display over other apps permission" +
                " is not granted so the application might not behave properly \nTo enable this permission kindly restart the application");
        ((Button)view.findViewById(R.id.btnOk)).setText(Constants.PER_DIALOG_OK);
        ((ImageView)view.findViewById(R.id.imgTitle)).setImageResource(R.drawable.ic_baseline_warning_24);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();


        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    // to run broadcast in API equals to marshmellow (API 26) and add intent.flag(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) in receiver to run app also
    // if app is killed
    public void runBackgroundPermissions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(intent);
            } else if (Build.BRAND.equalsIgnoreCase("Honor") || Build.BRAND.equalsIgnoreCase("HUAWEI")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                startActivity(intent);
            }
        }
    }

    private void fragmentTitlesinit() {
        fragmentTitles = new ArrayList<>();
        fragmentTitles.add("Upcoming");
        fragmentTitles.add("History");
        fragmentTitles.add("Profile");
    }

    private void fragmentsinit() {
        fragments = new ArrayList<>();
        fragments.add(upcomingFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
    }

    public void initAlarm(Trip trip) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra(Constants.TRIP_NAME, trip.getTripName());
        notifyIntent.putExtra(Constants.TRIP_ID, trip.getId());
        notifyIntent.putExtra(Constants.TRIP_USER_ID, trip.getUserID());
        notifyIntent.putExtra(Constants.TRIP_LATITUDE, trip.getEndPointLat());
        notifyIntent.putExtra(Constants.TRIP_LONGITUDE, trip.getEndPointLong());

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, trip.getId(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trip.getCalendar(), notifyPendingIntent);
    }

    private class registerAlarm extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return HomeActivity.database.tripDAO().selectUpcomingTrip(HomeActivity.fireBaseUseerId, "upcoming");
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).getTripStatus().equals("upcoming")) {
                    if (Calendar.getInstance().getTimeInMillis() > trips.get(i).getCalendar()) {
                        int finalI = i;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HomeActivity.database.tripDAO().updateTripStatus(HomeActivity.fireBaseUseerId,trips.get(finalI).getId(),Constants.MISSED_TRIP_STATUS);
                            }
                        }).start();
                    } else {
                        initAlarm(trips.get(i));
                    }
                }
            }
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewPager.getAdapter().notifyDataSetChanged();
    }
}