package ca.josephroque.bowlingcompanion.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Joseph Roque on 2015-07-19. Manages the pins for controlling the game.
 */
public class PinLayout
        extends LinearLayout
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "PinLayout";

    /** Instance of the touch listener interface. */
    private PinInterceptListener mListener;

    /** Width of edge to ignore touch for navigation drawer. */
    private static final int NAVIGATION_DRAWER_EDGE_WIDTH = 20;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return true;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event)
    {
        if (event.getX() < Math.ceil(NAVIGATION_DRAWER_EDGE_WIDTH
                * getResources().getDisplayMetrics().density))
            event.setAction(MotionEvent.ACTION_CANCEL);

        if (mListener != null)
            mListener.onPinTouch(event);

        return true;
    }

    /**
     * Sets the pin listener interface.
     *
     * @param listener instance of listener
     */
    public void setInterceptListener(PinInterceptListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public void onDetachedFromWindow()
    {
        mListener = null;
        super.onDetachedFromWindow();
    }

    /**
     * Default constructor which passes params to superclass.
     *
     * @param context context
     */
    public PinLayout(Context context)
    {
        super(context);
    }

    /**
     * Default constructor which passes params to superclass.
     *
     * @param context context
     * @param attr attribute set
     */
    public PinLayout(Context context, AttributeSet attr)
    {
        super(context, attr);
    }

    /**
     * Default constructor which passes params to superclass.
     *
     * @param context context
     * @param attr attribute set
     * @param defStyle style
     */
    public PinLayout(Context context, AttributeSet attr, int defStyle)
    {
        super(context, attr, defStyle);
    }

    /**
     * Offers methods to handle user events touching the pins.
     */
    public interface PinInterceptListener
    {

        /**
         * Invoked when the pin container is touched.
         *
         * @param event motion event created
         */
        void onPinTouch(MotionEvent event);
    }
}
