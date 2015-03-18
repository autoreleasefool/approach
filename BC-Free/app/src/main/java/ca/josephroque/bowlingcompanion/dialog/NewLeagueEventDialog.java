package ca.josephroque.bowlingcompanion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-03-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.dialog
 * in project Bowling Companion
 */
public class NewLeagueEventDialog extends DialogFragment
{
    /** Instance of listener which contains methods that are executed upon user interaction */
    private NewLeagueEventDialogListener mDialogListener;
    /** If true, a new event is being added, a league otherwise */
    private boolean isEventMode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_league_event, null);

        CharSequence leagueEventName = "";
        CharSequence leagueEventNumberOfGames = "";

        if (savedInstanceState != null)
        {
            //Loads member variables from bundle
            isEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            leagueEventName = savedInstanceState.getCharSequence(Constants.EXTRA_NAME_LEAGUE);
            leagueEventNumberOfGames = savedInstanceState.getCharSequence(Constants.EXTRA_NUMBER_OF_GAMES);
        }
        else
        {
            isEventMode = getArguments().getBoolean(Constants.EXTRA_EVENT_MODE);
        }

        final EditText editTextName = (EditText)dialogView.findViewById(R.id.et_league_event_name);
        editTextName.setHint(
                ((isEventMode)
                        ? "Event"
                        : "League")
                + " (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});
        editTextName.setText(leagueEventName);

        final EditText editTextNumberOfGames = (EditText)dialogView.findViewById(R.id.et_league_event_games);
        editTextNumberOfGames.setHint("# of games (1-"
                + ((isEventMode)
                        ? Constants.MAX_NUMBER_EVENT_GAMES
                        : Constants.MAX_NUMBER_LEAGUE_GAMES)
                + ")");
        editTextNumberOfGames.setText(leagueEventNumberOfGames);

        final boolean event = isEventMode;
        dialogBuilder.setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Gets input from EditText objects and calls listener method if input is valid
                        String leagueEventName = editTextName.getText().toString().trim();
                        String strNumberOfGames = editTextNumberOfGames.getText().toString().trim();
                        if (leagueEventName.length() > 0 && strNumberOfGames.length() > 0)
                        {
                            byte numberOfGames;
                            try
                            {
                                numberOfGames = Byte.parseByte(strNumberOfGames);
                            }
                            catch (NumberFormatException ex)
                            {
                                numberOfGames = -1;
                            }

                            mDialogListener.onAddNewLeagueEvent(event, leagueEventName, numberOfGames);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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
        outState.putBoolean(Constants.EXTRA_EVENT_MODE, isEventMode);
        outState.putCharSequence(Constants.EXTRA_NAME_LEAGUE,
                ((EditText)getDialog().findViewById(R.id.et_league_event_name)).getText());
        outState.putCharSequence(Constants.EXTRA_NUMBER_OF_GAMES,
                ((EditText)getDialog().findViewById(R.id.et_league_event_games)).getText());
    }

    /**
     * Provides a method to the activity which created this object to handle
     * user interaction with the dialog
     */
    public static interface NewLeagueEventDialogListener
    {
        /**
         * Executed when user opts to add a new league or event
         * @param isEvent if true, a new event was added. If false, a new league was added
         * @param leagueEventName name of the new league or event
         * @param numberOfGames number of games in the new league or event
         */
        public void onAddNewLeagueEvent(boolean isEvent, String leagueEventName, byte numberOfGames);
    }

    /**
     * Creates a new instance of this DialogFragment and sets the listener
     * to the parameter passed through this method
     *
     * @param listener a listener for on click events
     * @return a new instance of NewLeagueEventDialog
     */
    public static NewLeagueEventDialog newInstance(NewLeagueEventDialogListener listener)
    {
        NewLeagueEventDialog dialog = new NewLeagueEventDialog();
        dialog.mDialogListener = listener;
        return dialog;
    }
}
