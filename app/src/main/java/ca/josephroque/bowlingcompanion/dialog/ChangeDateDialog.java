package ca.josephroque.bowlingcompanion.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.lang.reflect.Field;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.bowling.Series;
import ca.josephroque.bowlingcompanion.utilities.DateUtils;

/**
 * Created by Joseph Roque on 15-03-29. Provides a dialog and callback interface {@link
 * ChangeDateDialog.ChangeDateDialogListener} for the user to change the date associated with a series
 */
public class ChangeDateDialog
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "ChangeDateDialog";

    /** Number of components which make up a date. */
    private static final byte TOTAL_DATE_COMPONENTS = 3;

    /** Dialog which allows user to select a date from a calendar. */
    private DatePickerDialog mDatePicker;
    /** Callback listener for when user selects a date. */
    private ChangeDateDialogListener mChangeDateListener;

    /** Series being edited. */
    private Series mSeries;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int[] date;
        if (savedInstanceState == null) {
            mSeries = getArguments().getParcelable(Constants.EXTRA_SERIES);
            assert mSeries != null;
            date = DateUtils.prettyCompactToFormattedDate(mSeries.getSeriesDate());
            date[0] -= 1; //Must subtract one because method returns 1-12 for month, need 0-11
        } else {
            mSeries = savedInstanceState.getParcelable(Constants.EXTRA_SERIES);
            date = savedInstanceState.getIntArray(Constants.EXTRA_NAME_SERIES);
        }

        if (date != null && date.length == TOTAL_DATE_COMPONENTS)
            mDatePicker = new DatePickerDialog(getActivity(), this, date[2], date[0], date[1]);
        else
            throw new IllegalStateException("must instantiate date with 3 values: year, month, day");
        return mDatePicker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mChangeDateListener.onChangeDate(mSeries, year, month, day);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        DatePicker datePicker;
        try {
            Field datePickerField = mDatePicker.getClass().getField("mDatePicker");
            datePickerField.setAccessible(true);
            datePicker = (DatePicker) datePickerField.get(mDatePicker);
        } catch (Exception ex) {
            throw new RuntimeException("Changing series date: " + ex.getMessage());
        }

        int[] currentDate = new int[TOTAL_DATE_COMPONENTS];
        currentDate[0] = datePicker.getMonth();
        currentDate[1] = datePicker.getDayOfMonth();
        currentDate[2] = datePicker.getYear();
        outState.putIntArray(Constants.EXTRA_NAME_SERIES, currentDate);
        outState.putParcelable(Constants.EXTRA_SERIES, mSeries);
    }

    /**
     * Returns a new instance of this dialog fragment with the listener assigned and arguments set.
     *
     * @param listener callback listener for user events
     * @param series identifies the series to be changed
     * @return a new instance ChangeDateDialog
     */
    public static ChangeDateDialog newInstance(ChangeDateDialogListener listener,
                                               Series series) {
        ChangeDateDialog dialog = new ChangeDateDialog();
        dialog.mChangeDateListener = listener;
        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_SERIES, series);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Callback listener for user events.
     */
    public interface ChangeDateDialogListener {

        /**
         * Called when the user selects a date to set.
         *
         * @param series series to change
         * @param year year to change date to
         * @param month month to change date to
         * @param day day of the month to change date to
         */
        void onChangeDate(Series series, int year, int month, int day);
    }
}
