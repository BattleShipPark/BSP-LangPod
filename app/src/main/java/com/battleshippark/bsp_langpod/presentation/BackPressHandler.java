package com.battleshippark.bsp_langpod.presentation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.battleshippark.bsp_langpod.Const;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 */

class BackPressHandler {
    private final Animation fadeIn, fadeOut;
    private long lastTime = 0;

    BackPressHandler(Context context) {
        fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
    }

    boolean isClosable() {
        long currentTime = System.currentTimeMillis();
        if (lastTime == 0 || currentTime - lastTime > Const.MAIN_BACKPRESS_DIALOG_DURATION) {
            lastTime = currentTime;
            return false;
        } else {
            return true;
        }
    }

    void show(View view) {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeIn);

        Observable.timer(Const.MAIN_BACKPRESS_DIALOG_DURATION, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(ignore -> {
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    view.startAnimation(fadeOut);
                });
    }
}
