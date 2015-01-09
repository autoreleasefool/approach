package ca.josephroque.bowlingcompanion;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by josephroque on 15-01-08.
 */
public class NewBowlerDialog extends DialogFragment
{

    public interface NewBowlerDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String fn, String ln);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NewBowlerDialogListener dialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_bowler, null);

        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String firstName = ((EditText)(dialogView.findViewById(R.id.new_bowler_firstname))).getText().toString();
                        String lastName = ((EditText)(dialogView.findViewById(R.id.new_bowler_lastname))).getText().toString();
                        dialogListener.onDialogPositiveClick(NewBowlerDialog.this, firstName, lastName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialogListener.onDialogNegativeClick(NewBowlerDialog.this);
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
            dialogListener = (NewBowlerDialogListener) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString() + " must implement NewBowlerDialogListener");
        }
    }
}
