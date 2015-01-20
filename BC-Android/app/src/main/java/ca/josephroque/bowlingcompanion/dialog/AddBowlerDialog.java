package ca.josephroque.bowlingcompanion.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-01-10.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class AddBowlerDialog extends DialogFragment
{

    /** Maximum length for name input */
    public static final int INPUT_MAX_LENGTH = 30;

    /** Instance of a listener */
    private AddBowlerDialogListener dialogListener;

    /**
     * Interface which declares methods that respond to
     * user events
     */
    public static interface AddBowlerDialogListener
    {
        /**
         * Called when the user clicks the "Add" button on the popup dialog
         *
         * @param element user input for the bowler's name
         */
        public void onAddNewBowler(String element);

        /**
         * Called when the user clicks the "Cancel" button on the popup dialog
         */
        public void onCancelNewBowler();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_bowler, null);

        EditText editText = (EditText)dialogView.findViewById(R.id.new_bowler_name);
        editText.setHint("Name (max " + INPUT_MAX_LENGTH + " characters)");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INPUT_MAX_LENGTH)});

        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String bowlerName = ((EditText)(dialogView.findViewById(R.id.new_bowler_name))).getText().toString().trim();
                        dialogListener.onAddNewBowler(bowlerName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialogListener.onCancelNewBowler();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            dialogListener = (AddBowlerDialogListener) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString() + " must implement AddBowlerDialogListener");
        }
    }
}
