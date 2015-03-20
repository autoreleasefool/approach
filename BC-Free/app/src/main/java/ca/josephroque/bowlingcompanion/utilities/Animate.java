package ca.josephroque.bowlingcompanion.utilities;

import android.os.Build;
import android.view.View;

import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-20.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.utilities
 * in project Bowling Companion
 */
public class Animate
{

    public static void startSupportFadeInAnimation(View view, AnimatorListenerAdapter listener)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
        {
            ViewHelper.setAlpha(view, 0f);
            view.setVisibility(View.VISIBLE);
            ViewPropertyAnimator.animate(view)
                    .alpha(1f)
                    .setDuration(Theme.getShortAnimationDuration())
                    .setListener(listener);
        }
    }

    public static void startSupportFadeOutAnimation(View view, AnimatorListenerAdapter listener)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
        {
            ViewPropertyAnimator.animate(view)
                    .alpha(0f)
                    .setDuration(Theme.getShortAnimationDuration())
                    .setListener(listener);
        }
    }

    public static void startFadeInAnimation(View view, android.animation.AnimatorListenerAdapter listener)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
        {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate()
                    .alpha(1f)
                    .setDuration(Theme.getShortAnimationDuration())
                    .setListener(listener);
        }
    }

    public static void startFadeOutAnimation(View view, android.animation.AnimatorListenerAdapter listener)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
        {
            view.animate()
                    .alpha(0f)
                    .setDuration(Theme.getShortAnimationDuration())
                    .setListener(listener);
        }
    }
}
