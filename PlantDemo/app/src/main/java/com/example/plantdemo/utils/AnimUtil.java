package com.example.plantdemo.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

/**
 * Created by MXL on 2020/12/18
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class AnimUtil {

    /**
     * 实现图片翻转
     * @param oldView
     * @param newView
     * @param time
     */
    public static void FlipAnimatorXViewShow(final View oldView, final View newView, final long time) {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(oldView, "rotationX", 0, 90);
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(newView, "rotationX", -90, 0);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));

        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                animator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(time).start();
    }
    /**
     * 实现按钮翻转效果
     * @param view
     * @param time
     */
    public static void FlipAnimatorXViewShow(final ImageButton view, final long time, int icon) {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "rotationX", 0, 90);
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "rotationX", -90, 0);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));

        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
             //   oldView.setVisibility(View.GONE);
               // view.setBackgroundResource(icon);
                view.setImageResource(icon);
                animator2.setDuration(time).start();
               // newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(time).start();
    }
}