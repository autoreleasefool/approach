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
 */
public class AddLeagueDialog extends DialogFragment
{

    public static final int NAME_MAX_LENGTH = 30;

    private AddLeagueDialogListener dialogListener = null;

    public static interface AddLeagueDialogListener
    {
        public void onAddNewLeague(String leagueName, int numberOfGames);
        public void onCancelNewLeague();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_league, null);

        EditText editText = (EditText)dialogView.findViewById(R.id.new_league_name);
        editText.setHint("League (max " + NAME_MAX_LENGTH + " characters)");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NAME_MAX_LENGTH)});

        editText = (EditText)dialogView.findViewById(R.id.new_league_number_of_games);
        editText.setHint("# of games (1-5");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String leagueName = ((EditText)(dialogView.findViewById(R.id.new_league_name))).getText().toString();
                        int numberOfGames = -1;

                        try
                        {
                            numberOfGames = Integer.parseInt(((EditText)(dialogView.findViewById(R.id.new_league_number_of_games))).getText().toString());
                        }
                        catch (NumberFormatException ex)
                        {
                            numberOfGames = -1;
                        }

                        dialogListener.onAddNewLeague(leagueName, numberOfGames);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialogListener.onCancelNewLeague();
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
            dialogListener = (AddLeagueDialogListener) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString() + " must implement AddLeagueDialogListener");
        }
    }
}
