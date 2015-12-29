package ca.josephroque.bowlingcompanion.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Fixes a bug where {@link TextInputLayout} does not display hint before {@link EditText} is focused. Retrieved from
 * https://gist.github.com/ljubisa987/e33cd5597da07172c55d
 */
public class CustomTextInputLayout
        extends TextInputLayout {

    /** To identify output from this class in the Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TextInputLayout";

    /** If hint is set. */
    private boolean mIsHintSet;
    /** Hint of the text input layout. */
    private CharSequence mHint;

    /**
     * Default constructor. Passes parameter to super constructor.
     *
     * @param context context
     */
    public CustomTextInputLayout(Context context) {
        super(context);
    }

    /**
     * Default constructor. Passes parameters to super constructor.
     *
     * @param context context
     * @param attrs attributes
     */
    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            // Since hint will be nullify on EditText once on parent addView, store hint value
            // locally
            mHint = ((EditText) child).getHint();
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mIsHintSet && ViewCompat.isLaidOut(this) && getEditText() != null) {
            // We have to reset the previous hint so that equals check pass
            setHint(null);

            // In case that hint is changed programmatically
            CharSequence currentEditTextHint = getEditText().getHint();
            if (!TextUtils.isEmpty(currentEditTextHint)) {
                mHint = currentEditTextHint;
                getEditText().setHint("");
            }
            setHint(mHint);
            mIsHintSet = true;
        }
    }
}
