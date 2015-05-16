package ca.josephroque.bowlingcompanion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ManualScoreDialog;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.utilities.Score;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.ShareUtils;

/**
 * Created by Joseph Roque on 15-03-18.
 * <p/>
 * Manages the UI to display information about the games being tracked by the application,
 * and offers a callback interface {@link GameFragment.GameFragmentCallbacks} for
 * handling interactions.
 */
public class GameFragment extends Fragment
    implements
        Theme.ChangeableTheme,
        ManualScoreDialog.ManualScoreDialogListener
{
    /** Integer which represents the background color of views in the activity */
    private int COLOR_BACKGROUND;
    /** Integer which represents the highlighted color of views in the activity */
    private int COLOR_HIGHLIGHT;

    /** Represents the OnClickListener for the frame TextView objects */
    private static final byte LISTENER_TEXT_FRAMES = 0;
    /** Represents the OnClickListener for the pin button objects */
    private static final byte LISTENER_PIN_BUTTONS = 1;
    /** Represents the OnClickListener for any other objects */
    private static final byte LISTENER_OTHER = 2;
    /** Represents the OnClickListener for the ball TextView objects */
    private static final byte LISTENER_TEXT_BALLS = 3;

    /** Ids which represent current games that are available to be edited */
    private long[] mGameIds;
    /** Ids which represent frames of current games */
    private long[] mFrameIds;

    /** The current game being edited */
    private byte mCurrentGame = 0;
    /** The current frame being edited */
    private byte mCurrentFrame = 0;
    /** The current ball being edited */
    private byte mCurrentBall = 0;
    /** Indicates which frames in the game have been accessed */
    private boolean[] mHasFrameBeenAccessed;
    /** Indicate whether a certain pin was knocked down after a certain ball in a certain frame */
    private boolean[][][] mPinState;
    /** Indicate whether a foul was invoked on a certain ball in a certain frame */
    private boolean[][] mFouls;
    /** Scores of the current games being edited */
    private short[] mGameScores;
    /** Scores of the current games being edited, with fouls considered */
    private short[] mGameScoresMinusFouls;
    /** Indicates if a game is locked or not */
    private boolean[] mGameLocked;
    /** Indicates if a game has a manual score set or not */
    private boolean[] mManualScoreSet;
    /** Indicates the results of match play for each game */
    private byte[] mMatchPlay;

    /** TextView which displays score on a certain ball in a certain frame */
    private TextView[][] mTextViewBallScores;
    /** TextView which displays whether a foul was invoked on a certain ball */
    private TextView[][] mTextViewFouls;
    /** TextView which displays total score (not considering fouls) in a certain frame */
    private TextView[] mTextViewFrames;
    /** TextView which displays final score of the game, with fouls considered */
    private TextView mTextViewFinalScore;
    /** Offer interaction methods, indicate state of pins in a frame */
    private ImageButton[] mImageButtonPins;
    /** Displays TextView objects in a layout which user can interact with to access specific frame */
    private HorizontalScrollView mHorizontalScrollViewFrames;
    /** Displays text to user of option to lock a game */
    private ImageView mImageViewLock;
    /** Used to offer user method to knock down all pins in a frame */
    private ImageView mImageViewClear;
    /** Displays image to user of option to enabled or disable a foul */
    private ImageView mImageViewFoul;
    /** Displays image to user of option to reset a frame */
    private ImageView mImageViewResetFrame;
    /** Displays image to user of option to set match play results */
    private ImageView mImageViewMatchPlay;
    /** Displays manually set score */
    private TextView mTextViewManualScore;
    /** Layout which contains views related to general game options */
    private RelativeLayout mRelativeLayoutGameToolbar;

    /** Displays text to indicate navigation to next ball */
    private TextView mTextViewNextBall;
    /** Displays text to indicate navigation to previous ball */
    private TextView mTextViewPrevBall;
    /** Displays image to indicate navigation to next ball */
    private ImageView mImageViewNextBall;
    /** Displays image to indicate navigation to previous ball */
    private ImageView mImageViewPrevBall;
    /** Displays the current game number being viewed */
    private TextView mTextViewGameNumber;

    /** Indicates if the app should not save games - set to true in case of errors */
    private AtomicBoolean doNotSave = new AtomicBoolean(false);

    private boolean mAutoAdvanceEnabled;
    private int mAutoAdvanceDelay;

    /** Instance of callback interface for handling user events */
    private GameFragmentCallbacks mCallback;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        /*
         * This makes sure the container Activity has implemented
         * the callback interface. If not, an exception is thrown
         */
        try
        {
            mCallback = (GameFragmentCallbacks)activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement GameFragmentCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Loads member variables from saved instance state, if one exists
        if (savedInstanceState != null)
        {
            mCurrentGame = savedInstanceState.getByte(Constants.EXTRA_CURRENT_GAME);
            mGameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
            mMatchPlay = savedInstanceState.getByteArray(Constants.EXTRA_ARRAY_MATCH_PLAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        //Density of screen to set proper width/height of views
        final float screenDensity = getResources().getDisplayMetrics().density;

        mHorizontalScrollViewFrames = (HorizontalScrollView)rootView.findViewById(R.id.hsv_frames);

        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams layoutParams;
        mTextViewBallScores = new TextView[Constants.NUMBER_OF_FRAMES][3];
        mTextViewFouls = new TextView[Constants.NUMBER_OF_FRAMES][3];
        mTextViewFrames = new TextView[Constants.NUMBER_OF_FRAMES];
        mPinState = new boolean[Constants.NUMBER_OF_FRAMES][3][5];
        mFouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
        mHasFrameBeenAccessed = new boolean[Constants.NUMBER_OF_FRAMES];

        final View.OnClickListener[] onClickListeners = getOnClickListeners();

        /*
         * Following creates TextView objects to display information about state of game and
         * stores references in member variables
         */

        //Calculates most static view sizes beforehand so they don't have to be recalculated
        final int dp_128 = DataFormatter.getPixelsFromDP(screenDensity, 128);
        final int dp_120 = DataFormatter.getPixelsFromDP(screenDensity, 120);
        final int dp_88 = DataFormatter.getPixelsFromDP(screenDensity, 88);
        final int dp_40 = DataFormatter.getPixelsFromDP(screenDensity, 40);
        final int dp_41 = DataFormatter.getPixelsFromDP(screenDensity, 41);
        final int dp_20 = DataFormatter.getPixelsFromDP(screenDensity, 20);
        final int dp_36 = DataFormatter.getPixelsFromDP(screenDensity, 36);
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            //TextView to display score of a frame
            TextView frameText = new TextView(getActivity());
            switch(i)
            {
                //Id is set so when view is clicked, it can be identified
                case 0: frameText.setId(R.id.text_frame_0); break;
                case 1: frameText.setId(R.id.text_frame_1); break;
                case 2: frameText.setId(R.id.text_frame_2); break;
                case 3: frameText.setId(R.id.text_frame_3); break;
                case 4: frameText.setId(R.id.text_frame_4); break;
                case 5: frameText.setId(R.id.text_frame_5); break;
                case 6: frameText.setId(R.id.text_frame_6); break;
                case 7: frameText.setId(R.id.text_frame_7); break;
                case 8: frameText.setId(R.id.text_frame_8); break;
                case 9: frameText.setId(R.id.text_frame_9); break;
            }
            frameText.setBackgroundResource(R.drawable.background_frame_text);
            frameText.setGravity(Gravity.CENTER);
            frameText.setOnClickListener(onClickListeners[LISTENER_TEXT_FRAMES]);

            layoutParams = new RelativeLayout.LayoutParams(dp_120, dp_88);
            layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i);
            layoutParams.topMargin = dp_40;
            relativeLayout.addView(frameText, layoutParams);
            mTextViewFrames[i] = frameText;

            for (int j = 0; j < 3; j++)
            {
                //TextView to display value scored on a certain ball
                TextView text = new TextView(getActivity());
                text.setBackgroundResource(R.drawable.background_frame_text);
                text.setGravity(Gravity.CENTER);
                layoutParams = new RelativeLayout.LayoutParams(dp_40, dp_41);
                layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i + j * 40);
                relativeLayout.addView(text, layoutParams);
                mTextViewBallScores[i][j] = text;
                text.setOnClickListener(onClickListeners[LISTENER_TEXT_BALLS]);

                //TextView to display fouls invoked on a certain ball
                text = new TextView(getActivity());
                text.setGravity(Gravity.CENTER);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                layoutParams = new RelativeLayout.LayoutParams(dp_40, dp_20);
                layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i + j * 40);
                layoutParams.topMargin = dp_40;
                relativeLayout.addView(text, layoutParams);
                mTextViewFouls[i][j] = text;
            }

            //TextView to display frame number under related frame information
            frameText = new TextView(getActivity());
            frameText.setText(String.valueOf(i + 1));
            frameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            frameText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            frameText.setTextColor(getResources().getColor(android.R.color.white));
            layoutParams = new RelativeLayout.LayoutParams(dp_120, dp_36);
            layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i);
            layoutParams.addRule(RelativeLayout.BELOW, mTextViewFrames[i].getId());
            relativeLayout.addView(frameText, layoutParams);
        }

        //TextView to display final score of game
        mTextViewFinalScore = new TextView(getActivity());
        mTextViewFinalScore.setGravity(Gravity.CENTER);
        mTextViewFinalScore.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
        mTextViewFinalScore.setBackgroundResource(R.drawable.background_frame_text);
        layoutParams = new RelativeLayout.LayoutParams(dp_120, dp_128);
        layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, Constants.NUMBER_OF_FRAMES * 120);
        relativeLayout.addView(mTextViewFinalScore, layoutParams);
        mHorizontalScrollViewFrames.addView(relativeLayout);

        //Buttons which indicate state of pins in a frame, provide user interaction methods
        mImageButtonPins = new ImageButton[5];
        mImageButtonPins[0] = (ImageButton)rootView.findViewById(R.id.button_pin_1);
        mImageButtonPins[1] = (ImageButton)rootView.findViewById(R.id.button_pin_2);
        mImageButtonPins[2] = (ImageButton)rootView.findViewById(R.id.button_pin_3);
        mImageButtonPins[3] = (ImageButton)rootView.findViewById(R.id.button_pin_4);
        mImageButtonPins[4] = (ImageButton)rootView.findViewById(R.id.button_pin_5);

        for (ImageButton pinButton : mImageButtonPins)
            pinButton.setOnClickListener(onClickListeners[LISTENER_PIN_BUTTONS]);

        //Loading other views into member variables, setting OnClickListeners
        mImageViewNextBall = (ImageView)rootView.findViewById(R.id.iv_next_ball);
        mImageViewNextBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewPrevBall = (ImageView)rootView.findViewById(R.id.iv_prev_ball);
        mImageViewPrevBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewNextBall = (TextView)rootView.findViewById(R.id.tv_next_ball);
        mTextViewNextBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewPrevBall = (TextView)rootView.findViewById(R.id.tv_prev_ball);
        mTextViewPrevBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewClear = (ImageView)rootView.findViewById(R.id.iv_clear);
        mImageViewClear.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mTextViewGameNumber = (TextView)rootView.findViewById(R.id.tv_game_number);
        layoutParams = (RelativeLayout.LayoutParams)mTextViewGameNumber.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewNextBall.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewNextBall.getId());

        mImageViewFoul = (ImageView)rootView.findViewById(R.id.iv_foul);
        mImageViewFoul.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewResetFrame = (ImageView)rootView.findViewById(R.id.iv_reset_frame);
        mImageViewResetFrame.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewLock = (ImageView)rootView.findViewById(R.id.iv_lock);
        mImageViewLock.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewMatchPlay = (ImageView)rootView.findViewById(R.id.iv_match);
        mImageViewMatchPlay.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mTextViewManualScore = (TextView)rootView.findViewById(R.id.tv_manual_score);
        mRelativeLayoutGameToolbar = (RelativeLayout)rootView.findViewById(R.id.rl_game_toolbar);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle(R.string.title_fragment_game, true);

        //Loads colors for frame view backgrounds
        COLOR_BACKGROUND = getResources().getColor(R.color.primary_background);
        COLOR_HIGHLIGHT = getResources().getColor(R.color.secondary_background);

        //If values were not loaded from saved instance state, they are loaded here
        if (mGameIds == null)
        {
            Bundle args = getArguments();
            mGameIds = args.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = args.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = args.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = args.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
            mMatchPlay = args.getByteArray(Constants.EXTRA_ARRAY_MATCH_PLAY);
        }

        mGameScores = new short[((MainActivity)getActivity()).getNumberOfGames()];
        mGameScoresMinusFouls = new short[((MainActivity)getActivity()).getNumberOfGames()];

        updateTheme();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAutoAdvanceEnabled = preferences.getBoolean(Constants.KEY_ENABLE_AUTO_ADVANCE, false);

        String strDelay = preferences.getString(Constants.KEY_AUTO_ADVANCE_TIME, "15 seconds");
        mAutoAdvanceDelay = (strDelay != null)
                ? Integer.valueOf(strDelay.substring(0, strDelay.indexOf(" ")))
                : 0;

        //Loads scores of games being edited from database
        loadInitialScores();
        //Loads first game to edit
        loadGameFromDatabase(mCurrentGame);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //Clears color changes to frames and saves the game being edited
        clearFrameColor();
        saveGame(false);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mCallback.stopAutoAdvanceTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Puts member variables into outState so they can be loaded back
        outState.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, mGameIds);
        outState.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, mFrameIds);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, mGameLocked);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, mManualScoreSet);
        outState.putByteArray(Constants.EXTRA_ARRAY_MATCH_PLAY, mMatchPlay);
        outState.putByte(Constants.EXTRA_CURRENT_GAME, mCurrentGame);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_game, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        //Sets names/visibility of menu items
        menu.findItem(R.id.action_series_stats)
                .setTitle(((MainActivity)getActivity()).isEventMode() ? R.string.action_event_stats : R.string.action_series_stats);
        menu.findItem(R.id.action_set_score)
                .setTitle((mManualScoreSet[mCurrentGame])
                        ? R.string.action_clear_score : R.string.action_set_score);

        boolean drawerOpen = ((MainActivity)getActivity()).isDrawerOpen();
        menu.findItem(R.id.action_stats).setVisible(!drawerOpen);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        menu.findItem(R.id.action_series_stats).setVisible(!drawerOpen);
        menu.findItem(R.id.action_reset_game).setVisible(!drawerOpen);
        menu.findItem(R.id.action_set_score).setVisible(!drawerOpen);

        menu.findItem(R.id.action_what_if).setVisible(!mManualScoreSet[mCurrentGame] && !drawerOpen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_share:
                //Prompts user to share or save an image of current series
                saveGame(false);
                MainActivity mainActivity = (MainActivity)getActivity();
                ShareUtils.showShareDialog(mainActivity, mainActivity.getSeriesId());
                return true;

            case R.id.action_set_score:
                //If a manual score is set, clear it
                //Otherwise, prompt for a new score from the user
                if (mManualScoreSet[mCurrentGame])
                    showClearManualScoreDialog();
                else
                    showManualScoreDialog();
                return true;

            case R.id.action_series_stats:
                //Displays all stats related to series of games
                mCallback.onSeriesStatsOpened();
                return true;

            case R.id.action_reset_game:
                //If the game is locked, it cannot be reset
                //Otherwise, prompts user to reset
                if (mGameLocked[mCurrentGame] && !mManualScoreSet[mCurrentGame])
                    showGameLockedDialog();
                else
                    showResetGameDialog();
                return true;

            case R.id.action_what_if:
                //Calculates possible score and displays
                showWhatIfDialog();
                return true;

            case R.id.action_stats:
                //Displays all stats related to current game
                mCallback.onGameStatsOpened(mGameIds[mCurrentGame], (byte)(mCurrentGame + 1));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme()
    {
        mRelativeLayoutGameToolbar.setBackgroundColor(Theme.getSecondaryThemeColor());
    }

    @Override
    public void onSetScore(short scoreToSet)
    {
        if (scoreToSet < 0 || scoreToSet > 450)
        {
            //If an invalid score is given, user is informed and method exists
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Invalid score!")
                    .setMessage(R.string.dialog_bad_score)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        //Clears state of game and sets the manual score of the game
        resetGame();
        setGameLocked(true);
        mManualScoreSet[mCurrentGame] = true;
        mGameScores[mCurrentGame] = scoreToSet;
        mGameScoresMinusFouls[mCurrentGame] = scoreToSet;
        clearAllText(false);
        getActivity().supportInvalidateOptionsMenu();
        saveGame(true);
    }

    /**
     * Creates instances of OnClickListener to listen to events created by
     * views in this activity
     *
     * @return OnClickListener instances which are applied to views in this activity
     */
    private View.OnClickListener[] getOnClickListeners()
    {
        View.OnClickListener[] listeners = new View.OnClickListener[4];
        listeners[LISTENER_TEXT_FRAMES] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                byte frameToSet = 0;
                switch(v.getId())
                {
                    case R.id.text_frame_9: frameToSet++;
                    case R.id.text_frame_8: frameToSet++;
                    case R.id.text_frame_7: frameToSet++;
                    case R.id.text_frame_6: frameToSet++;
                    case R.id.text_frame_5: frameToSet++;
                    case R.id.text_frame_4: frameToSet++;
                    case R.id.text_frame_3: frameToSet++;
                    case R.id.text_frame_2: frameToSet++;
                    case R.id.text_frame_1: frameToSet++;
                    case R.id.text_frame_0:
                        //Changes the current frame and updates the GUI
                        clearFrameColor();
                        mCurrentFrame = frameToSet;
                        mCurrentBall = 0;
                        byte frameUpdateCount = 0;
                        for (int i = mCurrentFrame; i >= 0; i--)
                        {
                            if (mHasFrameBeenAccessed[i])
                                break;
                            mHasFrameBeenAccessed[i] = true;
                            frameUpdateCount++;
                        }
                        setVisibilityOfNextAndPrevItems();
                        if (frameUpdateCount > 3)
                            updateAllBalls();
                        else
                            updateBalls(mCurrentFrame, (byte)0);
                        updateScore();
                        updateFrameColor(false);

                        //todo: stop timer
                        break;
                    default:
                        throw new RuntimeException("Invalid frame id");
                }
            }
        };

        listeners[LISTENER_PIN_BUTTONS] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                    return;
                byte ballToSet = 0;
                switch(v.getId())
                {
                    case R.id.button_pin_5: ballToSet++;
                    case R.id.button_pin_4: ballToSet++;
                    case R.id.button_pin_3: ballToSet++;
                    case R.id.button_pin_2: ballToSet++;
                    case R.id.button_pin_1:
                        alterPinState(ballToSet);

                        //TODO: start timer
                        break;
                    default:
                        throw new RuntimeException("Invalid pin button id");
                }
            }
        };

        listeners[LISTENER_OTHER] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int viewId = v.getId();

                switch(viewId)
                {
                    case R.id.iv_match:
                        if (mGameLocked[mCurrentGame])
                            showSetMatchPlayLockedDialog();
                        else
                            showSetMatchPlayDialog();

                        //todo: stop timer
                        break;

                    case R.id.iv_lock:
                        //Locks the game if it is unlocked,
                        //unlocks if locked
                        if (mManualScoreSet[mCurrentGame])
                            return;
                        setGameLocked(!mGameLocked[mCurrentGame]);
                        //todo: stop timer
                        break;

                    case R.id.iv_foul:
                        //Sets or removes a foul and updates scores
                        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        mFouls[mCurrentFrame][mCurrentBall] = !mFouls[mCurrentFrame][mCurrentBall];
                        updateFouls();
                        //todo: stop timer
                        break;

                    case R.id.iv_reset_frame:
                        //Resets the current frame to ball 0, no fouls, no pins knocked
                        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        clearFrameColor();
                        mCurrentBall = 0;
                        for (int i = 0; i < 3; i++)
                        {
                            mFouls[mCurrentFrame][i] = false;
                            for (int j = 0; j < 5; j++)
                                mPinState[mCurrentFrame][i][j] = false;
                        }
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
                        updateBalls(mCurrentFrame, (byte)0);
                        updateScore();
                        //todo: stop timer
                        break;

                    case R.id.iv_clear:
                        clearPins();
                        //todo: stop timer
                        break;

                    case R.id.iv_next_ball:
                    case R.id.tv_next_ball:
                        //Changes the current frame and updates the GUI
                        if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2)
                            return;

                        clearFrameColor();
                        if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
                        {
                            if (mCurrentFrame < Constants.LAST_FRAME)
                            {
                                mCurrentBall = 0;
                                mCurrentFrame++;
                            }
                            else if (mCurrentBall < 2)
                            {
                                mCurrentBall++;
                            }
                        }
                        else if (++mCurrentBall == 3)
                        {
                            mCurrentBall = 0;
                            ++mCurrentFrame;
                        }
                        mHasFrameBeenAccessed[mCurrentFrame] = true;
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);

                        //todo: stop timer
                        break;

                    case R.id.iv_prev_ball:
                    case R.id.tv_prev_ball:
                        //Changes the current frame and updates the GUI
                        if (mCurrentFrame == 0 && mCurrentBall == 0)
                            return;

                        clearFrameColor();
                        if (--mCurrentBall == -1)
                        {
                            mCurrentBall = 0;
                            --mCurrentFrame;
                            while(!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN) && mCurrentBall < 2)
                            {
                                mCurrentBall++;
                            }
                        }
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);

                        //todo: stop timer
                        break;

                    default:
                        throw new RuntimeException("Unknown other button id");
                }
            }
        };

        listeners[LISTENER_TEXT_BALLS] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean viewFound = false;
                for (byte i = 0; i < mTextViewBallScores.length && !viewFound; i++)
                {
                    for (byte j = 0; j < mTextViewBallScores[i].length; j++)
                    {
                        if (v == mTextViewBallScores[i][j])
                        {
                            viewFound = true;

                            //Changes the current frame and updates the GUI
                            clearFrameColor();
                            mCurrentFrame = i;
                            mCurrentBall = 0;
                            byte frameUpdateCount = 0;
                            for (int k = mCurrentFrame; k >= 0; k--)
                            {
                                if (mHasFrameBeenAccessed[k])
                                    break;
                                mHasFrameBeenAccessed[k] = true;
                                frameUpdateCount++;
                            }
                            while(!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN) && mCurrentBall < j)
                            {
                                mCurrentBall++;
                            }
                            setVisibilityOfNextAndPrevItems();
                            if (frameUpdateCount > 3)
                                updateAllBalls();
                            else
                                updateBalls(mCurrentFrame, (byte)0);
                            updateScore();
                            updateFrameColor(false);
                            break;
                        }
                    }
                }
            }
        };

        return listeners;
    }

    /**
     * Displays prompt to user to inform them the game is locked. Included to prevent
     * accidental changes of match play results, but still allow changes if game is
     * locked (such as by a manual score being set)
     */
    private void showSetMatchPlayLockedDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle("Game is locked")
                .setMessage(R.string.dialog_match_locked)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        showSetMatchPlayDialog();
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
                })
                .create()
                .show();
    }

    /**
     * Prompts user to set results of match play for a game
     */
    private void showSetMatchPlayDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle("Set match play")
                .setSingleChoiceItems(new CharSequence[]{"None", "Won", "Lost", "Tied"}, mMatchPlay[mCurrentGame], null)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mMatchPlay[mCurrentGame] = (byte) ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        setMatchPlay();
                        saveGame(true);
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
                })
                .create()
                .show();
    }

    /**
     * Prompts user to reset the current game and set a manual score
     */
    private void showManualScoreDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set manual score?")
                .setMessage(R.string.dialog_set_score)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        DialogFragment dialogFragment = ManualScoreDialog.newInstance(GameFragment.this);
                        dialogFragment.show(getFragmentManager(), "ManualScoreDialog");
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts user to reset the current game and remove a manual score
     */
    private void showClearManualScoreDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Clear the set score?")
                .setMessage(R.string.dialog_clear_score)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Unlocks and resets game
                        setGameLocked(false);
                        mManualScoreSet[mCurrentGame] = false;
                        resetGame();
                        clearAllText(true);
                        updateScore();
                        updateAllBalls();
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
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
                })
                .create()
                .show();
    }

    /**
     * Hides or shows TextView objects in the app that display individual score elements
     * @param enabled if true, TextView objects related to scores will be shown
     */
    private void clearAllText(boolean enabled)
    {
        if (enabled)
        {
            mImageViewNextBall.setVisibility(View.VISIBLE);
            mImageViewPrevBall.setVisibility(View.VISIBLE);
            mTextViewNextBall.setVisibility(View.VISIBLE);
            mTextViewPrevBall.setVisibility(View.VISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.VISIBLE);
            for (ImageButton imageButton : mImageButtonPins)
                imageButton.setVisibility(View.VISIBLE);
            mTextViewManualScore.setText(null);
            mTextViewManualScore.setVisibility(View.INVISIBLE);
            mImageViewFoul.setVisibility(View.VISIBLE);
            mImageViewResetFrame.setVisibility(View.VISIBLE);
        }
        else
        {
            mImageViewNextBall.setVisibility(View.INVISIBLE);
            mImageViewPrevBall.setVisibility(View.INVISIBLE);
            mTextViewNextBall.setVisibility(View.INVISIBLE);
            mTextViewPrevBall.setVisibility(View.INVISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.INVISIBLE);
            for (ImageButton imageButton : mImageButtonPins)
                imageButton.setVisibility(View.INVISIBLE);
            mTextViewManualScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
            mTextViewManualScore.setVisibility(View.VISIBLE);
            mImageViewFoul.setVisibility(View.INVISIBLE);
            mImageViewResetFrame.setVisibility(View.INVISIBLE);
        }

        mImageViewLock.setImageResource((mGameLocked[mCurrentGame])
                ? R.drawable.ic_lock
                : R.drawable.ic_lock_open);
    }

    /**
     * Informs user with prompt that the game is current locked
     */
    private void showGameLockedDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle("Invalid action!")
                .setMessage(R.string.dialog_game_locked)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Prompts user to reset the current game
     */
    private void showResetGameDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle("Reset Game?")
                .setMessage(R.string.dialog_reset_game)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Resets and saves the current game
                        resetGame();
                        saveGame(true);
                        setGameLocked(false);
                        mMatchPlay[mCurrentGame] = 0;
                        mManualScoreSet[mCurrentGame] = false;
                        clearAllText(true);
                        updateScore();
                        updateAllBalls();
                        setMatchPlay();
                        getActivity().supportInvalidateOptionsMenu();
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
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
                })
                .create()
                .show();
    }

    /**
     * Sets mImageViewMatchPlay image resource depending on match play results set by user
     */
    private void setMatchPlay()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch(mMatchPlay[mCurrentGame])
                {
                    case 0: mImageViewMatchPlay.setImageResource(R.drawable.ic_match_none); break;
                    case 1: mImageViewMatchPlay.setImageResource(R.drawable.ic_match_win); break;
                    case 2: mImageViewMatchPlay.setImageResource(R.drawable.ic_match_lose); break;
                    case 3: mImageViewMatchPlay.setImageResource(R.drawable.ic_match_tie); break;
                }
            }
        });
    }

    /**
     * Locks or unlocks a game and hides/shows settings which are only necessary
     * if a game is unlocked
     * @param lock if true, settings will be hidden and game locked
     */
    private void setGameLocked(boolean lock)
    {
        mGameLocked[mCurrentGame] = lock;
        mImageViewLock.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mGameLocked[mCurrentGame])
                {
                    mImageViewFoul.setVisibility(View.INVISIBLE);
                    mImageViewResetFrame.setVisibility(View.INVISIBLE);
                    mImageViewClear.setVisibility(View.INVISIBLE);
                    mImageViewLock.setImageResource(R.drawable.ic_lock);
                }
                else
                {
                    mImageViewFoul.setVisibility(View.VISIBLE);
                    mImageViewResetFrame.setVisibility(View.VISIBLE);
                    mImageViewClear.setVisibility(View.VISIBLE);
                    mImageViewLock.setImageResource(R.drawable.ic_lock_open);
                }
            }
        });
    }

    /**
     * Copies data of current game to variables and saves game to the database
     * on a new thread
     * @param ignoreManualScore if false, game will only be saved if a manual score is
     *                          not set. Otherwise, will save regardless.
     */
    private void saveGame(boolean ignoreManualScore)
    {
        if ((!ignoreManualScore && mManualScoreSet[mCurrentGame]) || doNotSave.get())
            return;

        long[] framesToSave = new long[Constants.NUMBER_OF_FRAMES];
        boolean[] accessToSave = new boolean[Constants.NUMBER_OF_FRAMES];
        boolean[][][] pinStateToSave = new boolean[Constants.NUMBER_OF_FRAMES][3][5];
        boolean[][] foulsToSave = new boolean[Constants.NUMBER_OF_FRAMES][3];
        System.arraycopy(mFrameIds, mCurrentGame * Constants.NUMBER_OF_FRAMES, framesToSave, 0, Constants.NUMBER_OF_FRAMES);
        System.arraycopy(mHasFrameBeenAccessed, 0, accessToSave, 0, Constants.NUMBER_OF_FRAMES);
        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            for (byte j = 0; j < mPinState[i].length; j++)
            {
                System.arraycopy(mPinState[i][j], 0, pinStateToSave[i][j], 0, mPinState[i][j].length);
            }
            System.arraycopy(mFouls[i], 0, foulsToSave[i], 0, mFouls[i].length);
        }

        ((MainActivity)getActivity()).addSavingThread(
                saveGameToDatabase((MainActivity) getActivity(),
                        mGameIds[mCurrentGame],
                        framesToSave,
                        accessToSave,
                        pinStateToSave,
                        foulsToSave,
                        mGameScoresMinusFouls[mCurrentGame],
                        mGameLocked[mCurrentGame],
                        mManualScoreSet[mCurrentGame],
                        mMatchPlay[mCurrentGame]));
    }

    /**
     * Resets a game to its original state with 0 score, 0 fouls
     */
    private void resetGame()
    {
        clearFrameColor();
        mCurrentBall = 0;
        mCurrentFrame = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            mHasFrameBeenAccessed[i] = false;
            for (int j = 0; j < 3; j++)
            {
                mFouls[i][j] = false;
                for (int k = 0; k < 5; k++)
                    mPinState[i][j][k] = false;
            }
        }
        mHasFrameBeenAccessed[0] = true;
        mGameScores[mCurrentGame] = 0;
        mGameScoresMinusFouls[mCurrentGame] = 0;
    }

    /**
     * Calculates the highest score possible from the current state
     * of the game and displays it in a dialog to the user
     */
    private void showWhatIfDialog()
    {
        StringBuilder alertMessageBuilder = new StringBuilder("If you get");
        short possibleScore = Short.parseShort(mTextViewFrames[mCurrentFrame].getText().toString());

        if (mCurrentFrame < Constants.LAST_FRAME)
        {
            if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
            {
                int firstBallNextFrame = Score.getValueOfFrame(mPinState[mCurrentFrame + 1][0]);
                possibleScore -= firstBallNextFrame;
                if (firstBallNextFrame == 15)
                {
                    if (mCurrentFrame < Constants.LAST_FRAME - 1)
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 2][0]);
                    else
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 1][1]);
                }
                else
                    possibleScore -= Score.getValueOfFrameDifference(mPinState[mCurrentFrame][0], mPinState[mCurrentFrame][1]);
            }
            else if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
            {
                int firstBallNextFrame = Score.getValueOfFrame(mPinState[mCurrentFrame + 1][0]);
                possibleScore -= firstBallNextFrame;
            }
        }
        else
        {
            for (int i = mCurrentBall + 1; i < 3; i++)
                possibleScore -= Score.getValueOfFrameDifference(mPinState[mCurrentFrame][i - 1], mPinState[mCurrentFrame][i]);
        }

        int pinsLeftStanding = 0;
        for (int i = 0; i < 5; i++)
        {
            if (!mPinState[mCurrentFrame][mCurrentBall][i])
            {
                switch(i)
                {
                    case 0:case 4: pinsLeftStanding += 2; break;
                    case 1:case 3: pinsLeftStanding += 3; break;
                    case 2: pinsLeftStanding += 5; break;
                }
            }
        }

        boolean strikeLastFrame = false;
        boolean strikeTwoFramesAgo = false;
        boolean spareLastFrame = false;

        if (mCurrentFrame > 0)
        {
            if (Arrays.equals(mPinState[mCurrentFrame - 1][0], Constants.FRAME_PINS_DOWN))
            {
                strikeLastFrame = true;
                if (mCurrentFrame > 1 && Arrays.equals(mPinState[mCurrentFrame - 2][0], Constants.FRAME_PINS_DOWN))
                    strikeTwoFramesAgo = true;
            }
            else
            {
                if (Arrays.equals(mPinState[mCurrentFrame - 1][1], Constants.FRAME_PINS_DOWN))
                    spareLastFrame = true;
            }
        }

        if (mCurrentBall == 0)
        {
            alertMessageBuilder.append(" a strike");
            possibleScore += pinsLeftStanding + 30;
            int secondBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][0], mPinState[mCurrentFrame][1]);
            int thirdBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][1], mPinState[mCurrentFrame][2]);

            if (mCurrentFrame < Constants.LAST_FRAME)
                possibleScore -= secondBall + thirdBall;
            if (strikeLastFrame)
            {
                possibleScore -= secondBall;
                possibleScore += pinsLeftStanding + 15;
                if (strikeTwoFramesAgo)
                    possibleScore += pinsLeftStanding;
            }
            else if (spareLastFrame)
                possibleScore += pinsLeftStanding;
        }
        else if (mCurrentBall == 1)
        {
            int thirdBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][1], mPinState[mCurrentFrame][2]);
            if (mCurrentFrame < Constants.LAST_FRAME)
                possibleScore -= thirdBall;

            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" a spare");
            possibleScore += pinsLeftStanding + 15;
            if (strikeLastFrame)
                possibleScore += pinsLeftStanding;
        }
        else
        {
            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" fifteen");
            possibleScore += pinsLeftStanding;
        }
        possibleScore += 45 * (Constants.LAST_FRAME - mCurrentFrame);

        for (int i = 0; i <= mCurrentFrame; i++)
        {
            for (int j = 0; j < 3 && !(i == mCurrentFrame && j >= mCurrentBall); j++)
            {
                if (mFouls[i][j])
                    possibleScore -= 15;
            }
        }
        if (possibleScore < 0)
            possibleScore = 0;

        alertMessageBuilder.append(" this ball, and strikes onwards, your final score will be ");
        alertMessageBuilder.append(possibleScore);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessageBuilder.toString())
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        builder.create()
                .show();
    }

    /**
     * Sets the visibility of text and image views which indicate available navigation options
     * in the fragment, depending on whether they are available at the time
     */
    private void setVisibilityOfNextAndPrevItems()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void  run()
            {
                if (mCurrentFrame == 0 && mCurrentBall == 0)
                {
                    mTextViewPrevBall.setVisibility(View.GONE);
                    mImageViewPrevBall.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mTextViewGameNumber.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewNextBall.getId());
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewNextBall.getId());
                }
                else
                {
                    mTextViewPrevBall.setVisibility(View.VISIBLE);
                    mImageViewPrevBall.setVisibility(View.VISIBLE);
                }

                if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2)
                {
                    mTextViewNextBall.setVisibility(View.GONE);
                    mImageViewNextBall.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mTextViewGameNumber.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewPrevBall.getId());
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewPrevBall.getId());
                }
                else
                {
                    mTextViewNextBall.setVisibility(View.VISIBLE);
                    mImageViewNextBall.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Sets text of all TextView instances which display individual ball values
     */
    private void updateAllBalls()
    {
        for (byte i = Constants.LAST_FRAME; i >= 0; i -= 3)
            updateBalls(i, (byte)0);
    }

    /**
     * Sets the text of the three TextView instances which correspond to frameToUpdate
     *
     * @param frameToUpdate frame of which text should be updated
     */
    private void updateBalls(final byte frameToUpdate, final byte leftToUpdate)
    {
        if (frameToUpdate < 0 || frameToUpdate > Constants.LAST_FRAME)
            return;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Sets text depending on state of pins in the frame
                final String[] ballString = new String[3];
                if (mHasFrameBeenAccessed[frameToUpdate])
                {
                    if (frameToUpdate == Constants.LAST_FRAME) //Treat last frame differently than rest
                    {
                        if (Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                        {
                            //If first ball is a strike, next two can be strikes/spares
                            ballString[0] = Constants.BALL_STRIKE;
                            if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                            {
                                ballString[1] = Constants.BALL_STRIKE;
                                ballString[2] = Score.getValueOfBall(mPinState[frameToUpdate][2], 2, true, false);
                            }
                            else
                            {
                                ballString[1] = Score.getValueOfBall(mPinState[frameToUpdate][1], 1, false, false);
                                if (Arrays.equals(mPinState[frameToUpdate][2], Constants.FRAME_PINS_DOWN))
                                    ballString[2] = Constants.BALL_SPARE;
                                else
                                    ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false, false);
                            }
                        }
                        else
                        {
                            //If first ball is not a strike, score is calculated normally
                            ballString[0] = Score.getValueOfBall(mPinState[frameToUpdate][0], 0, false, false);
                            if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                            {
                                ballString[1] = Constants.BALL_SPARE;
                                ballString[2] = Score.getValueOfBall(mPinState[frameToUpdate][2], 2, true, true);
                            }
                            else
                            {
                                ballString[1] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 1, false, false);
                                ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false, false);
                            }
                        }
                    }
                    else
                    {
                        ballString[0] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 0, false, false);
                        if (!Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                        {
                            if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                            {
                                ballString[1] = Constants.BALL_SPARE;
                                ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 1])
                                        ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 0, false, true)
                                        : Constants.BALL_EMPTY;
                            }
                            else
                            {
                                ballString[1] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 1, false, false);
                                ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false, false);
                            }
                        }
                        else
                        {
                            //Either displays pins knocked down in next frames
                            //or shows empty frames
                            if (mHasFrameBeenAccessed[frameToUpdate + 1])
                            {
                                ballString[1] = Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 0, false, true);
                                if (Arrays.equals(mPinState[frameToUpdate + 1][0], Constants.FRAME_PINS_DOWN))
                                {
                                    if (frameToUpdate < Constants.LAST_FRAME - 1)
                                    {
                                        ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 2])
                                                ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 2], 0, false, true)
                                                : Constants.BALL_EMPTY;
                                    }
                                    else
                                    {
                                        ballString[2] = Score.getValueOfBall(mPinState[frameToUpdate + 1][1], 1, false, true);
                                    }
                                }
                                else
                                {
                                    ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 1, false, true);
                                }
                            }
                            else
                            {
                                ballString[1] = Constants.BALL_EMPTY;
                                ballString[2] = Constants.BALL_EMPTY;
                            }
                        }
                    }
                }
                else
                {
                    ballString[0] = ballString[1] = ballString[2] = "";
                }


                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (byte i = 0; i < 3; i++)
                        {
                            mTextViewBallScores[frameToUpdate][i].setText(ballString[i]);
                            mTextViewFouls[frameToUpdate][i].setText(
                                    (mFouls[frameToUpdate][i]) ? "F" : null);
                        }
                    }
                });

                //Updates previous frames as well, to display balls after strikes
                switch(leftToUpdate)
                {
                    case 0:
                        updateBalls((byte)(frameToUpdate - 1), (byte)(leftToUpdate + 1));
                        break;
                    case 1:
                        updateBalls((byte)(frameToUpdate - 1), (byte)(leftToUpdate + 1));
                        break;
                    default:
                        //do nothing
                }
            }
        }).start();
    }

    /**
     * Sets text of the TextView instances which display the score up to the frame to the user
     */
    private void updateScore()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Calculates and keeps running total of scores of each frame
                final short[] frameScores = new short[Constants.NUMBER_OF_FRAMES];
                for (byte f = Constants.LAST_FRAME; f >= 0; f--)
                {
                    if (f == Constants.LAST_FRAME)
                    {
                        for (byte b = 2; b >= 0; b--)
                        {
                            switch(b)
                            {
                                case 2:
                                    frameScores[f] += Score.getValueOfFrame(mPinState[f][b]);
                                    break;
                                case 1:
                                case 0:
                                    if (Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN))
                                    {
                                        frameScores[f] += Score.getValueOfFrame(mPinState[f][b]);
                                    }
                                    break;
                                default: //do nothing
                            }
                        }
                    }
                    else
                    {
                        for (byte b = 0; b < 3; b++)
                        {
                            if (b < 2 && Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN))
                            {
                                frameScores[f] += Score.getValueOfFrame(mPinState[f][b]);
                                frameScores[f] += Score.getValueOfFrame(mPinState[f + 1][0]);
                                if (b == 0)
                                {
                                    if (f == Constants.LAST_FRAME - 1)
                                    {
                                        if (frameScores[f] == 30)
                                        {
                                            frameScores[f] += Score.getValueOfFrame(mPinState[f + 1][1]);
                                        }
                                        else
                                        {
                                            frameScores[f] += Score.getValueOfFrameDifference(mPinState[f + 1][0], mPinState[f + 1][1]);
                                        }
                                    }
                                    else if (frameScores[f] < 30)
                                    {
                                        frameScores[f] += Score.getValueOfFrameDifference(mPinState[f + 1][0], mPinState[f + 1][1]);
                                    }
                                    else
                                    {
                                        frameScores[f] += Score.getValueOfFrame(mPinState[f + 2][0]);
                                    }
                                }
                                break;
                            }
                            else if (b == 2)
                            {
                                frameScores[f] += Score.getValueOfFrame(mPinState[f][b]);
                            }
                        }
                    }
                }

                short totalScore = 0;
                for (byte i = 0; i < frameScores.length; i++)
                {
                    totalScore += frameScores[i];
                    frameScores[i] = totalScore;
                }
                mGameScores[mCurrentGame] = totalScore;

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Sets scores calculated from running total as text of TextViews
                        for (byte i = 0; i < frameScores.length; i++)
                        {
                            mTextViewFrames[i].setText((mHasFrameBeenAccessed[i]
                                    ? String.valueOf(frameScores[i])
                                    : ""));
                        }
                    }
                });
                updateFouls();
            }
        }).start();
    }

    /**
     * Counts fouls of the frames and calculates scores minus 15 points
     * for each foul, then sets score in last TextView
     */
    private void updateFouls()
    {
        byte foulCount = 0;
        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (mFouls[i][j])
                    foulCount++;
            }
        }
        short scoreWithFouls = (short)(mGameScores[mCurrentGame] - 15 * foulCount);
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        mGameScoresMinusFouls[mCurrentGame] = scoreWithFouls;

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mTextViewFinalScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
                mImageViewFoul.setImageResource(
                        !mFouls[mCurrentFrame][mCurrentBall]
                                ? R.drawable.ic_foul_remove
                                : R.drawable.ic_foul);
                mTextViewFouls[mCurrentFrame][mCurrentBall]
                        .setText(mFouls[mCurrentFrame][mCurrentBall] ? "F" : "");
            }
        });
    }

    /**
     * Sets background color of current ball and frame TextView instances to COLOR_BACKGROUND
     */
    private void clearFrameColor()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GradientDrawable drawable = (GradientDrawable)
                        mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(COLOR_BACKGROUND);
                drawable = (GradientDrawable)
                        mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(COLOR_BACKGROUND);
            }
        });
    }

    /**
     * Sets background color of current ball and frame TextView instances to COLOR_HIGHLIGHT
     * and sets color of pin and whether its enabled or not depending on its state
     * @param initialLoad indicates if this method was called when a game was first loaded
     */
    private void updateFrameColor(final boolean initialLoad)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GradientDrawable drawable = (GradientDrawable)
                        mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(COLOR_HIGHLIGHT);
                drawable = (GradientDrawable)
                        mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(COLOR_HIGHLIGHT);

                //Sets images of pins to enabled/disabled depending on
                //if they were knocked down in current frame or a previous one
                int numberOfPinsStanding = 0;
                for (byte i = 0; i < 5; i++)
                {
                    if (mPinState[mCurrentFrame][mCurrentBall][i])
                    {
                        mImageButtonPins[i].setImageResource(R.drawable.pin_disabled);
                    } else
                    {
                        mImageButtonPins[i].setImageResource(R.drawable.pin_enabled);
                        numberOfPinsStanding++;
                    }

                    if (mCurrentBall > 0 && (mPinState[mCurrentFrame][mCurrentBall - 1][i])
                            && !(mCurrentFrame == Constants.LAST_FRAME
                            && Arrays.equals(mPinState[mCurrentFrame][mCurrentBall - 1], Constants.FRAME_PINS_DOWN)))
                    {
                        mImageButtonPins[i].setEnabled(false);
                    } else
                    {
                        mImageButtonPins[i].setEnabled(true);
                    }
                }

                if (mCurrentFrame == Constants.LAST_FRAME)
                {
                    switch (mCurrentBall)
                    {
                        case 0:
                            mImageViewClear.setImageResource(R.drawable.ic_strike);
                            break;
                        case 1:
                            if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                                mImageViewClear.setImageResource(R.drawable.ic_strike);
                            else
                                mImageViewClear.setImageResource(R.drawable.ic_spare);
                            break;
                        case 2:
                            if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
                                mImageViewClear.setImageResource(R.drawable.ic_strike);
                            else if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                                mImageViewClear.setImageResource(R.drawable.ic_spare);
                            else
                                mImageViewClear.setImageResource(R.drawable.ic_fifteen);
                            break;
                    }
                } else
                {
                    switch (mCurrentBall)
                    {
                        case 0:
                            mImageViewClear.setImageResource(R.drawable.ic_strike);
                            break;
                        case 1:
                            mImageViewClear.setImageResource(R.drawable.ic_spare);
                            break;
                        case 2:
                            mImageViewClear.setImageResource(R.drawable.ic_fifteen);
                            break;
                    }
                }
                mImageViewClear.setEnabled(numberOfPinsStanding > 0);

                mImageViewFoul.setImageResource(
                        !mFouls[mCurrentFrame][mCurrentBall]
                        ? R.drawable.ic_foul_remove
                        : R.drawable.ic_foul);

                focusOnFrame(initialLoad);
            }
        });
    }

    /**
     * Either sets a pin to be standing or knocked down, and updates the score accordingly
     *
     * @param pinToSet the pin which was altered
     */
    private void alterPinState(final byte pinToSet)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final boolean isPinKnockedOver = mPinState[mCurrentFrame][mCurrentBall][pinToSet];
                final boolean allPinsKnockedOver;
                if (!isPinKnockedOver)
                {
                    for (int i = mCurrentBall; i < 3; i++)
                        mPinState[mCurrentFrame][i][pinToSet] = true;

                    if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
                    {
                        for (int i = mCurrentBall + 1; i < 3; i++)
                            mFouls[mCurrentFrame][i] = false;

                        if (mCurrentFrame == Constants.LAST_FRAME)
                        {
                            if (mCurrentBall < 2)
                            {
                                for (int j = mCurrentBall + 1; j < 3; j++)
                                {
                                    for (int i = 0; i < 5; i++)
                                        mPinState[mCurrentFrame][j][i] = false;
                                }
                            }
                        }
                        allPinsKnockedOver = true;
                    }
                    else
                        allPinsKnockedOver = false;
                }
                else
                {
                    allPinsKnockedOver = false;
                    for (int i = mCurrentBall; i < 3; i++)
                        mPinState[mCurrentFrame][i][pinToSet] = false;

                    if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 1)
                        System.arraycopy(mPinState[mCurrentFrame][1], 0, mPinState[mCurrentFrame][2], 0, mPinState[mCurrentFrame][1].length);
                }

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mPinState[mCurrentFrame][mCurrentBall][pinToSet])
                        {
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin_disabled);
                        }
                        else
                        {
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin_enabled);
                        }
                        mImageViewClear.setEnabled(!allPinsKnockedOver);
                    }
                });

                updateBalls(mCurrentFrame, (byte)0);
                updateScore();
            }
        }).start();
    }

    /**
     * Clears all the pins which are currently standing in the frame and updates
     * the TextViews with new score
     */
    private void clearPins()
    {
        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
            return;

        if (!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
        {
            for (int j = mCurrentBall; j < 3; j++)
            {
                for (int i = 0; i < 5; i++)
                {
                    mPinState[mCurrentFrame][j][i] = (mCurrentFrame != Constants.LAST_FRAME || (j == mCurrentBall));
                    if (j > mCurrentBall)
                        mFouls[mCurrentFrame][j] = false;
                }
            }

            updateBalls(mCurrentFrame, (byte)0);
            updateScore();
            updateFrameColor(false);
        }
    }

    /**
     * Scrolls the position of hsvFrames so the current frame is centred
     * @param initialLoad indicates if this method was called when a game was first loaded
     */
    private void focusOnFrame(final boolean initialLoad)
    {
        mHorizontalScrollViewFrames.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (initialLoad && mCurrentFrame == Constants.LAST_FRAME)
                    mHorizontalScrollViewFrames.smoothScrollTo(mTextViewFrames[mCurrentFrame].getLeft(), 0);
                else if (mCurrentFrame >= 1)
                    mHorizontalScrollViewFrames.smoothScrollTo(mTextViewFrames[mCurrentFrame - 1].getLeft(), 0);
                else
                    mHorizontalScrollViewFrames.smoothScrollTo(mTextViewFrames[mCurrentFrame].getLeft(), 0);
            }
        });
    }

    /**
     * Saves a games score and individual frames to the database on a separate thread.
     *
     * @param srcActivity activity which called the method to get instance of database
     * @param gameId id of the game to be updated
     * @param frameIds ids of the frames to be updated
     * @param hasFrameBeenAccessed state of whether frames have been accessed or not
     * @param pinState state of pins after each ball
     * @param fouls indicates whether a foul was invoked on each ball
     * @param finalScore final score of the game, considering fouls
     * @param gameLocked indicates if the game is locked
     * @param manualScoreSet indicates if a manual score for the game is set
     * @param matchPlay indicates the result of match play for the game
     * @return a new thread which is saving a game. Thread is not started
     */
    private static Thread saveGameToDatabase(
            final MainActivity srcActivity,
            final long gameId,
            final long[] frameIds,
            final boolean[] hasFrameBeenAccessed,
            final boolean[][][] pinState,
            final boolean[][] fouls,
            final short finalScore,
            final boolean gameLocked,
            final boolean manualScoreSet,
            final byte matchPlay)
    {
        return new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(srcActivity).getWritableDatabase();
                ContentValues values;

                database.beginTransaction();
                try
                {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_SCORE, finalScore);
                    values.put(GameEntry.COLUMN_IS_LOCKED, (gameLocked ? 1:0));
                    values.put(GameEntry.COLUMN_IS_MANUAL, (manualScoreSet) ? 1:0);
                    values.put(GameEntry.COLUMN_MATCH_PLAY, matchPlay);
                    database.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{String.valueOf(gameId)});

                    for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                    {
                        StringBuilder foulsOfFrame = new StringBuilder();
                        for (int ballCount = 0; ballCount < 3; ballCount++)
                        {
                            if (fouls[i][ballCount])
                                foulsOfFrame.append(ballCount + 1);
                        }
                        if (foulsOfFrame.length() == 0)
                            foulsOfFrame.append(0);

                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_PIN_STATE[0], Score.booleanFrameToString(pinState[i][0]));
                        values.put(FrameEntry.COLUMN_PIN_STATE[1], Score.booleanFrameToString(pinState[i][1]));
                        values.put(FrameEntry.COLUMN_PIN_STATE[2], Score.booleanFrameToString(pinState[i][2]));
                        values.put(FrameEntry.COLUMN_IS_ACCESSED, (hasFrameBeenAccessed[i]) ? 1:0);
                        values.put(FrameEntry.COLUMN_FOULS, foulsOfFrame.toString());
                        database.update(FrameEntry.TABLE_NAME,
                                values,
                                FrameEntry._ID + "=?",
                                new String[]{String.valueOf(frameIds[i])});
                    }
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    throw new RuntimeException("Fatal error: Game could not save. " + ex.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }
            }
        });
    }

    /**
     * Loads a game from the database to member variables
     *
     * @param newGame index of id in mGameIds to load
     */
    private void loadGameFromDatabase(final byte newGame)
    {
        clearFrameColor();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mCurrentGame = newGame;
                mCallback.onGameChanged(mCurrentGame);
                SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

                Cursor cursor = null;
                try
                {
                    cursor = database.query(FrameEntry.TABLE_NAME,
                            new String[]{FrameEntry.COLUMN_IS_ACCESSED, FrameEntry.COLUMN_PIN_STATE[0], FrameEntry.COLUMN_PIN_STATE[1], FrameEntry.COLUMN_PIN_STATE[2], FrameEntry.COLUMN_FOULS},
                            FrameEntry.COLUMN_GAME_ID + "=?",
                            new String[]{String.valueOf(mGameIds[mCurrentGame])},
                            null,
                            null,
                            FrameEntry.COLUMN_FRAME_NUMBER);

                    mFouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
                    byte currentFrameIterator = 0;
                    if (cursor.moveToFirst())
                    {
                        while (!cursor.isAfterLast())
                        {
                            byte frameAccessed = (byte) cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED));
                            mHasFrameBeenAccessed[currentFrameIterator] = (frameAccessed == 1);
                            for (int i = 0; i < 3; i++)
                            {
                                String ballString = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[i]));
                                boolean[] ballBoolean = {Score.getBoolean(ballString.charAt(0)), Score.getBoolean(ballString.charAt(1)), Score.getBoolean(ballString.charAt(2)), Score.getBoolean(ballString.charAt(3)), Score.getBoolean(ballString.charAt(4))};
                                mPinState[currentFrameIterator][i] = ballBoolean;
                            }
                            String foulsOfFrame = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_FOULS));
                            for (int ballCount = 0; ballCount < 3; ballCount++)
                            {
                                mFouls[currentFrameIterator][ballCount] = foulsOfFrame.contains(String.valueOf(ballCount + 1));
                            }

                            currentFrameIterator++;
                            cursor.moveToNext();
                        }
                    }
                }
                finally
                {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        clearAllText(!mManualScoreSet[mCurrentGame]);
                        getActivity().supportInvalidateOptionsMenu();
                        mTextViewGameNumber.setText("Game " + (mCurrentGame + 1));
                    }
                });
                setMatchPlay();
                setGameLocked(mGameLocked[mCurrentGame]);

                mCurrentFrame = 0;
                mCurrentBall = 0;
                if (mManualScoreSet[mCurrentGame])
                    return;

                updateScore();
                updateAllBalls();
                mHasFrameBeenAccessed[0] = true;

                while (mCurrentFrame < Constants.LAST_FRAME && mHasFrameBeenAccessed[mCurrentFrame + 1])
                    mCurrentFrame++;

                setVisibilityOfNextAndPrevItems();
                updateFrameColor(true);
            }
        }).start();
    }

    /**
     * Saves current game and loads new game from database
     * @param gameNumber index in mGameIds to load from database
     */
    public void switchGame(byte gameNumber)
    {
        if (gameNumber == mCurrentGame)
            return;

        saveGame(false);
        loadGameFromDatabase(gameNumber);
    }

    /**
     * Loads the initial scores for the games being displayed from the database
     * so they can be shown and updated
     */
    private void loadInitialScores()
    {
        MainActivity.waitForSaveThreads((MainActivity)getActivity());

        byte numberOfGames = ((MainActivity)getActivity()).getNumberOfGames();
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
        StringBuilder whereBuilder = new StringBuilder(GameEntry._ID + "=?");
        String[] whereArgs = new String[numberOfGames];
        whereArgs[0] = String.valueOf(mGameIds[0]);
        for (byte i = 1; i < numberOfGames; i++)
        {
            whereBuilder.append(" OR ");
            whereBuilder.append(GameEntry._ID);
            whereBuilder.append("=?");
            whereArgs[i] = String.valueOf(mGameIds[i]);
        }

        Cursor cursor = database.query(GameEntry.TABLE_NAME,
                new String[]{GameEntry.COLUMN_SCORE},
                whereBuilder.toString(),
                whereArgs,
                null,
                null,
                GameEntry._ID);

        byte currentGamePosition = 0;
        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                mGameScoresMinusFouls[currentGamePosition++] = gameScore;
                cursor.moveToNext();
            }
        }
        else
        {
            doNotSave.set(true);
            throw new RuntimeException("Fatal error: could not load initial scores.");
        }
        cursor.close();
    }

    /**
     * Gets the current game being edited
     * @return value of mCurrentGame
     */
    public byte getCurrentGame(){return mCurrentGame;}

    /**
     * Callback interface offers methods upon user interaction
     */
    public interface GameFragmentCallbacks
    {
        /**
         * Tells activity to open new StatsFragment with current game id and game number
         * @param gameId id of the game to display
         * @param gameNumber number in a series of the game to display
         */
        void onGameStatsOpened(long gameId, byte gameNumber);

        /**
         * Tells activity to open new StatsFragment with current series
         */
        void onSeriesStatsOpened();

        /**
         * Tells activity that the game has been changed
         * @param newGameNumber number of the new game, starting at index 0
         */
        void onGameChanged(byte newGameNumber);

        /**
         * Tells activity to begin a timer that will auto advance frame when it runs out
         * @param enable either enables or disables the timer
         * @param delaySeconds time to wait before auto advancing
         */
        void setAutoAdvanceEnabled(boolean enable, int delaySeconds);

        /**
         * Resets the auto advance timer before the delay expires
         */
        void resetAutoAdvanceTimer();

        /**
         * Stops the auto advance timer
         */
        void stopAutoAdvanceTimer();
    }

    /**
     * Creates a new instance and sets parameters as arguments for the instance
     * @param gameIds ids of the games being displayed
     * @param frameIds ids of frames belonging to gameIds
     * @param gameLocked whether the games being displayed are locked or not
     * @param manualScore whether the games being displayed have manual scores set
     * @return the newly created instance
     */
    public static GameFragment newInstance(long[] gameIds, long[] frameIds, boolean[] gameLocked, boolean[] manualScore, byte[] matchPlay)
    {
        GameFragment gameFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, gameIds);
        args.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, frameIds);
        args.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, gameLocked);
        args.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, manualScore);
        args.putByteArray(Constants.EXTRA_ARRAY_MATCH_PLAY, matchPlay);
        gameFragment.setArguments(args);
        return gameFragment;
    }
}
