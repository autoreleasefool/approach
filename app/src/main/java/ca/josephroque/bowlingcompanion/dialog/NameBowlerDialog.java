package ca.josephroque.bowlingcompanion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.data.Bowler;

/**
 * Created by Joseph Roque on 15-03-15. Provides a dialog and callback interface {@link
 * ca.josephroque.bowlingcompanion.dialog.NameBowlerDialog.NameBowlerDialogListener} for the user to enter the name of a
 * new bowler to track the statistics of.
 */
public class NameBowlerDialog
        extends DialogFragment {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NewBowlerDialog";

    /** Argument to indicate if the dialog should edit the name of a bowler. */
    private static final String ARG_EDITING_NAME = "arg_editing_name";
    /** Argument to indicate the bowler name being changed. */
    private static final String ARG_BOWLER = "arg_bowler";

    /** Instance of listener which contains methods that are executed upon user interaction. */
    private NameBowlerDialogListener mDialogListener;

    /** Indicates if the dialog is being used to edit the name of an existing bowler. */
    private boolean mEditingName;
    /** Bowler whose name will be changed. Null if {@code mEditingName} is false. */
    private Bowler mBowlerToChange;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        final View dialogView = View.inflate(getContext(), R.layout.dialog_new_bowler, null);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.et_bowler_name);
        editTextName.setHint("Name (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)
        });

        if (savedInstanceState != null) {
            //Loads member variables from bundle
            editTextName.setText(savedInstanceState.getCharSequence(Constants.EXTRA_NAME_BOWLER));
            mEditingName = savedInstanceState.getBoolean(ARG_EDITING_NAME, false);
            if (mEditingName)
                mBowlerToChange = savedInstanceState.getParcelable(ARG_BOWLER);
        } else {
            Bundle arguments = getArguments();
            mEditingName = arguments.getBoolean(ARG_EDITING_NAME, false);
            if (mEditingName)
                mBowlerToChange = arguments.getParcelable(ARG_BOWLER);
        }

        int textToFinish = (mEditingName)
                ? R.string.dialog_change
                : R.string.dialog_add;
        dialogBuilder.setView(dialogView)
                .setPositiveButton(textToFinish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Gets name input and calls listener method if input is valid
                        String bowlerName = editTextName.getText().toString().trim();
                        if (bowlerName.length() > 0) {
                            if (mEditingName)
                                mDialogListener.onChangeBowlerName(mBowlerToChange, bowlerName);
                            else
                                mDialogListener.onAddNewBowler(bowlerName);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return dialogBuilder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Saves member variables to bundle
        outState.putCharSequence(Constants.EXTRA_NAME_BOWLER,
                ((EditText) getDialog().findViewById(R.id.et_bowler_name)).getText());
        outState.putBoolean(ARG_EDITING_NAME, mEditingName);
        if (mEditingName)
            outState.putParcelable(ARG_BOWLER, mBowlerToChange);
    }

    /**
     * Provides a method to the activity which created this object to handle user interaction with the dialog.
     */
    public interface NameBowlerDialogListener {

        /**
         * Executed when user opts to add a new bowler.
         *
         * @param bowlerName name of the new bowler to add
         */
        void onAddNewBowler(String bowlerName);

        /**
         * Executed when user opts to change a bowler's name.
         *
         * @param bowler bowler to change name of
         * @param newName bowler's new name
         */
        void onChangeBowlerName(Bowler bowler, String newName);
    }

    /**
     * Creates a new instance of this DialogFragment and sets the listener to the parameter passed through this method.
     *
     * @param listener a listener for on click events
     * @param editingName if {@code true}, the dialog will be used to edit a bowler's name
     * @param bowlerToChange if {@code editingName} is true, a bowler whose name will be edited must be included.
     * Otherwise, this parameter can be null.
     * @return a new instance of NameBowlerDialog
     */
    public static NameBowlerDialog newInstance(NameBowlerDialogListener listener,
                                               boolean editingName,
                                               @Nullable Bowler bowlerToChange) {
        NameBowlerDialog dialog = new NameBowlerDialog();
        dialog.mDialogListener = listener;
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDITING_NAME, editingName);
        if (editingName) {
            if (bowlerToChange == null)
                throw new IllegalArgumentException("Must include a bowler to edit the name of.");
            else
                args.putParcelable(ARG_BOWLER, bowlerToChange);
        }
        dialog.setArguments(args);
        return dialog;
    }
}
