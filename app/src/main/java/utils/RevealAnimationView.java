package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

/**
 * Created on 14-03-2020.
 */
public class RevealAnimationView {

    public void showWithCircularRevealAnimation(View view) {
        int cx = view.getWidth();
        int cy = 0;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        Log.e("finalRadius:", "" + finalRadius);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(500);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void hideWithCircularRevealAnimation(final View view, View viewExtra, boolean showAfterHide) {
        int cx = view.getWidth();
        int cy = 0;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        anim.setDuration(100);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                if (viewExtra != null)
                    viewExtra.setVisibility(View.GONE);

                if (showAfterHide) {
                    if (viewExtra != null)
                        viewExtra.setVisibility(View.VISIBLE);
                    showWithCircularRevealAnimation(view);
                }

            }
        });

        // start the animation
        anim.start();
    }
}
