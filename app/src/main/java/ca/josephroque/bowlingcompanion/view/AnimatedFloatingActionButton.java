package ca.josephroque.bowlingcompanion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * A {@link android.support.design.widget.FloatingActionButton} which provides methods to animate changing the drawable
 * and background colors.
 */
public class AnimatedFloatingActionButton
        extends FloatingActionButton {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "AnimatedFab";

    /** Current icon of the floating action button. */
    private int mCurrentFabIcon = 0;
    /** Indicates if the floating action button is being animated. */
    private boolean mFabIsAnimating = false;

    /**
     * Default constructor which only passes parameters to super constructor.
     *
     * @param context context the view is running in
     */
    public AnimatedFloatingActionButton(Context context) {
        super(context);
    }

    /**
     * Default constructor which only passes parameters to super constructor.
     *
     * @param context context the view is running in
     * @param attrs attributes of the XML tag inflating the view
     */
    public AnimatedFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Default constructor which only passes parameters to super constructor.
     *
     * @param context context the view is running in
     * @param attrs attributes of the XML tag inflating the view
     * @param defStyle attribute in the current theme that contains a reference to a style resource that supplies
     * default values for the view
     */
    public AnimatedFloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // Overrides touch handler so that on click listener is not invoked if fab is animating
        return !mFabIsAnimating && super.onTouchEvent(event);
    }

    /**
     * Sets the icon and colors of the floating action button with animation.
     *
     * @param drawableId id of the drawable for the floating action button
     */
    public void animateIconChanges(final int drawableId) {
        if (drawableId != mCurrentFabIcon || drawableId == 0) {
            if (mCurrentFabIcon == 0 && drawableId != 0)
                growFloatingActionButton(drawableId);
            else
                shrinkFloatingActionButton(drawableId);
        }
    }

    /**
     * Shrinks the floating action button.
     *
     * @param drawableId new drawable to set if fab grows again
     */
    private void shrinkFloatingActionButton(final int drawableId) {
        if (getVisibility() == View.GONE) {
            mCurrentFabIcon = drawableId;
            return;
        }

        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        ScaleAnimation shrink = new ScaleAnimation(1.0f,
                0f,
                1.0f,
                0f,
                Animation.RELATIVE_TO_SELF,
                DisplayUtils.ANIMATION_CENTER_PIVOT,
                Animation.RELATIVE_TO_SELF,
                DisplayUtils.ANIMATION_CENTER_PIVOT);
        shrink.setDuration((mCurrentFabIcon == 0)
                ? 1
                : shortAnimTime);
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFabIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFabIsAnimating = false;
                growFloatingActionButton(drawableId);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // does nothing
            }
        });

        startAnimation(shrink);
    }

    /**
     * Grows the floating action button.
     *
     * @param drawableId new drawable to set
     */
    @SuppressLint("NewApi") // Lint confuses FloatingActionButton#setBackgroundTintList for new API
    // when it is available in the support design library
    private void growFloatingActionButton(final int drawableId) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mCurrentFabIcon = drawableId;
        if (mCurrentFabIcon != 0)
            setVisibility(View.VISIBLE);
        else {
            setVisibility(View.GONE);
            return;
        }

        setIcon(mCurrentFabIcon);

        ScaleAnimation grow = new ScaleAnimation(0f,
                1.0f,
                0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                DisplayUtils.ANIMATION_CENTER_PIVOT,
                Animation.RELATIVE_TO_SELF,
                DisplayUtils.ANIMATION_CENTER_PIVOT);
        grow.setDuration(shortAnimTime);
        grow.setInterpolator(new OvershootInterpolator());
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFabIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFabIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // does nothing
            }
        });
        startAnimation(grow);
    }

    /**
     * Sets a new icon for the floating action button.
     *
     * @param drawableId id of the icon drawable
     */
    private void setIcon(int drawableId) {
        setImageResource(drawableId);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.mutate();
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        }
    }
}
