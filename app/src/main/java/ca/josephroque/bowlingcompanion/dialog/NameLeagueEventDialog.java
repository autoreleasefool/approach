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
import ca.josephroque.bowlingcompanion.bowling.LeagueEvent;

/**
 * Created by Joseph Roque on 15-03-16. Provides dialog and callback interface {@link
 * ca.josephroque.bowlingcompanion.dialog.NameLeagueEventDialog.NameLeagueEventDialogListener} for the user to enter the
 * name of a new league or event to track the statistics of.
 */
public class NameLeagueEventDialog
        extends DialogFragment {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NewLeagueEventDialog";

    /** Argument to indicate if the dialog should provide an input for the number of games, or just to edit a name. */
    private static final String ARG_EDITING_NAME = "arg_editing_name";
    /** Argument to indicate the league / event name being changed. */
    private static final String ARG_LEAGUE_EVENT = "arg_league_event";

    /** Instance of listener which contains methods that are executed upon user interaction. */
    private NameLeagueEventDialogListener mDialogListener;

    /** If true, a new event is being added, a league otherwise. */
    private boolean mIsEventMode;
    /** If true, a league or event's name is being changed. */
    private boolean mEditingName;
    /** The league / event being edited. Null if a new league / event is being created. */
    private LeagueEvent mLeagueEvent;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_new_league_event, null);

        CharSequence leagueEventName = "";
        CharSequence leagueEventNumberOfGames = "";

        if (savedInstanceState != null) {
            //Loads member variables from bundle
            mIsEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mEditingName = savedInstanceState.getBoolean(ARG_EDITING_NAME);
            leagueEventName = savedInstanceState.getCharSequence(Constants.EXTRA_NAME_LEAGUE);

            if (!mEditingName)
                leagueEventNumberOfGames = savedInstanceState.getCharSequence(Constants.EXTRA_NUMBER_OF_GAMES);
            else
                mLeagueEvent = savedInstanceState.getParcelable(ARG_LEAGUE_EVENT);
        } else {
            Bundle arguments = getArguments();
            mIsEventMode = arguments.getBoolean(Constants.EXTRA_EVENT_MODE);
            mEditingName = arguments.getBoolean(ARG_EDITING_NAME);
            if (mEditingName)
                mLeagueEvent = arguments.getParcelable(ARG_LEAGUE_EVENT);
        }

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.et_league_event_name);
        editTextName.setHint(
                ((mIsEventMode)
                        ? "Event"
                        : "League")
                        + " (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});
        editTextName.setText(leagueEventName);

        final EditText editTextNumberOfGames =
                (EditText) dialogView.findViewById(R.id.et_league_event_games);
        final int positiveButtonText;
        if (mEditingName) {
            editTextNumberOfGames.setVisibility(View.GONE);
            positiveButtonText = R.string.dialog_change;
        } else {
            editTextNumberOfGames.setHint("# of games (1-" + ((mIsEventMode)
                    ? Constants.MAX_NUMBER_EVENT_GAMES
                    : Constants.MAX_NUMBER_LEAGUE_GAMES) + ")");
            editTextNumberOfGames.setText(leagueEventNumberOfGames);
            positiveButtonText = R.string.dialog_add;
        }

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    if (mEditingName) {
                        changeLeagueName(editTextName.getText().toString().trim());
                    } else {
                        addNewLeague(editTextName.getText().toString().trim(),
                                editTextNumberOfGames.getText().toString().trim());
                    }
                }
                dialog.dismiss();
            }
        };

        dialogBuilder.setView(dialogView)
                .setPositiveButton(positiveButtonText, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener);
        return dialogBuilder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Saves member variables to bundle
        outState.putBoolean(Constants.EXTRA_EVENT_MODE, mIsEventMode);
        outState.putBoolean(ARG_EDITING_NAME, mEditingName);
        outState.putCharSequence(Constants.EXTRA_NAME_LEAGUE,
                ((EditText) getDialog().findViewById(R.id.et_league_event_name)).getText());

        if (!mEditingName)
            outState.putCharSequence(Constants.EXTRA_NUMBER_OF_GAMES,
                    ((EditText) getDialog().findViewById(R.id.et_league_event_games)).getText());
        else
            outState.putParcelable(ARG_LEAGUE_EVENT, mLeagueEvent);
    }

    /**
     * Checks if the user input is valid, and changes the name of the league / event if so.
     *
     * @param newLeagueName new name for league / event
     */
    private void changeLeagueName(String newLeagueName) {
        if (newLeagueName.length() > 0)
            mDialogListener.onChangeLeagueEventName(mLeagueEvent, newLeagueName);
    }

    /**
     * Checks if the user input is valid, and creates a new league / event if so.
     *
     * @param newLeagueName name for new league / event
     * @param strNumberOfGames number of games for new league / event
     */
    private void addNewLeague(String newLeagueName, String strNumberOfGames) {
        if (newLeagueName.length() > 0 && strNumberOfGames.length() > 0) {
            byte numberOfGames;
            try {
                numberOfGames = Byte.parseByte(strNumberOfGames);
            } catch (NumberFormatException ex) {
                numberOfGames = -1;
            }

            mDialogListener.onAddNewLeagueEvent(mIsEventMode,
                    newLeagueName,
                    numberOfGames);
        }
    }

    /**
     * Provides a method to the activity which created this object to handle user interaction with the dialog.
     */
    public interface NameLeagueEventDialogListener {

        /**
         * Executed when user opts to add a new league or event.
         *
         * @param isEvent if true, a new event was added. If false, a new league was added
         * @param leagueEventName name of the new league or event
         * @param numberOfGames number of games in the new league or event
         */
        void onAddNewLeagueEvent(boolean isEvent, String leagueEventName, byte numberOfGames);

        /**
         * Executed when user opts to change the name of a league or event.
         *
         * @param leagueEvent league or event name that is being changed
         * @param leagueEventName new name
         */
        void onChangeLeagueEventName(LeagueEvent leagueEvent, String leagueEventName);
    }

    /**
     * Creates a new instance of this DialogFragment and sets the listener to the parameter passed through this method.
     *
     * @param listener a listener for on click events
     * @param newEvent indicates if a new event or league is being created. Ignored if {@code editingName} is {@code
     * true}
     * @param editingName if {@code true}, then the name of an event or league is being changed
     * @param leagueEvent league / event to change name of. Can be null if {@code editingName} is false
     * @return a new instance of NameLeagueEventDialog
     */
    public static NameLeagueEventDialog newInstance(NameLeagueEventDialogListener listener,
                                                    boolean newEvent,
                                                    boolean editingName,
                                                    @Nullable LeagueEvent leagueEvent) {
        NameLeagueEventDialog dialog = new NameLeagueEventDialog();
        dialog.mDialogListener = listener;

        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_EVENT_MODE, newEvent);
        args.putBoolean(ARG_EDITING_NAME, editingName);
        if (editingName) {
            if (leagueEvent == null)
                throw new IllegalArgumentException("Must provide a league / event to change.");
            else
                args.putParcelable(ARG_LEAGUE_EVENT, leagueEvent);
        }
        dialog.setArguments(args);
        return dialog;
    }
}
