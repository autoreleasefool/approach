package ca.josephroque.bowlingcompanion.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 2015-12-30. A dialog for the user to either create a new event and name it, or to select a
 * league for each bowler that will be a part of the team.
 */
public class TeamSetupDialog
        extends DialogFragment {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TeamSetupDialog";

    /** Instance of callback listener. */
    private TeamSetupDialogListener mDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        final View dialogView = View.inflate(getContext(), R.layout.dialog_create_team, null);

        // Getting references to input views
        final EditText editTextEventName = (EditText) dialogView.findViewById(R.id.et_team_event_name);
        final EditText editTextEventGames = (EditText) dialogView.findViewById(R.id.et_team_event_games);
        final EditText editTextLeagueGames = (EditText) dialogView.findViewById(R.id.et_team_league_games);
        final RadioButton radioButtonEvent = (RadioButton) dialogView.findViewById(R.id.rb_team_event);
        final RadioButton radioButtonLeague = (RadioButton) dialogView.findViewById(R.id.rb_team_league);

        // Setting hints for the text input fields
        editTextEventName.setHint(String.format(getResources().getString(R.string.text_max_name_length_placeholder),
                "Event",
                Constants.NAME_MAX_LENGTH));
        editTextEventGames.setHint(String.format("# of games (1-%d)", Constants.MAX_NUMBER_EVENT_GAMES));
        editTextLeagueGames.setHint(String.format("# of games (1-%d)", Constants.MAX_NUMBER_LEAGUE_GAMES));

        // Disabling the league name input to start
        editTextLeagueGames.setEnabled(false);

        radioButtonEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButtonLeague.setChecked(false);
                    editTextEventName.requestFocus();
                }

                editTextEventName.setEnabled(isChecked);
                editTextEventGames.setEnabled(isChecked);
            }
        });

        radioButtonLeague.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButtonEvent.setChecked(false);
                    editTextLeagueGames.requestFocus();
                }

                editTextLeagueGames.setEnabled(isChecked);
            }
        });

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (radioButtonEvent.isChecked())
                        parseEventTeamDetails(editTextEventName.getText(), editTextEventGames.getText());
                    else
                        parseLeagueTeamDetails(editTextLeagueGames.getText());
                }

                dialog.dismiss();
            }
        };

        dialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.dialog_okay, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener);
        return dialogBuilder.create();
    }

    /**
     * Considers user input for an event name and number of games, provided as {@link java.lang.CharSequence} objects,
     * and extracts the relevant information to begin creating a team for the user to score.
     *
     * @param seqEventName user input; name of the event
     * @param seqNumberOfGames user input; number of games in the event.
     */
    private void parseEventTeamDetails(CharSequence seqEventName, CharSequence seqNumberOfGames) {
        String eventName = null;
        byte numberOfGames = -1;

        if (!TextUtils.isEmpty(seqEventName)) {
            eventName = seqEventName.toString().trim();
        }

        if (!TextUtils.isEmpty(seqNumberOfGames)) {
            try {
                numberOfGames = Byte.parseByte(seqNumberOfGames.toString().trim());
            } catch (NumberFormatException ex) {
                numberOfGames = -1;
            }
        }

        if (mDialogListener != null)
            mDialogListener.onCreateEventTeam(eventName, numberOfGames);
    }

    /**
     * Considers user input for a number of games in a league, provided as a {@link java.lang.CharSequence} object, and
     * extracts the relevant information to begin creating a team for the user to score.
     *
     * @param seqNumberOfGames user input; number of games in the leagues.
     */
    private void parseLeagueTeamDetails(CharSequence seqNumberOfGames) {
        byte numberOfGames = -1;

        if (!TextUtils.isEmpty(seqNumberOfGames)) {
            try {
                numberOfGames = Byte.parseByte(seqNumberOfGames.toString().trim());
            } catch (NumberFormatException ex) {
                numberOfGames = -1;
            }
        }

        if (mDialogListener != null)
            mDialogListener.onCreateLeagueTeam(numberOfGames);
    }

    /**
     * Callback interface which executes methods in activity upon user interaction.
     */
    public interface TeamSetupDialogListener {

        /**
         * Prompts the user to select a number of bowlers to record scores of for a new event, with the name {@code
         * eventName} and {@code numberOfGames} games (up to 20).
         *
         * @param eventName name of the event
         * @param numberOfGames number of games in the event (max 20)
         */
        void onCreateEventTeam(String eventName, byte numberOfGames);

        /**
         * Prompts the user to select a number of bowlers and a league for each to record the scores of. The leagues
         * selected must have exactly {@code numberOfGames} games.
         *
         * @param numberOfGames number of games that leagues must have (up to 5).
         */
        void onCreateLeagueTeam(byte numberOfGames);
    }

    /**
     * Creates a new instance of TeamSetupDialog, sets listener member variable and returns the new instance.
     *
     * @param listener instance of callback interface
     * @return new instance of TeamSetupDialog
     */
    public static TeamSetupDialog newInstance(TeamSetupDialogListener listener) {
        TeamSetupDialog dialogFragment = new TeamSetupDialog();
        dialogFragment.mDialogListener = listener;
        return dialogFragment;
    }
}
