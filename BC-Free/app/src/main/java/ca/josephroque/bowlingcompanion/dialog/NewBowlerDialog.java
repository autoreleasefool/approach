package ca.josephroque.bowlingcompanion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-03-15.
 * <p/>
 * Provides a dialog and callback interface {@link NewBowlerDialog.NewBowlerDialogListener}
 * for the user to enter the name of a new bowler to track the statistics of.
 */
public class NewBowlerDialog extends DialogFragment
{
    /** Instance of listener which contains methods that are executed upon user interaction */
    private NewBowlerDialogListener mDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_new_bowler, null);

        final EditText editTextName = (EditText)dialogView.findViewById(R.id.et_bowler_name);
        editTextName.setHint("Name (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});

        if (savedInstanceState != null)
        {
            //Loads member variables from bundle
            editTextName.setText(savedInstanceState.getCharSequence(Constants.EXTRA_NAME_BOWLER));
        }

        dialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Gets name input and calls listener method if input is valid
                        String bowlerName = editTextName.getText().toString().trim();
                        if (bowlerName.length() > 0)
                            mDialogListener.onAddNewBowler(bowlerName);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
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
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Saves member variables to bundle
        outState.putCharSequence(Constants.EXTRA_NAME_BOWLER,
                ((EditText) getDialog().findViewById(R.id.et_bowler_name)).getText());
    }

    /**
     * Provides a method to the activity which created this object to handle
     * user interaction with the dialog
     */
    public static interface NewBowlerDialogListener
    {
        /**
         * Executed when user opts to add a new bowler
         *
         * @param bowlerName name of the new bowler to add
         */
        public void onAddNewBowler(String bowlerName);
    }

    /**
     * Creates a new instance of this DialogFragment and sets the listener
     * to the parameter passed through this method
     *
     * @param listener a listener for on click events
     * @return a new instance of NewBowlerDialog
     */
    public static NewBowlerDialog newInstance(NewBowlerDialogListener listener)
    {
        NewBowlerDialog dialog = new NewBowlerDialog();
        dialog.mDialogListener = listener;
        return dialog;
    }
}
