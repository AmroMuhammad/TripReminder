package com.iti41g1.tripreminder.controller.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
    public ViewPager viewPager;
    public ViewPagerAdaptor adaptor;
    private TabLayout tabLayout;
    private UpcomingFragment upcomingFragment;
    private ProfileFragment profileFragment;
    private HistoryFragment historyFragment;
    private List<Fragment> fragments;
    private List<String> fragmentTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fireBaseUseerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        runBackgroundPermissions();


    }

    @Override
    protected void onResume() {
        super.onResume();
        new registerAlarm().execute();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void checkDrawOverAppsPermissionsDialog() {
        new AlertDialog.Builder(this).setTitle("Permission request").setCancelable(false).setMessage("Allow Draw Over Apps Permission to be able to use application probably")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawOverAppPermission();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                errorWarningForNotGivingDrawOverAppsPermissions();
            }
        }).show();
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

    private void errorWarningForNotGivingDrawOverAppsPermissions() {
        new AlertDialog.Builder(this).setTitle("Warning").setCancelable(false).setMessage("Unfortunately the display over other apps permission" +
                " is not granted so the application might not behave properly \nTo enable this permission kindly restart the application")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
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
}