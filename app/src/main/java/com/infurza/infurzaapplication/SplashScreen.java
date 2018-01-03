package com.infurza.infurzaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static android.view.animation.Animation.AnimationListener;

public class SplashScreen extends AppCompatActivity implements AnimationListener {

    ImageView imageIcon;
    ImageView imageIcon1;
    ImageView imageIcon2;
    public int time=5000;



    // Animation
    Animation animMoveToTop;
    Animation animMoveToTop1;
    Animation animMoveToTop2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageIcon2 = (ImageView) findViewById(R.id.icon2);
        imageIcon1 = (ImageView) findViewById(R.id.icon1);
        imageIcon = (ImageView) findViewById(R.id.icon);


        // load the animation
        animMoveToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);


        // set animation listener
        animMoveToTop.setAnimationListener(this);

        imageIcon.setVisibility(View.VISIBLE);

        // start the animation
        imageIcon.startAnimation(animMoveToTop);

        animMoveToTop1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move1);


        // set animation listener
        animMoveToTop1.setAnimationListener(this);

        imageIcon1.setVisibility(View.VISIBLE);


        // start the animation
        imageIcon1.startAnimation(animMoveToTop1);

        animMoveToTop2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move2);


        // set animation listener
        animMoveToTop2.setAnimationListener(this);

        imageIcon2.setVisibility(View.VISIBLE);


        // start the animation
        imageIcon2.startAnimation(animMoveToTop2);




    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animMoveToTop2) {

        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
        startActivity(intent);




    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}






