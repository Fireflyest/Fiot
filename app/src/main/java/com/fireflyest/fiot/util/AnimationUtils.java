package com.fireflyest.fiot.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationUtils {

    public static final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private AnimationUtils(){
    }

    /**
     * 点击回弹效果
     * @param view 所点击的控件
     */
    public static void click(View view){
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .scaleY(0.94F)
                .scaleX(0.94F)
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setScaleX(1);
                        view.setScaleY(1);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                                .setInterpolator(decelerateInterpolator)
                                .scaleX(1.02F)
                                .scaleY(1.02F)
                                .setDuration(100)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        view.setScaleX(1);
                                        view.setScaleY(1);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        view.setScaleX(1);
                                        view.setScaleY(1);
                                    }
                                }).start();
                    }
                }).start();
    }

    public static void down(View view){
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .scaleY(0.88F)
                .scaleX(0.88F)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setScaleX(1);
                        view.setScaleY(1);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setScaleX(1);
                        view.setScaleY(1);
                    }
                });
    }

}
