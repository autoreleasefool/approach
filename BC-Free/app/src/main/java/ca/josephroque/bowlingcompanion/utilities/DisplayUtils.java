package ca.josephroque.bowlingcompanion.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Joseph Roque on 2015-07-24. Constants and methods for changes made to the UI.
 */
public final class DisplayUtils {

    /** To identify output from this class in the Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "DisplayUtils";

    /** Point relative to center of objects for animations. */
    public static final float ANIMATION_CENTER_PIVOT = 0.5f;
    /** Alpha value for black icons. */
    public static final int BLACK_ICON_ALPHA = 0x8A;
    /** Alpha value for secondary black text. */
    public static final float BLACK_SECONDARY_TEXT_ALPHA = 0.54f;
    /** Constant for black color. */
    public static final int COLOR_BLACK = 0xff000000;
    /** Constant for white color. */
    public static final int COLOR_WHITE = 0xffffffff;

    /**
     * Default private constructor.
     */
    private DisplayUtils() {
        // does nothing
    }


    /**
     * Sets the primary and ripple colors of a {@link android.support.design.widget.FloatingActionButton}.
     *
     * @param fab floating action button to adjust
     * @param primaryColor primary color of action button
     * @param rippleColor color of action button on press
     */
    public static void setFloatingActionButtonColors(FloatingActionButton fab,
                                                     int primaryColor,
                                                     int rippleColor) {
        int[][] states = {
                {android.R.attr.state_enabled},
                {android.R.attr.state_pressed},
        };

        int[] colors = {
                primaryColor,
                rippleColor,
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        fab.setBackgroundTintList(colorStateList);
    }

    /**
     * Applies a workaround to fix {@link android.support.design.widget.FloatingActionButton} margins pre-lollipop.
     *
     * @param resources to get screen density
     * @param fab floating action button to fix
     */
    public static void fixFloatingActionButtonMargins(Resources resources, FloatingActionButton fab) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            final int underLollipopMargin = 8;
            final float scale = resources.getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams p =
                    (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            p.setMargins(0, 0, DataFormatter.getPixelsFromDP(scale, underLollipopMargin), 0);
            fab.setLayoutParams(p);
        }
    }

    /**
     * Uses a method appropriate to the SDK version to create a drawable from a drawable id.
     *
     * @param res to create drawable
     * @param drawableId id of drawable
     * @return drawable which represents {@code drawableId}
     */
    @SuppressWarnings("deprecation")    //Uses newer APIs when available
    public static Drawable getDrawable(Resources res, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return res.getDrawable(drawableId, null);
        else
            return res.getDrawable(drawableId);
    }

    /**
     * Gets a color from the resources using the color's id.
     *
     * @param resources to get color
     * @param id id of color
     * @return integer representation of color
     */
    @SuppressWarnings("deprecation") // uses newer APIs where available
    public static int getColorResource(Resources resources, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return resources.getColor(id, null);
        else
            return resources.getColor(id);
    }

    /**
     * Hides the keyboard in an activity.
     *
     * @param activity current activity
     */
    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
