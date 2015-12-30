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
import android.widget.TextView;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.wrapper.LeagueEvent;

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

    /**
     * Argument to indicate if the dialog should provide an input for the number of games, or just to edit the
     * instance.
     */
    private static final String ARG_EDITING = "arg_editing";
    /** Argument to indicate the league / event name being changed. */
    private static final String ARG_LEAGUE_EVENT = "arg_league_event";

    /** Instance of listener which contains methods that are executed upon user interaction. */
    private NameLeagueEventDialogListener mDialogListener;

    /** If true, a new event is being added, a league otherwise. */
    private boolean mIsEventMode;
    /** If true, a league or event is being changed. */
    private boolean mEditing;
    /** The league / event being edited. Null if a new league / event is being created. */
    private LeagueEvent mLeagueEvent;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        final View dialogView = View.inflate(getContext(), R.layout.dialog_new_league_event, null);

        CharSequence leagueEventName = "";
        CharSequence leagueEventNumberOfGames = "";
        CharSequence leagueBaseAverage = "";
        CharSequence leagueCurrentGames = "";

        if (savedInstanceState != null) {
            //Loads member variables from bundle
            mIsEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mEditing = savedInstanceState.getBoolean(ARG_EDITING);
            leagueEventName = savedInstanceState.getCharSequence(Constants.EXTRA_NAME_LEAGUE);

            if (!mIsEventMode) {
                leagueBaseAverage = savedInstanceState.getCharSequence(Constants.EXTRA_BASE_AVERAGE);
                leagueCurrentGames = savedInstanceState.getCharSequence(Constants.EXTRA_BASE_GAMES);
            }

            if (!mEditing)
                leagueEventNumberOfGames = savedInstanceState.getCharSequence(Constants.EXTRA_NUMBER_OF_GAMES);
            else
                mLeagueEvent = savedInstanceState.getParcelable(ARG_LEAGUE_EVENT);
        } else {
            Bundle arguments = getArguments();
            mIsEventMode = arguments.getBoolean(Constants.EXTRA_EVENT_MODE);
            mEditing = arguments.getBoolean(ARG_EDITING);
            if (mEditing) {
                mLeagueEvent = arguments.getParcelable(ARG_LEAGUE_EVENT);
                if (mLeagueEvent != null) {
                    leagueEventName = mLeagueEvent.getLeagueEventName();
                    if (mLeagueEvent.getBaseAverage() >= 0)
                        leagueBaseAverage = Short.toString(mLeagueEvent.getBaseAverage());
                    if (mLeagueEvent.getBaseGames() > 0)
                        leagueCurrentGames = Integer.toString(mLeagueEvent.getBaseGames());
                }
            }
        }

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.et_league_event_name);
        editTextName.setHint(
                ((mIsEventMode)
                        ? "Event"
                        : "League")
                        + " (max " + Constants.NAME_MAX_LENGTH + " characters)");
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.NAME_MAX_LENGTH)});
        editTextName.setText(leagueEventName);

        final EditText editTextBaseAverage = (EditText) dialogView.findViewById(R.id.et_league_base_avg);
        final EditText editTextCurrentGames = (EditText) dialogView.findViewById(R.id.et_league_base_games);
        final TextView textViewBaseAverage = (TextView) dialogView.findViewById(R.id.tv_base_avg);
        if (mIsEventMode) {
            textViewBaseAverage.setVisibility(View.GONE);
            editTextCurrentGames.setVisibility(View.GONE);
            editTextBaseAverage.setVisibility(View.GONE);
        } else {
            editTextBaseAverage.setHint("Current Average");
            editTextBaseAverage.setText(leagueBaseAverage);
            editTextCurrentGames.setHint("Games so far (max 100,000)");
            editTextCurrentGames.setText(leagueCurrentGames);
        }

        final EditText editTextNumberOfGames = (EditText) dialogView.findViewById(R.id.et_league_event_games);
        final int positiveButtonText;
        if (mEditing) {
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
                    if (mEditing) {
                        updateLeagueEvent(editTextName.getText().toString().trim(),
                                editTextBaseAverage.getText().toString().trim(),
                                editTextCurrentGames.getText().toString().trim());
                    } else {
                        addNewLeagueEvent(editTextName.getText().toString().trim(),
                                editTextNumberOfGames.getText().toString().trim(),
                                editTextBaseAverage.getText().toString().trim(),
                                editTextCurrentGames.getText().toString().trim());
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
        outState.putBoolean(ARG_EDITING, mEditing);
        outState.putCharSequence(Constants.EXTRA_NAME_LEAGUE,
                ((EditText) getDialog().findViewById(R.id.et_league_event_name)).getText());

        if (!mIsEventMode) {
            outState.putCharSequence(Constants.EXTRA_BASE_AVERAGE,
                    ((EditText) getDialog().findViewById(R.id.et_league_base_avg)).getText());
            outState.putCharSequence(Constants.EXTRA_BASE_GAMES,
                    ((EditText) getDialog().findViewById(R.id.et_league_base_games)).getText());
        }

        if (!mEditing)
            outState.putCharSequence(Constants.EXTRA_NUMBER_OF_GAMES,
                    ((EditText) getDialog().findViewById(R.id.et_league_event_games)).getText());
        else
            outState.putParcelable(ARG_LEAGUE_EVENT, mLeagueEvent);
    }

    /**
     * Checks if the user input is valid, and changes the name and base average of the league / event if so.
     *
     * @param leagueName new name for league / event
     * @param strBaseAverage new base average for the league / event
     * @param strBaseGames new base number of games for determining the average
     */
    private void updateLeagueEvent(String leagueName, String strBaseAverage, String strBaseGames) {
        short baseAverage;
        int baseGames;
        if (strBaseAverage.length() > 0) {
            try {
                baseAverage = Short.parseShort(strBaseAverage);
            } catch (NumberFormatException ex) {
                baseAverage = -1;
            }

            if (baseAverage > -1 && strBaseGames.length() > 0) {
                try {
                    baseGames = Integer.parseInt(strBaseGames);
                } catch (NumberFormatException ex) {
                    baseGames = 0;
                }
            } else {
                baseGames = 0;
            }
        } else {
            baseAverage = -1;
            baseGames = 0;
        }

        if (baseAverage >= 0 && baseGames == 0)
            baseGames = 1;

        if (leagueName.length() > 0 && baseAverage >= -1 && baseAverage <= Constants.GAME_MAX_SCORE && baseGames >= 0
                && baseGames <= Constants.MAXIMUM_BASE_GAMES)
            mDialogListener.onUpdateLeagueEvent(mLeagueEvent, leagueName, baseAverage, baseGames);
    }

    /**
     * Checks if the user input is valid, and creates a new league / event if so.
     *
     * @param newLeagueName name for new league / event
     * @param strNumberOfGames number of games for new league / event
     * @param strBaseAverage base average of the league
     * @param strBaseGames base number of games for determining the average
     */
    private void addNewLeagueEvent(String newLeagueName,
                                   String strNumberOfGames,
                                   String strBaseAverage,
                                   String strBaseGames) {
        if (newLeagueName.length() > 0 && strNumberOfGames.length() > 0) {
            byte numberOfGames;
            try {
                numberOfGames = Byte.parseByte(strNumberOfGames);
            } catch (NumberFormatException ex) {
                numberOfGames = -1;
            }

            short baseAverage;
            int baseGames;
            if (strBaseAverage.length() > 0) {
                try {
                    baseAverage = Short.parseShort(strBaseAverage);
                } catch (NumberFormatException ex) {
                    baseAverage = -1;
                }

                if (baseAverage > -1 && strBaseGames.length() > 0) {
                    try {
                        baseGames = Integer.parseInt(strBaseGames);
                    } catch (NumberFormatException ex) {
                        baseGames = 0;
                    }
                } else {
                    baseGames = 1;
                }
            } else {
                baseAverage = -1;
                baseGames = 0;
            }

            if (baseAverage >= 0 && baseGames == 0)
                baseGames = 1;

            mDialogListener.onAddNewLeagueEvent(mIsEventMode,
                    newLeagueName,
                    numberOfGames,
                    baseAverage,
                    baseGames);
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
         * @param name name of the new league or event
         * @param numberOfGames number of games in the new league or event
         * @param baseAverage base average for the game
         * @param baseGames base number of games for determining the average
         */
        void onAddNewLeagueEvent(boolean isEvent,
                                 String name,
                                 byte numberOfGames,
                                 short baseAverage,
                                 int baseGames);

        /**
         * Executed when user opts to change the name or base average of a league or event.
         *
         * @param leagueEvent league or event name that is being changed
         * @param name new name
         * @param baseAverage new base average for the game
         * @param baseGames base number of games for determining the average
         */
        void onUpdateLeagueEvent(LeagueEvent leagueEvent, String name, short baseAverage, int baseGames);
    }

    /**
     * Creates a new instance of this DialogFragment and sets the listener to the parameter passed through this method.
     *
     * @param listener a listener for on click events
     * @param newEvent indicates if a new event or league is being created. Ignored if {@code editing} is {@code true}
     * @param editing if {@code true}, then an event or league is being changed
     * @param leagueEvent league / event to change. Can be null if {@code editing} is false
     * @return a new instance of NameLeagueEventDialog
     */
    public static NameLeagueEventDialog newInstance(NameLeagueEventDialogListener listener,
                                                    boolean newEvent,
                                                    boolean editing,
                                                    @Nullable LeagueEvent leagueEvent) {
        NameLeagueEventDialog dialog = new NameLeagueEventDialog();
        dialog.mDialogListener = listener;

        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_EVENT_MODE, newEvent);
        args.putBoolean(ARG_EDITING, editing);
        if (editing) {
            if (leagueEvent == null)
                throw new IllegalArgumentException("Must provide a league / event to change.");
            else
                args.putParcelable(ARG_LEAGUE_EVENT, leagueEvent);
        }
        dialog.setArguments(args);
        return dialog;
    }
}
