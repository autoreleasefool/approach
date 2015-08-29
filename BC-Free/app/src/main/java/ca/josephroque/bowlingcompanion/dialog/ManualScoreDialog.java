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

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-03-19. Provides a dialog and a callback interface {@link
 * ManualScoreDialog.ManualScoreDialogListener} for the user to enter a score for a game manually. The purpose is to
 * allow them to include a game in their statistics, without requiring that they insert every game frame-by-frame.
 */
public class ManualScoreDialog
        extends DialogFragment {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "ManualScoreDialog";

    /** Maximum number of characters in a bowling score. */
    private static final byte MAX_SCORE_LENGTH = 3;

    /** Instance of callback listener. */
    private ManualScoreDialogListener mDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_set_score, null);

        final EditText editTextScore = (EditText) dialogView.findViewById(R.id.et_score);
        editTextScore.setHint("Score (max 450)");
        editTextScore.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_SCORE_LENGTH)});

        if (savedInstanceState != null)
            editTextScore.setText(savedInstanceState.getCharSequence("GameScore"));

        dialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editTextScore.length() > 0) {
                            short gameScore;
                            try {
                                gameScore = Short.parseShort(editTextScore.getText().toString());
                            } catch (NumberFormatException ex) {
                                gameScore = -1;
                            }
                            mDialogListener.onSetScore(gameScore);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence("GameScore",
                ((EditText) getDialog().findViewById(R.id.et_score)).getText());
    }

    /**
     * Callback interface which executes methods in activity upon user interaction.
     */
    public interface ManualScoreDialogListener {

        /**
         * Invoked when user opts to submit a manual score.
         *
         * @param gameScore score input from user
         */
        void onSetScore(short gameScore);
    }

    /**
     * Creates a new instance of ManualScoreDialog, sets listener member variable and returns the new instance.
     *
     * @param listener instance of callback interface
     * @return new instance of ManualScoreDialog
     */
    public static ManualScoreDialog newInstance(ManualScoreDialogListener listener) {
        ManualScoreDialog dialogFragment = new ManualScoreDialog();
        dialogFragment.mDialogListener = listener;
        return dialogFragment;
    }
}
