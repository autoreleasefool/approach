package ca.josephroque.bowlingcompanion.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.lang.reflect.Field;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;

/**
 * Created by josephroque on 15-03-29.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class ChangeDateDialog extends DialogFragment
    implements DatePickerDialog.OnDateSetListener
{

    private DatePickerDialog mDatePicker;
    private ChangeDateDialogListener mChangeDateListener;
    private long mSeriesId;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int[] date;
        if (savedInstanceState == null)
        {
            String dateOfSeries = getArguments().getString(Constants.EXTRA_NAME_SERIES);
            date = DataFormatter.prettyCompactToFormattedDate(dateOfSeries);
            date[0] -= 1;
            mSeriesId = getArguments().getLong(Constants.EXTRA_ID_SERIES, -1);
        }
        else
        {
            date = savedInstanceState.getIntArray(Constants.EXTRA_NAME_SERIES);
            mSeriesId = savedInstanceState.getLong(Constants.EXTRA_ID_SERIES, -1);
        }

        mDatePicker = new DatePickerDialog(getActivity(), this, date[2], date[0], date[1]);
        return mDatePicker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        mChangeDateListener.onChangeDate(mSeriesId, year, month, day);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        DatePicker datePicker;
        try
        {
            Field datePickerField = mDatePicker.getClass().getField("mDatePicker");
            datePickerField.setAccessible(true);
            datePicker = (DatePicker)datePickerField.get(mDatePicker);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Changing series date: " + ex.getMessage());
        }

        int[] currentDate = new int[3];
        currentDate[0] = datePicker.getMonth();
        currentDate[1] = datePicker.getDayOfMonth();
        currentDate[2] = datePicker.getYear();
        outState.putIntArray(Constants.EXTRA_NAME_SERIES, currentDate);
        outState.putLong(Constants.EXTRA_ID_SERIES, mSeriesId);
    }

    public static ChangeDateDialog newInstance(ChangeDateDialogListener listener, String seriesDate, long seriesId)
    {
        ChangeDateDialog dialog = new ChangeDateDialog();
        dialog.mChangeDateListener = listener;
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_NAME_SERIES, seriesDate);
        args.putLong(Constants.EXTRA_ID_SERIES, seriesId);
        dialog.setArguments(args);
        return dialog;
    }

    public static interface ChangeDateDialogListener
    {
        public void onChangeDate(long seriesId, int year, int month, int day);
    }
}
