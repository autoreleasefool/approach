package ca.josephroque.bowlingcompanion.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.data.ConvertValue;

/**
 * Created by josephroque on 15-03-07.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class ChangeDateDialog extends DialogFragment
    implements DatePickerDialog.OnDateSetListener
{
    private DatePickerDialog mDatePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int[] date;
        if (savedInstanceState == null)
        {
            String dateOfSeries = getArguments().getString(Constants.EXTRA_NAME_SERIES);
            date = ConvertValue.prettyCompactToFormattedDate(dateOfSeries);
        }
        else
        {
            date = savedInstanceState.getIntArray(Constants.EXTRA_NAME_SERIES);
        }
        mDatePicker = new DatePickerDialog(getActivity(), this, date[2], date[0], date[1]);
        return mDatePicker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        SeriesActivity seriesActivity = (SeriesActivity)getActivity();
        seriesActivity.seriesDateChanged(
                getArguments().getLong(Constants.EXTRA_ID_SERIES),
                year,
                month,
                day);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        int[] currentDate = new int[3];
        DatePicker datePicker = mDatePicker.getDatePicker();
        currentDate[0] = datePicker.getMonth();
        currentDate[1] = datePicker.getDayOfMonth();
        currentDate[2] = datePicker.getYear();
        outState.putIntArray(Constants.EXTRA_NAME_SERIES, currentDate);
    }
}
