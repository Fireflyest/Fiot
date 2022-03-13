package com.fireflyest.fiot.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationUtils {

    public static final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static final OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

    private AnimationUtils(){
    }

    /**
     * 点击回弹效果
     * @param view 所点击的控件
     */
    public static void click(View view){
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .scaleY(0.88F)
                .scaleX(0.88F)
                .setDuration(120)
                .setInterpolator(new OvershootInterpolator())
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
                                .setDuration(220)
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

    public static void alpha(View view, float alpha){
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .alpha(alpha)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setAlpha(0.7F);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(0.7F);
                    }
                });
    }

    public static void hide(View view){
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .alpha(0)
                .translationY(-500)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void show(View view){
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(0);
        view.animate()
                .setInterpolator(decelerateInterpolator)
                .alpha(1)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

}
