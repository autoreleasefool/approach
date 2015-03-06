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
 * Created by josephroque on 15-03-06.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class ManualScoreDialog extends DialogFragment
{

    private ManualScoreDialogListener mDialogListener;

    public static interface ManualScoreDialogListener
    {
        public void onSetScore(short gameScore);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_set_score, null);

        final EditText editTextScore = (EditText)dialogView.findViewById(R.id.editText_score);
        editTextScore.setHint("Score (max 450)");
        editTextScore.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        dialogBuilder.setView(dialogView)
                .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        short gameScore =
                                Short.parseShort(editTextScore.getText().toString());
                        mDialogListener.onSetScore(gameScore);
                        dialog.dismiss();
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

        try
        {
            mDialogListener = (ManualScoreDialogListener)activity;
        }
        catch (ClassCastException ex)
        {
            throw new RuntimeException("Parent activity must implement ManualScoreDialogListener");
        }
    }
}
