package com.iti41g1.tripreminder.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.Models.Constants;


public class SplashActivity extends AppCompatActivity {
    TextView logoPart1_txt;
    TextView logoPart2_txt;
    TextView logoPart3_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        logoPart1_txt=findViewById(R.id.part1_txt);
        logoPart2_txt=findViewById(R.id.part2_txt);
        logoPart3_txt=findViewById(R.id.part3_txt);
        logoPart1_txt.setAnimation(AnimationUtils.loadAnimation(this, R.anim.left_to_right_anim));
        logoPart2_txt.setAnimation(AnimationUtils.loadAnimation(this, R.anim.right_to_left_anim));
        logoPart3_txt.setAnimation(AnimationUtils.loadAnimation(this, R.anim.down_to_up_anim));
        //Animation anim = AnimationUtils.loadAnimation(this, R.anim.left_to_right_anim);
        //logoPart1_txt.setAnimation( anim );
       // logoPart1_txt.startAnimation(anim);

       // Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.down_to_up_anim);
       // logoPart2_txt.startAnimation(anim2);

       // Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.right_to_left_anim);
       // logoPart1_txt.startAnimation(anim3);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.activity_splash);
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,
                        LoginActivity.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, Constants.SPLASH_SCREEN_TIME_OUT);
    }
}