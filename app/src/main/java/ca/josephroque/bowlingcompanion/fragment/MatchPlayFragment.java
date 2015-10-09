package ca.josephroque.bowlingcompanion.fragment;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.MatchPlayEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * Displays options for setting match play results for a game.
 */
public class MatchPlayFragment
        extends Fragment
        implements Theme.ChangeableTheme {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MatchPlayFragment";

    /** Represents the opponent's name. */
    private static final String ARG_OPPONENT_NAME = "arg_opp_name";
    /** Represents the opponent's score. */
    private static final String ARG_OPPONENT_SCORE = "arg_opp_score";
    /** Represents the user's selected match play result. */
    private static final String ARG_SELECTED_RESULT = "arg_selected_result";

    /** TextView to display the bowler's name. */
    private TextView mTextViewBowler;
    /** TextView to display the league or event's name. */
    private TextView mTextViewLeagueEvent;
    /** TextView to display the date of the series or event. */
    private TextView mTextViewDate;
    /** TextView to display the number of game. */
    private TextView mTextViewGameNumber;
    /** TextView to display the user's score. */
    private TextView mTextViewScore;

    /** Input field for opponent's name. */
    private EditText mEditTextOpponentName;
    /** Input field for opponent's score. */
    private EditText mEditTextOpponentScore;

    /** Radio buttons indicating the results of the match. */
    private RadioGroup mRadioGroupMatchResult;

    /** Game for which match play statistics are being shown. */
    private long mGameId;
    /** Indicates if the fragment was loaded from a saved instance state. */
    private boolean mFromSavedInstanceState;
    /** Indicates if the match play results have finished loading from the database. */
    private boolean mFinishedLoadingResults;
    /** The selected radio button. Used to restore instance state. */
    private int mSelectedRadioButtonId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_match_play, container, false);

        mTextViewBowler = (TextView) rootView.findViewById(R.id.tv_bowler_name);
        mTextViewLeagueEvent = (TextView) rootView.findViewById(R.id.tv_league_name);
        mTextViewDate = (TextView) rootView.findViewById(R.id.tv_series_name);
        mTextViewGameNumber = (TextView) rootView.findViewById(R.id.tv_game_number);
        mTextViewScore = (TextView) rootView.findViewById(R.id.tv_score);
        mEditTextOpponentName = (EditText) rootView.findViewById(R.id.et_opponent_name);
        mEditTextOpponentScore = (EditText) rootView.findViewById(R.id.et_opponent_score);
        mRadioGroupMatchResult = (RadioGroup) rootView.findViewById(R.id.rg_match_results);

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
            mGameId = savedInstanceState.getLong(Constants.EXTRA_ID_GAME);
            String opponentName = savedInstanceState.getString(ARG_OPPONENT_NAME);
            String opponentScore = savedInstanceState.getString(ARG_OPPONENT_SCORE);
            mSelectedRadioButtonId = savedInstanceState.getInt(ARG_SELECTED_RESULT);

            mEditTextOpponentName.setText(opponentName);
            mEditTextOpponentScore.setText(opponentScore);
        } else {
            mGameId = getArguments().getLong(Constants.EXTRA_ID_GAME);
        }

        setupToolbar(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setFloatingActionButtonState(0);
            mainActivity.setDrawerState(false);

            if (mFromSavedInstanceState && mSelectedRadioButtonId != -1)
                mRadioGroupMatchResult.check(mSelectedRadioButtonId);

            mainActivity.setActionBarTitle(R.string.title_fragment_match_play, true);
            mFinishedLoadingResults = false;
            new LoadMatchPlayTask(this).execute(mGameId);
        }

        updateTheme();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.EXTRA_ID_GAME, mGameId);
        outState.putString(ARG_OPPONENT_NAME, mEditTextOpponentName.getText().toString());
        outState.putString(ARG_OPPONENT_SCORE, mEditTextOpponentScore.getText().toString());
        outState.putInt(ARG_SELECTED_RESULT, mRadioGroupMatchResult.getCheckedRadioButtonId());
    }

    @Override
    public void updateTheme() {
        View rootView = getView();
        if (rootView != null) {
            rootView.findViewById(R.id.toolbar_bottom).setBackgroundColor(Theme.getPrimaryThemeColor());
        }
    }

    /**
     * Sets up the toolbar for the fragment.
     *
     * @param rootView root view of the fragment
     */
    private void setupToolbar(View rootView) {
        Toolbar toolbarBottom = (Toolbar) rootView.findViewById(R.id.toolbar_bottom);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save:
                        mEditTextOpponentName.clearFocus();
                        mEditTextOpponentScore.clearFocus();
                        DisplayUtils.hideKeyboard(getActivity());
                        if (mFinishedLoadingResults)
                            saveMatchPlayResults();
                        else
                            Toast.makeText(getContext(), R.string.text_not_loaded, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_cancel:
                        getActivity().onBackPressed();
                        break;
                    default:
                        // does nothing
                }
                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbarBottom.inflateMenu(R.menu.menu_match_play);
    }

    /**
     * Checks if the user's input for a name and score is valid.
     *
     * @param opponentName name input by user for opponent
     * @param opponentScore score input by user for opponent
     * @return {@code true} if the score and name are valid, {@code false} otherwise
     */
    private boolean isInputValid(String opponentName, String opponentScore) {
        int invalidInputMessage = -1;

        short scoreConverted;
        if (!TextUtils.isEmpty(opponentScore)) {
            try {
                scoreConverted = Short.parseShort(opponentScore);
                if (scoreConverted < 0 || scoreConverted > Constants.GAME_MAX_SCORE)
                    throw new NumberFormatException("Not a valid 5 pin score.");
            } catch (NumberFormatException ex) {
                invalidInputMessage = R.string.dialog_score_invalid;
            }
        }

        if (!TextUtils.isEmpty(opponentName) && !opponentName.matches(Constants.REGEX_NAME)) {
            //Name is not made up of letters and spaces
            invalidInputMessage = R.string.dialog_name_letters_spaces;
        }

        /*
         * If the input was invalid for any reason, a dialog is shown
         * to the user and the method does not continue
         */
        if (invalidInputMessage != -1) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.text_save_failure)
                    .setMessage(invalidInputMessage)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    /**
     * Saves the user's input for the match play results.
     */
    private void saveMatchPlayResults() {
        if (!isInputValid(mEditTextOpponentName.getText().toString(), mEditTextOpponentScore.getText().toString()))
            return;

        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.addSavingThread(new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = DatabaseHelper.getInstance(mainActivity).getWritableDatabase();

                boolean saveSuccessful = true;
                database.beginTransaction();
                try {
                    String[] whereArgs = {String.valueOf(mGameId)};
                    ContentValues values = new ContentValues();
                    values.put(GameEntry.COLUMN_MATCH_PLAY, getSelectedMatchPlayRadioButton());
                    database.update(GameEntry.TABLE_NAME, values, GameEntry._ID + "=?", whereArgs);

                    String opponentName = mEditTextOpponentName.getText().toString();
                    String opponentScore = mEditTextOpponentScore.getText().toString();

                    values = new ContentValues();
                    values.put(MatchPlayEntry.COLUMN_GAME_ID, mGameId);
                    if (!TextUtils.isEmpty(opponentName))
                        values.put(MatchPlayEntry.COLUMN_OPPONENT_NAME, opponentName);
                    if (!TextUtils.isEmpty(opponentScore))
                        values.put(MatchPlayEntry.COLUMN_OPPONENT_SCORE, Short.parseShort(opponentScore));
                    else
                        values.put(MatchPlayEntry.COLUMN_OPPONENT_SCORE, 0);

                    /*
                     * Due to the way this method was originally implemented, when match play results were updated,
                     * often the wrong row in the table was altered. This bug prevented users from saving match play
                     * results under certain circumstances. This has been fixed, but the old data cannot be safely
                     * removed all at once, without potentially deleting some of the user's real data. As a fix, when
                     * a user now saves match play results, any old results for *only that game* are deleted, and the
                     * new results are inserted, as seen below.
                     */
                    database.delete(MatchPlayEntry.TABLE_NAME, MatchPlayEntry.COLUMN_GAME_ID + "=?", whereArgs);
                    long result = database.insert(MatchPlayEntry.TABLE_NAME, null, values);
                    if (result == -1)
                        throw new Exception("Failed to insert values into database.");

                    database.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.e(TAG, "Error saving match results.", ex);
                    saveSuccessful = false;
                } finally {
                    database.endTransaction();
                }

                final int toastMessage = (saveSuccessful)
                        ? R.string.text_results_saved
                        : R.string.text_save_failure;
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    /**
     * Gets the match play results set by the user depending on the radio button they selected in {@code
     * mRadioGroupMatchResult}.
     *
     * @return one of {@code Constants.MATCH_PLAY_WON}, {@code Constants.MATCH_PLAY_LOST}, {@code
     * Constants.MATCH_PLAY_TIED}, or {@code Constants.MATCH_PLAY_NONE}
     */
    private int getSelectedMatchPlayRadioButton() {
        if (mRadioGroupMatchResult != null) {
            switch (mRadioGroupMatchResult.getCheckedRadioButtonId()) {
                case R.id.rb_result_won:
                    return Constants.MATCH_PLAY_WON;
                case R.id.rb_result_lost:
                    return Constants.MATCH_PLAY_LOST;
                case R.id.rb_result_tied:
                    return Constants.MATCH_PLAY_TIED;
                default:
                    return Constants.MATCH_PLAY_NONE;
            }
        }

        return Constants.MATCH_PLAY_NONE;
    }

    /**
     * Creates a new instance of this fragment and passes the parameters as arguments.
     *
     * @param gameId game id to set match play results of
     * @return a new {@code MatchPlayFragment} instance
     */
    public static MatchPlayFragment newInstance(long gameId) {
        MatchPlayFragment fragment = new MatchPlayFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.EXTRA_ID_GAME, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads the match play statistics for the game so they can be viewed and altered.
     */
    private static final class LoadMatchPlayTask
            extends AsyncTask<Long, Void, SparseArray<Object>> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<MatchPlayFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadMatchPlayTask(MatchPlayFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected SparseArray<Object> doInBackground(Long... gameId) {
            MatchPlayFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded() || fragment.getActivity() == null)
                return null;

            MainActivity.waitForSaveThreads(new WeakReference<>((MainActivity) fragment.getActivity()));

            SparseArray<Object> result = new SparseArray<>();
            int gameNumber = -1;
            int matchResult = -1;
            short gameScore = -1;
            String[] rawArgs = {String.valueOf(gameId[0])};
            String rawMatchQuery = "SELECT "
                    + GameEntry.COLUMN_GAME_NUMBER + ", "
                    + GameEntry.COLUMN_MATCH_PLAY + ", "
                    + GameEntry.COLUMN_SCORE
                    + " FROM " + GameEntry.TABLE_NAME
                    + " WHERE " + GameEntry._ID + "=?";
            SQLiteDatabase database = DatabaseHelper.getInstance(fragment.getContext()).getReadableDatabase();
            Cursor cursor = database.rawQuery(rawMatchQuery, rawArgs);
            if (cursor.moveToFirst()) {
                gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER));
                gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                matchResult = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));
            }
            cursor.close();

            String opponentName = null;
            short opponentScore;
            rawMatchQuery = "SELECT "
                    + MatchPlayEntry.COLUMN_OPPONENT_NAME + ", "
                    + MatchPlayEntry.COLUMN_OPPONENT_SCORE
                    + " FROM " + MatchPlayEntry.TABLE_NAME
                    + " WHERE " + MatchPlayEntry.COLUMN_GAME_ID + "=?";
            cursor = database.rawQuery(rawMatchQuery, rawArgs);
            if (cursor.moveToFirst()) {
                if (!cursor.isNull(cursor.getColumnIndex(MatchPlayEntry.COLUMN_OPPONENT_NAME)))
                    opponentName = cursor.getString(cursor.getColumnIndex(MatchPlayEntry.COLUMN_OPPONENT_NAME));
                opponentScore = cursor.getShort(cursor.getColumnIndex(MatchPlayEntry.COLUMN_OPPONENT_SCORE));
            } else {
                opponentName = null;
                opponentScore = 0;
            }

            if (gameNumber == -1 || matchResult == -1)
                return null;

            if (opponentName != null)
                result.put(MatchPlayData.OpponentName.ordinal(), opponentName);
            result.put(MatchPlayData.Score.ordinal(), gameScore);
            result.put(MatchPlayData.OpponentScore.ordinal(), opponentScore);
            result.put(MatchPlayData.GameNumber.ordinal(), gameNumber);
            result.put(MatchPlayData.Result.ordinal(), matchResult);
            return result;
        }

        @Override
        protected void onPostExecute(SparseArray<Object> result) {
            MatchPlayFragment fragment = mFragment.get();
            if (result == null || fragment == null)
                return;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return;

            fragment.mTextViewBowler.setText(mainActivity.getBowlerName());
            fragment.mTextViewLeagueEvent.setText(mainActivity.getLeagueName().substring(1));
            fragment.mTextViewDate.setText(mainActivity.getSeriesDate());

            int gameNumber = (int) result.get(MatchPlayData.GameNumber.ordinal());
            short gameScore = (short) result.get(MatchPlayData.Score.ordinal());
            fragment.mTextViewGameNumber.setText(Integer.toString(gameNumber));
            fragment.mTextViewScore.setText(Short.toString(gameScore));

            if (!fragment.mFromSavedInstanceState) {
                String opponentName = result.get(MatchPlayData.OpponentName.ordinal(), "").toString();
                short opponentScore = (short) result.get(MatchPlayData.OpponentScore.ordinal());

                if (opponentName.length() > 0)
                    fragment.mEditTextOpponentName.setText(opponentName);
                if (opponentScore > 0)
                    fragment.mEditTextOpponentScore.setText(Short.toString(opponentScore));
                Log.d(TAG, "Match play: " + (int) result.get(MatchPlayData.Result.ordinal()));
                switch ((int) result.get(MatchPlayData.Result.ordinal())) {
                    case Constants.MATCH_PLAY_NONE:
                        fragment.mRadioGroupMatchResult.check(R.id.rb_result_none);
                        break;
                    case Constants.MATCH_PLAY_WON:
                        fragment.mRadioGroupMatchResult.check(R.id.rb_result_won);
                        break;
                    case Constants.MATCH_PLAY_LOST:
                        fragment.mRadioGroupMatchResult.check(R.id.rb_result_lost);
                        break;
                    case Constants.MATCH_PLAY_TIED:
                        fragment.mRadioGroupMatchResult.check(R.id.rb_result_tied);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid match play results: " + (int) result.get(MatchPlayData.Result.ordinal()));
                }
            }
            fragment.mFinishedLoadingResults = true;
        }
    }

    /**
     * Data of the results of a game's match play.
     */
    private enum MatchPlayData {
        /** Number of the game in the series. */
        GameNumber,
        /** User's score. */
        Score,
        /** Name of the opponent. */
        OpponentName,
        /** Score of the opponent. */
        OpponentScore,
        /** Match play result. */
        Result
    }
}
