package ca.josephroque.bowlingcompanion.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ManualScoreDialog;
import ca.josephroque.bowlingcompanion.utilities.Animate;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.utilities.Score;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class GameFragment extends Fragment
    implements
        Theme.ChangeableTheme,
        ManualScoreDialog.ManualScoreDialogListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "GameFragment";

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
    /** Indicates whether the current games being edited belong to an event or not */
    private boolean mEventMode;
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
    /** Used to offer user method to knock down all pins in a frame */
    private ImageView mImageViewClearPins;
    /** Displays TextView objects in a layout which user can interact with to access specific frame */
    private HorizontalScrollView mHorizontalScrollViewFrames;
    /** Displays text to user of option to enabled or disable a foul */
    private TextView mTextViewSettingFoul;
    /** Displays text to user of option to reset a frame */
    private TextView mTextViewSettingResetFrame;
    /** Displays text to user of option to lock a game */
    private TextView mTextViewSettingLockGame;
    /** Displays image to user of option to open settings menu */
    private ImageView mImageViewGameSettings;
    /** Displays manually set score */
    private TextView mTextViewManualScore;
    /** Layout which contains views related to general game options */
    private RelativeLayout mRelativeLayoutGameToolbar;

    /** Indicates if the game settings are visible */
    private boolean mSettingsOpened;
    /** Indicates if the game settings are disabled */
    private boolean mSettingsButtonsDisabled;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null)
        {
            mEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mGameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
            mSettingsOpened = savedInstanceState.getBoolean(Constants.EXTRA_SETTINGS_OPEN);
            mSettingsButtonsDisabled = savedInstanceState.getBoolean(Constants.EXTRA_SETTINGS_DISABLED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

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
         * Creates TextView objects to display information about state of game and
         * stores references in member variables
         */

        final int dp_128 = DataFormatter.getPixelsFromDP(screenDensity, 128);
        final int dp_120 = DataFormatter.getPixelsFromDP(screenDensity, 120);
        final int dp_88 = DataFormatter.getPixelsFromDP(screenDensity, 88);
        final int dp_40 = DataFormatter.getPixelsFromDP(screenDensity, 40);
        final int dp_41 = DataFormatter.getPixelsFromDP(screenDensity, 41);
        final int dp_20 = DataFormatter.getPixelsFromDP(screenDensity, 20);
        final int dp_36 = DataFormatter.getPixelsFromDP(screenDensity, 36);
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            TextView frameText = new TextView(getActivity());
            switch(i)
            {
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
                TextView text = new TextView(getActivity());
                text.setBackgroundResource(R.drawable.background_frame_text);
                text.setGravity(Gravity.CENTER);
                layoutParams = new RelativeLayout.LayoutParams(dp_40, dp_41);
                layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i + j * 40);
                relativeLayout.addView(text, layoutParams);
                mTextViewBallScores[i][j] = text;

                text = new TextView(getActivity());
                text.setGravity(Gravity.CENTER);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                layoutParams = new RelativeLayout.LayoutParams(dp_40, dp_20);
                layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i + j * 40);
                layoutParams.topMargin = dp_40;
                relativeLayout.addView(text, layoutParams);
                mTextViewFouls[i][j] = text;
            }

            frameText = new TextView(getActivity());
            frameText.setText(String.valueOf(i + 1));
            frameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            frameText.setGravity(Gravity.CENTER);
            frameText.setTextColor(getResources().getColor(android.R.color.white));
            layoutParams = new RelativeLayout.LayoutParams(dp_120, dp_36);
            layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, 120 * i);
            layoutParams.topMargin = dp_128;
        }

        mTextViewFinalScore = new TextView(getActivity());
        mTextViewFinalScore.setGravity(Gravity.CENTER);
        mTextViewFinalScore.setBackgroundResource(R.drawable.background_frame_text);
        layoutParams = new RelativeLayout.LayoutParams(dp_120, dp_128);
        layoutParams.leftMargin = DataFormatter.getPixelsFromDP(screenDensity, Constants.NUMBER_OF_FRAMES * 120);
        relativeLayout.addView(mTextViewFinalScore, layoutParams);
        mHorizontalScrollViewFrames.addView(relativeLayout);

        mImageButtonPins = new ImageButton[5];
        mImageButtonPins[0] = (ImageButton)rootView.findViewById(R.id.button_pin_1);
        mImageButtonPins[1] = (ImageButton)rootView.findViewById(R.id.button_pin_2);
        mImageButtonPins[2] = (ImageButton)rootView.findViewById(R.id.button_pin_3);
        mImageButtonPins[3] = (ImageButton)rootView.findViewById(R.id.button_pin_4);
        mImageButtonPins[4] = (ImageButton)rootView.findViewById(R.id.button_pin_5);

        for (ImageButton pinButton : mImageButtonPins)
            pinButton.setOnClickListener(onClickListeners[LISTENER_PIN_BUTTONS]);

        rootView.findViewById(R.id.iv_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        rootView.findViewById(R.id.iv_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        rootView.findViewById(R.id.tv_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        rootView.findViewById(R.id.tv_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewClearPins = (ImageView)rootView.findViewById(R.id.iv_clear_pins);
        mImageViewClearPins.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewGameSettings = (ImageView)rootView.findViewById(R.id.iv_game_settings);
        mImageViewGameSettings.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mTextViewSettingFoul = (TextView)rootView.findViewById(R.id.tv_setting_foul);
        mTextViewSettingFoul.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewSettingFoul.setVisibility(View.GONE);

        mTextViewSettingResetFrame = (TextView)rootView.findViewById(R.id.tv_setting_reset_frame);
        mTextViewSettingResetFrame.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewSettingResetFrame.setVisibility(View.GONE);

        mTextViewSettingLockGame = (TextView)rootView.findViewById(R.id.tv_setting_lock);
        mTextViewSettingLockGame.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewSettingLockGame.setVisibility(View.GONE);

        mTextViewManualScore = (TextView)rootView.findViewById(R.id.tv_manual_score);
        mRelativeLayoutGameToolbar = (RelativeLayout)rootView.findViewById(R.id.rl_game_toolbar);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        COLOR_BACKGROUND = getResources().getColor(R.color.primary_background);
        COLOR_HIGHLIGHT = getResources().getColor(R.color.secondary_background);

        if (mGameIds == null)
        {
            Bundle args = getArguments();
            mEventMode = args.getBoolean(Constants.EXTRA_EVENT_MODE);
            mGameIds = args.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = args.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = args.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = args.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
            mSettingsOpened = false;
            mSettingsButtonsDisabled = false;
        }

        mGameScores = new short[((MainActivity)getActivity()).getNumberOfGames()];
        mGameScoresMinusFouls = new short[((MainActivity)getActivity()).getNumberOfGames()];

        if (Theme.getGameFragmentThemeInvalidated())
        {
            updateTheme();
        }

        loadGameFromDatabase((byte)0);
    }

    @Override
    public void onPause()
    {
        clearFrameColor();
        saveGame();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Constants.EXTRA_EVENT_MODE, mEventMode);
        outState.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, mGameIds);
        outState.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, mFrameIds);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, mGameLocked);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, mManualScoreSet);
        outState.putBoolean(Constants.EXTRA_SETTINGS_OPEN, mSettingsOpened);
        outState.putBoolean(Constants.EXTRA_SETTINGS_DISABLED, mSettingsButtonsDisabled);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_game, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_series_stats)
                .setTitle((mEventMode) ? R.string.action_event_stats : R.string.action_series_stats);
        menu.findItem(R.id.action_set_score)
                .setTitle((mManualScoreSet[mCurrentGame])
                ? R.string.action_clear_score : R.string.action_set_score);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_share:
                //TODO Social.showShareDialog();
                return true;
            case R.id.action_set_score:
                if (mManualScoreSet[mCurrentGame])
                    showClearManualScoreDialog();
                else
                    showManualScoreDialog();
                return true;
            case R.id.action_series_stats:
                //TODO series stats
                return true;
            case R.id.action_reset_game:
                if (mGameLocked[mCurrentGame])
                    showGameLockedDialog();
                else
                    showResetGameDialog();
                return true;
            case R.id.action_what_if:
                showWhatIfDialog();
                return true;
            case R.id.action_stats:
                //TODO show stats
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme()
    {
        mRelativeLayoutGameToolbar.setBackgroundColor(Theme.getSecondaryThemeColor());

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Theme.getPrimaryThemeColor());
        gradientDrawable.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 12, 12});
        Theme.setBackgroundByAPI(mTextViewSettingFoul, gradientDrawable);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Theme.getPrimaryThemeColor());
        gradientDrawable.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Theme.setBackgroundByAPI(mTextViewSettingResetFrame, gradientDrawable);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Theme.getPrimaryThemeColor());
        gradientDrawable.setCornerRadii(new float[]{12, 12, 0, 0, 0, 0, 0, 0});
        Theme.setBackgroundByAPI(mTextViewSettingLockGame, gradientDrawable);

        Theme.validateGameFragmentTheme();
    }

    @Override
    public void onSetScore(short scoreToSet)
    {
        if (scoreToSet < 0 || scoreToSet > 450)
        {
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

        resetGame();
        mGameLocked[mCurrentGame] = true;
        mManualScoreSet[mCurrentGame] = true;
        mGameScores[mCurrentGame] = scoreToSet;
        mGameScoresMinusFouls[mCurrentGame] = scoreToSet;
        clearAllText(false);

        mTextViewSettingLockGame.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTextViewSettingLockGame.setText(R.string.text_unloock_game);
            }
        });
        getActivity().supportInvalidateOptionsMenu();
        saveGame();
    }

    /**
     * Creates instances of OnClickListener to listen to events created by
     * views in this activity
     *
     * @return OnClickListener instances which are applied to views in this activity
     */
    private View.OnClickListener[] getOnClickListeners()
    {
        View.OnClickListener[] listeners = new View.OnClickListener[3];
        listeners[LISTENER_TEXT_FRAMES] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideGameSettings();
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
                        for (int i = mCurrentFrame; i >= 0; i--)
                        {
                            if (mHasFrameBeenAccessed[i])
                                break;
                            mHasFrameBeenAccessed[i] = true;
                        }
                        updateFrameColor();
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
                hideGameSettings();
                byte ballToSet = 0;
                switch(v.getId())
                {
                    case R.id.button_pin_5: ballToSet++;
                    case R.id.button_pin_4: ballToSet++;
                    case R.id.button_pin_3: ballToSet++;
                    case R.id.button_pin_2: ballToSet++;
                    case R.id.button_pin_1:
                        alterPinState(ballToSet);
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
                if (viewId != R.id.tv_setting_foul && viewId != R.id.tv_setting_reset_frame
                        && viewId != R.id.tv_setting_lock && viewId != R.id.iv_game_settings)
                    hideGameSettings();

                switch(viewId)
                {
                    case R.id.iv_game_settings:
                        fadeGameSettings(mSettingsOpened);
                        break;
                    case R.id.tv_setting_foul:
                        if (mSettingsButtonsDisabled || mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        hideGameSettings();
                        mFouls[mCurrentFrame][mCurrentBall] = !mFouls[mCurrentFrame][mCurrentBall];
                        updateFouls();
                        break;
                    case R.id.tv_setting_reset_frame:
                        if (mSettingsButtonsDisabled || mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        hideGameSettings();
                        clearFrameColor();
                        mCurrentBall = 0;
                        for (int i = 0; i < 3; i++)
                        {
                            mFouls[mCurrentFrame][i] = false;
                            for (int j = 0; j < 5; j++)
                                mPinState[mCurrentFrame][i][j] = false;
                        }
                        updateFrameColor();
                        updateBalls(mCurrentFrame);
                        updateScore();
                        break;
                    case R.id.tv_setting_lock:
                        if (mSettingsButtonsDisabled || mManualScoreSet[mCurrentGame])
                            return;
                        hideGameSettings();
                        mGameLocked[mCurrentGame] = !mGameLocked[mCurrentGame];
                        mTextViewSettingLockGame.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mTextViewSettingLockGame.setText(
                                        (mGameLocked[mCurrentGame])
                                                ? R.string.text_unloock_game
                                                : R.string.text_lock_game);
                            }
                        });
                        break;
                    case R.id.iv_clear_pins:
                        clearPins();
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
                        updateFrameColor();
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
                        updateFrameColor();
                        break;
                    default:
                        throw new RuntimeException("Unknown other button id");
                }
            }
        };

        return listeners;
    }

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
                        mGameLocked[mCurrentGame] = false;
                        mManualScoreSet[mCurrentGame] = false;
                        resetGame();
                        clearAllText(true);
                        updateScore();
                        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                            updateBalls(i);
                        updateFrameColor();
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

    private void clearAllText(boolean enabled)
    {
        if (enabled)
        {
            mRelativeLayoutGameToolbar.setVisibility(View.VISIBLE);
            mImageViewGameSettings.setVisibility(View.VISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.VISIBLE);
            for (ImageButton imageButton : mImageButtonPins)
                imageButton.setVisibility(View.VISIBLE);
            mTextViewManualScore.setText(null);
            mTextViewManualScore.setVisibility(View.INVISIBLE);
        }
        else
        {
            mRelativeLayoutGameToolbar.setVisibility(View.INVISIBLE);
            mImageViewGameSettings.setVisibility(View.INVISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.INVISIBLE);
            for (ImageButton imageButton : mImageButtonPins)
                imageButton.setVisibility(View.INVISIBLE);
            mTextViewManualScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
            mTextViewManualScore.setVisibility(View.VISIBLE);
        }
    }

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
                        resetGame();
                        saveGame();
                        clearAllText(true);
                        updateScore();
                        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                            updateBalls(i);
                        getActivity().supportInvalidateOptionsMenu();
                        updateFrameColor();
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

    private void saveGame()
    {
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

        saveGameToDatabase(getActivity(),
                mGameIds[mCurrentGame],
                framesToSave,
                accessToSave,
                pinStateToSave,
                foulsToSave,
                mGameScoresMinusFouls[mCurrentGame],
                mGameLocked[mCurrentGame],
                mManualScoreSet[mCurrentGame]);
    }

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
                    {
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 2][0]);
                    }
                    else
                    {
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 1][1]);
                    }
                }
                else
                {
                    possibleScore -= Score.getValueOfFrameDifference(mPinState[mCurrentFrame][0], mPinState[mCurrentFrame][1]);
                }
            }
            else if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
            {
                int firstBallNextFrame = Score.getValueOfFrame(mPinState[mCurrentFrame + 1][0]);
                possibleScore -= firstBallNextFrame;
            }
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
            if (strikeLastFrame)
            {
                possibleScore += pinsLeftStanding + 15;
                if (strikeTwoFramesAgo)
                    possibleScore += pinsLeftStanding;
            } else if (spareLastFrame)
                possibleScore += pinsLeftStanding;
        }
        else if (mCurrentBall == 1)
        {
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

    private void hideGameSettings()
    {
        if(!mSettingsOpened)
            return;

        mSettingsOpened = false;
        mSettingsButtonsDisabled = false;

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mTextViewSettingFoul.setVisibility(View.GONE);
                mTextViewSettingResetFrame.setVisibility(View.GONE);
                mTextViewSettingLockGame.setVisibility(View.GONE);
            }
        });
    }

    private void fadeGameSettings(final boolean hideSettings)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mSettingsOpened = !hideSettings;
                if (mSettingsOpened)
                {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
                    {
                        Animate.startSupportFadeInAnimation(mTextViewSettingFoul, new com.nineoldandroids.animation.AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationStart(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mSettingsButtonsDisabled = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mSettingsButtonsDisabled = false;
                                    }
                                });
                        Animate.startSupportFadeInAnimation(mTextViewSettingResetFrame, null);
                        Animate.startSupportFadeInAnimation(mTextViewSettingLockGame, null);
                    }
                    else
                    {
                        Animate.startFadeInAnimation(mTextViewSettingFoul, new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        mSettingsButtonsDisabled = false;
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {
                                        mSettingsButtonsDisabled = true;
                                    }
                                });
                        Animate.startFadeInAnimation(mTextViewSettingResetFrame, null);
                        Animate.startFadeInAnimation(mTextViewSettingLockGame, null);

                    }
                }
                else
                {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
                    {
                        Animate.startSupportFadeOutAnimation(mTextViewSettingFoul, new com.nineoldandroids.animation.AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mTextViewSettingFoul.setVisibility(View.GONE);
                                        mSettingsButtonsDisabled = false;
                                    }

                                    @Override
                                    public void onAnimationStart(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mSettingsButtonsDisabled = true;
                                    }
                                });
                        Animate.startSupportFadeOutAnimation(mTextViewSettingResetFrame, new com.nineoldandroids.animation.AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mTextViewSettingResetFrame.setVisibility(View.GONE);
                                    }
                                });
                        Animate.startSupportFadeOutAnimation(mTextViewSettingLockGame, new com.nineoldandroids.animation.AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation)
                                    {
                                        mTextViewSettingLockGame.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else
                    {
                        Animate.startFadeOutAnimation(mTextViewSettingFoul, new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {
                                        mSettingsButtonsDisabled = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        mSettingsButtonsDisabled = false;
                                        mTextViewSettingFoul.setVisibility(View.GONE);
                                    }
                                });
                        Animate.startFadeOutAnimation(mTextViewSettingResetFrame, new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        mTextViewSettingResetFrame.setVisibility(View.GONE);
                                    }
                                });
                        Animate.startFadeOutAnimation(mTextViewSettingLockGame, new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        mTextViewSettingLockGame.setVisibility(View.GONE);
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * Sets the text of the three TextView instances which correspond to frameToUpdate
     *
     * @param frameToUpdate frame of which text should be updated
     */
    private void updateBalls(final byte frameToUpdate)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Sets text depending on state of pins in the frame
                final String[] ballString = new String[3];
                if (frameToUpdate == Constants.LAST_FRAME)
                {
                    if (Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                    {
                        ballString[0] = Constants.BALL_STRIKE;
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_STRIKE;
                            ballString[2] = Score.getValueOfBall(mPinState[frameToUpdate][2], 2, true);
                        }
                        else
                        {
                            ballString[1] = Score.getValueOfBall(mPinState[frameToUpdate][1], 1, false);
                            if (Arrays.equals(mPinState[frameToUpdate][2], Constants.FRAME_PINS_DOWN))
                                ballString[2] = Constants.BALL_SPARE;
                            else
                                ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                    else
                    {
                        ballString[0] = Score.getValueOfBall(mPinState[frameToUpdate][0], 0, false);
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_SPARE;
                            ballString[2] = Score.getValueOfBall(mPinState[frameToUpdate][2], 2, true);
                        }
                        else
                        {
                            ballString[1] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 1, false);
                            ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                }
                else
                {
                    ballString[0] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 0, false);
                    if (!Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                    {
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_SPARE;
                            ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 1])
                                    ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 0, false)
                                    : Constants.BALL_EMPTY;
                        }
                        else
                        {
                            ballString[1] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 1, false);
                            ballString[2] = Score.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                    else
                    {
                        if (mHasFrameBeenAccessed[frameToUpdate + 1])
                        {
                            ballString[1] = (mHasFrameBeenAccessed[frameToUpdate + 1])
                                    ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 0, false)
                                    : Constants.BALL_EMPTY;
                            if (Arrays.equals(mPinState[frameToUpdate + 1][0], Constants.FRAME_PINS_DOWN) && frameToUpdate < Constants.LAST_FRAME - 1)
                            {
                                ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 2])
                                        ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 2], 0, false)
                                        : Constants.BALL_EMPTY;
                            }
                            else
                            {
                                ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 1])
                                        ? Score.getValueOfBallDifference(mPinState[frameToUpdate + 1], 1, false)
                                        : Constants.BALL_EMPTY;
                            }
                        }
                        else
                        {
                            ballString[1] = Constants.BALL_EMPTY;
                            ballString[2] = Constants.BALL_EMPTY;
                        }
                    }
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
                            mTextViewFrames[i].setText(String.valueOf(frameScores[i]));
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
                mTextViewSettingFoul.setText(
                        (mFouls[mCurrentFrame][mCurrentBall])
                                ? R.string.text_remove_foul
                                : R.string.text_add_foul);
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
     */
    private void updateFrameColor()
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
                            mImageViewClearPins.setImageResource(R.drawable.ic_action_strike);
                            break;
                        case 1:
                            if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                                mImageViewClearPins.setImageResource(R.drawable.ic_action_strike);
                            else
                                mImageViewClearPins.setImageResource(R.drawable.ic_action_spare);
                            break;
                        case 2:
                            if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
                                mImageViewClearPins.setImageResource(R.drawable.ic_action_strike);
                            else if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                                mImageViewClearPins.setImageResource(R.drawable.ic_action_spare);
                            else
                                mImageViewClearPins.setImageResource(R.drawable.ic_action_fifteen);
                            break;
                    }
                } else
                {
                    switch (mCurrentBall)
                    {
                        case 0:
                            mImageViewClearPins.setImageResource(R.drawable.ic_action_strike);
                            break;
                        case 1:
                            mImageViewClearPins.setImageResource(R.drawable.ic_action_spare);
                            break;
                        case 2:
                            mImageViewClearPins.setImageResource(R.drawable.ic_action_fifteen);
                            break;
                    }
                }
                mImageViewClearPins.setEnabled(numberOfPinsStanding > 0);

                mTextViewSettingFoul.setText(
                        mFouls[mCurrentFrame][mCurrentBall]
                                ? R.string.text_remove_foul
                                : R.string.text_add_foul);

                focusOnFrame();
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
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin_disabled);
                        else
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin_enabled);
                        mImageViewClearPins.setEnabled(!allPinsKnockedOver);
                    }
                });

                updateBalls(mCurrentFrame);
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

            updateBalls(mCurrentFrame);
            updateScore();
            updateFrameColor();
        }
    }

    /**
     * Scrolls the position of hsvFrames so the current frame is centred
     */
    private void focusOnFrame()
    {
        mHorizontalScrollViewFrames.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCurrentFrame >= 1)
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
     */
    private static void saveGameToDatabase(
            final Activity srcActivity,
            final long gameId,
            final long[] frameIds,
            final boolean[] hasFrameBeenAccessed,
            final boolean[][][] pinState,
            final boolean[][] fouls,
            final short finalScore,
            final boolean gameLocked,
            final boolean manualScoreSet)
    {
        new Thread(new Runnable()
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
                    Log.w(TAG, "Error saving game " + gameId);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
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
                        mTextViewSettingLockGame.setText(
                                (mGameLocked[mCurrentGame])
                                        ? R.string.text_unloock_game
                                        : R.string.text_lock_game);
                        clearAllText(!mManualScoreSet[mCurrentGame]);
                        getActivity().supportInvalidateOptionsMenu();
                    }
                });

                mCurrentFrame = 0;
                mCurrentBall = 0;
                if (mManualScoreSet[mCurrentGame])
                    return;

                updateScore();
                for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                    updateBalls(i);
                mHasFrameBeenAccessed[0] = true;

                while (mCurrentFrame < Constants.LAST_FRAME && mHasFrameBeenAccessed[mCurrentFrame + 1])
                    mCurrentFrame++;

                updateFrameColor();
            }
        }).start();
    }

    public static GameFragment newInstance(boolean isEvent, long[] gameIds, long[] frameIds, boolean[] gameLocked, boolean[] manualScore)
    {
        GameFragment gameFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_EVENT_MODE, isEvent);
        args.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, gameIds);
        args.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, frameIds);
        args.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, gameLocked);
        args.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, manualScore);
        gameFragment.setArguments(args);
        return gameFragment;
    }
}
