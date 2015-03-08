package ca.josephroque.bowlingcompanion.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-19.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class NewLeagueEventDialog extends DialogFragment
{
    /** Instance of listener which contains methods that are executed upon user interaction */
    private NewLeagueEventDialogListener mDialogListener;

    /**
     * Provides a method to the activity which created this object to handle
     * user interaction with the dialog
     */
    public static interface NewLeagueEventDialogListener
    {
        /**
         * Executed when user opts to add a new league
         *
         * @param leagueName name of the new league to add
         * @param numberOfGames number of games of the new league
         */
        public void onAddNewLeague(String leagueName, byte numberOfGames);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //Displays a dialog to the user and waits for input
        AlertDialog.Builder newLeagueEventBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_league_event, null);

        final boolean isAddingEvent = getArguments().getBoolean(Constants.EXTRA_EVENT_MODE);

        //Different hint is displayed whether a new event or league is being added
        final EditText editTextName = (EditText)dialogView.findViewById(R.id.editText_new_league_event_name);
        editTextName.setHint(
                ((isAddingEvent)
                        ? "Event"
                        : "League")
                + " (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});

        final EditText editTextNumberOfGames = (EditText)dialogView.findViewById(R.id.editText_new_league_event_games);
        editTextNumberOfGames.setHint("# of games (1-"
                + ((isAddingEvent)
                        ? Constants.MAX_NUMBER_EVENT_GAMES
                        : Constants.MAX_NUMBER_LEAGUE_GAMES)
                + ")");

        if (savedInstanceState != null)
        {
            editTextName.setText(savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE));
            editTextNumberOfGames.setText(savedInstanceState.getString(Constants.EXTRA_NUMBER_OF_GAMES));
        }

        newLeagueEventBuilder.setView(dialogView)
                .setPositiveButton(Constants.DIALOG_ADD, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String leagueEventName = editTextName.getText().toString().trim();
                        String numberOfGamesText = editTextNumberOfGames.getText().toString().trim();
                        if (leagueEventName.length() > 0 && numberOfGamesText.length() > 0)
                        {
                            byte numberOfGames;
                            try
                            {
                                numberOfGames = Byte.parseByte(numberOfGamesText);
                            }
                            catch (NumberFormatException ex)
                            {
                                numberOfGames = -1;
                            }

                            mDialogListener.onAddNewLeague(leagueEventName, numberOfGames);
                        }
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
        return newLeagueEventBuilder.create();
    }

    @Override
    public void onAttach(Activity mActivity)
    {
        super.onAttach(mActivity);

        /*
         * Attempts to cast the parent activity to a listener object for this dialog.
         * If the parent activity cannot be cast to a listener, then this dialog
         * is effectively useless and the program will crash until a correct
         * implementation is completed
         */
        try
        {
            mDialogListener = (NewLeagueEventDialogListener)mActivity;
        }
        catch (ClassCastException ex)
        {
            throw new RuntimeException("Parent activity must implement NewLeagueEventDialogListener");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_NAME_LEAGUE,
                ((EditText)getDialog().findViewById(R.id.editText_new_league_event_name)).getText().toString());
        outState.putString(Constants.EXTRA_NUMBER_OF_GAMES,
                ((EditText)getDialog().findViewById(R.id.editText_new_league_event_games)).getText().toString());
    }
}
