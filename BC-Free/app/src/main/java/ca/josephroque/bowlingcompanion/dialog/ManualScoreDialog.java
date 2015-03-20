package ca.josephroque.bowlingcompanion.dialog;

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
 * Created by josephroque on 15-03-19.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class ManualScoreDialog extends DialogFragment
{

    private ManualScoreDialogListener mDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_set_score, null);

        final EditText editTextScore = (EditText)dialogView.findViewById(R.id.et_score);
        editTextScore.setHint("Score (max 450)");
        editTextScore.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        if (savedInstanceState != null)
            editTextScore.setText(savedInstanceState.getCharSequence("GameScore"));

        dialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (editTextScore.length() > 0)
                        {
                            short gameScore;
                            try
                            {
                                gameScore = Short.parseShort(editTextScore.getText().toString());
                            }
                            catch (NumberFormatException ex)
                            {
                                gameScore = -1;
                            }
                            mDialogListener.onSetScore(gameScore);
                        }
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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putCharSequence("GameScore",
                ((EditText)getDialog().findViewById(R.id.et_score)).getText());
    }

    public static interface ManualScoreDialogListener
    {
        public void onSetScore(short gameScore);
    }
}
