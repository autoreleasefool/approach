package ca.josephroque.bowlingcompanion.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class NewBowlerDialog extends DialogFragment
{

    /** Instance of listener which contains methods that are executed upon user interaction */
    private NewBowlerDialogListener mDialogListener;

    /**
     * Provides a method to the activity which created this object to handle
     * user interaction with the dialog
     */
    public static interface NewBowlerDialogListener
    {
        /**
         * Executed when user opts to add a new bowler
         *
         * @param element name of the new bowler to add
         */
        public void onAddNewBowler(String element);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //Displays a dialog to the user and waits for input
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_bowler, null);

        EditText editTextName = (EditText)dialogView.findViewById(R.id.editText_bowler_name);
        editTextName.setHint("Name (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});

        dialogBuilder.setView(dialogView)
                .setPositiveButton(Constants.DIALOG_ADD, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String bowlerName = ((EditText)(dialogView.findViewById(R.id.editText_bowler_name))).getText().toString().trim();
                        mDialogListener.onAddNewBowler(bowlerName);
                    }
                })
                .setNegativeButton(Constants.DIALOG_CANCEL, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        return dialogBuilder.create();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        /*
         * Attempts to cast the parent activity to a listener object for this dialog.
         * If the parent activity cannot be cast to a listener, then this dialog
         * is effectively useless and the program will crash until a correct
         * implementation is completed
         */
        try
        {
            mDialogListener = (NewBowlerDialogListener)activity;
        }
        catch (ClassCastException ex)
        {
            throw new RuntimeException("Parent activity must implement NewBowlerDialogListener");
        }
    }
}
