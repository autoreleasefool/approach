package ca.josephroque.bowlingcompanion.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 2015-09-22. A dialog for selecting the value which games or series must be greater than to
 * be highlighted.
 */
public class HighlightsDialog
        extends DialogPreference {

    /** Minimum value the dialog can return. */
    private int mMinimumValue;
    /** Maximum value the dialog can return. */
    private int mMaximumValue;
    /**
     * Values in the number picker will start at {@code mMinimumValue} and increment by this value until it is equal to
     * or greater than {@code mMaximumValue}.
     */
    private int mIncrementBy;

    /** Instance of a number picker. */
    private NumberPicker mNumberPicker;
    /** Value of the number picker. */
    private int mValue;

    /**
     * Initializes the dialog using the parameters provided as attributes.
     *
     * @param context current context
     * @param attrs attributes
     */
    public HighlightsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Highlights,
                0, 0);

        try {
            mMaximumValue = a.getInt(R.styleable.Highlights_maximumValue, -1);
            mMinimumValue = a.getInt(R.styleable.Highlights_maximumValue, 0);
            mIncrementBy = a.getInt(R.styleable.Highlights_incrementBy, 1);
        } finally {
            a.recycle();
        }
    }

    /**
     * Initializes the dialog using the parameters provided as attributes.
     *
     * @param context current context
     * @param attrs attributes
     * @param defStyleAttr style
     */
    public HighlightsDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Highlights,
                0, 0);

        try {
            mMaximumValue = a.getInt(R.styleable.Highlights_maximumValue, -1);
            mMinimumValue = a.getInt(R.styleable.Highlights_maximumValue, 0);
            mIncrementBy = a.getInt(R.styleable.Highlights_incrementBy, 1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mNumberPicker = new NumberPicker(getContext());
        mNumberPicker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mNumberPicker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        List<String> displayValues = new ArrayList<>();
        for (int i = mMinimumValue; i <= mMaximumValue; i += mIncrementBy) {
            displayValues.add(String.valueOf(i));
        }

        String[] displayValuesArr = new String[displayValues.size()];
        displayValues.toArray(displayValuesArr);
        mNumberPicker.setMaxValue(displayValues.size() - 1);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setDisplayedValues(displayValuesArr);
        mNumberPicker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int newValue = mNumberPicker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mMinimumValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue
                ? getPersistedInt(mMinimumValue)
                : (Integer) defaultValue);
    }

    /**
     * Sets the value of the number picker.
     *
     * @param value new value
     */
    public void setValue(int value) {
        this.mValue = value;
        persistInt(this.mValue);
    }

    /**
     * Gets the value of the number picker.
     *
     * @return currently selected value
     */
    public int getValue() {
        return this.mValue;
    }
}
