package ca.josephroque.bowlingcompanion.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String dateOfSeries = getArguments().getString(Constants.EXTRA_NAME_SERIES);
        int[] date = ConvertValue.prettyCompactToFormattedDate(dateOfSeries);

        return new DatePickerDialog(getActivity(), this, date[2], date[0], date[1]);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        SeriesActivity seriesActivity = (SeriesActivity)getActivity();
        seriesActivity.seriesDateChanged(
                getArguments().getLong(Constants.EXTRA_ID_SERIES),
                year,
                month,
                day);
    }
}
