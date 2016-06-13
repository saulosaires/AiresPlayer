package com.airesplayer;

import android.animation.Animator;
import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by saulo on 23/05/2016.
 */
public class MainActivity2 extends AppCompatActivity implements OnFragmentTouched {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void addFragment(View v) {
        int randomColor =
                Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        Fragment fragment = CircularRevealingFragment.newInstance(20, 20, randomColor);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }

    @Override
    public void onFragmentTouched(Fragment fragment, float x, float y) {
        if (fragment instanceof CircularRevealingFragment) {

        }
            final CircularRevealingFragment theFragment = (CircularRevealingFragment) fragment;

            Animator unreveal = theFragment.prepareUnrevealAnimator(x, y);

            unreveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // remove the fragment only when the animation finishes
                    getSupportFragmentManager().beginTransaction().remove(theFragment).commit();
                    //to prevent flashing the fragment before removing it, execute pending transactions inmediately
                    getSupportFragmentManager().executePendingTransactions();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            unreveal.start();
        }
    }