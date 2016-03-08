package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ManualScoreDialog;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.NavigationUtils;
import ca.josephroque.bowlingcompanion.utilities.Score;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.ShareUtils;
import ca.josephroque.bowlingcompanion.view.AnimatedFloatingActionButton;
import ca.josephroque.bowlingcompanion.view.PinLayout;

/**
 * Created by Joseph Roque on 15-03-18. Manages the UI to display information about the games being tracked by the
 * application, and offers a callback interface {@code GameFragment.GameFragmentCallback} for handling interactions.
 */
@SuppressWarnings({"Convert2Lambda", "CheckStyle"})
public class GameFragment
        extends Fragment
        implements Theme.ChangeableTheme,
        ManualScoreDialog.ManualScoreDialogListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameFragment";

    /** Represents the OnClickListener for the frame TextView objects. */
    private static final byte LISTENER_TEXT_FRAMES = 0;
    /** Represents the OnClickListener for any other objects. */
    private static final byte LISTENER_OTHER = 1;
    /** Represents the OnClickListener for the ball TextView objects. */
    private static final byte LISTENER_TEXT_BALLS = 2;

    /** TextView which displays score on a certain ball in a certain frame. */
    private TextView[][] mTextViewBallScores;
    /** TextView which displays whether a foul was invoked on a certain ball. */
    private TextView[][] mTextViewFouls;
    /** TextView which displays total score (not considering fouls) in a certain frame. */
    private TextView[] mTextViewFrames;
    /** TextView which displays final score of the game, with fouls considered. */
    private TextView mTextViewFinalScore;
    /** Offer interaction methods, indicate state of pins in a frame. */
    private ImageView[] mImageViewPins;
    /**
     * Displays TextView objects in a layout which user can interact with to access specific frame.
     */
    private HorizontalScrollView mHorizontalScrollViewFrames;
    /** Displays text to user of option to lock a game. */
    private ImageView mImageViewLock;
    /** Used to offer user method to knock down all pins in a frame. */
    private ImageView mImageViewClear;
    /** Displays image to user of option to enabled or disable a foul. */
    private ImageView mImageViewFoul;
    /** Displays image to user of option to reset a frame. */
    private ImageView mImageViewResetFrame;
    /** Displays image to user of option to set match play results. */
    private ImageView mImageViewMatchPlay;
    /** Displays manually set score. */
    private TextView mTextViewManualScore;

    /** Displays text to indicate navigation to next ball. */
    private TextView mTextViewNextBall;
    /** Displays text to indicate navigation to previous ball. */
    private TextView mTextViewPrevBall;
    /** Displays image to indicate navigation to next ball. */
    private ImageView mImageViewNextBall;
    /** Displays image to indicate navigation to previous ball. */
    private ImageView mImageViewPrevBall;
    /** Displays the current game number being viewed. */
    private TextView mTextViewGameNumber;

    /** View which displays auto advance information. */
    private TextView mTextViewAutoAdvance;

    /** Floating action button to advance to next ball. */
    private AnimatedFloatingActionButton mNextFab;
    /** Floating action button to return to previous ball. */
    private AnimatedFloatingActionButton mPrevFab;

    /** Instance of callback interface for handling user events. */
    private GameFragmentCallback mGameCallback;

    /** Ids which represent current games that are available to be edited. */
    private long[] mGameIds;
    /** Ids which represent frames of current games. */
    private long[] mFrameIds;

    /** Indicates which frames in the game have been accessed. */
    private boolean[] mHasFrameBeenAccessed;
    /** Indicate whether a certain pin was knocked down after a certain ball in a certain frame. */
    private boolean[][][] mPinState;
    /** Indicate whether a foul was invoked on a certain ball in a certain frame. */
    private boolean[][] mFouls;
    /** Scores of the current games being edited. */
    private short[] mGameScores;
    /** Scores of the current games being edited, with fouls considered. */
    private short[] mGameScoresMinusFouls;
    /** Indicates if a game is locked or not. */
    private boolean[] mGameLocked;
    /** Indicates if a game has a manual score set or not. */
    private boolean[] mManualScoreSet;
    /** Indicates the results of match play for the game. */
    private byte mMatchPlay;

    /** Integer which represents the background color of views in the activity. */
    private int mColorBackground;
    /** Integer which represents the highlighted color of views in the activity. */
    private int mColorHighlight;

    /** The current game being edited. */
    private byte mCurrentGame = 0;
    /** The current frame being edited. */
    private byte mCurrentFrame = 0;
    /** The current ball being edited. */
    private byte mCurrentBall = 0;

    /** Indicates if the app should not save games - set to true in case of errors. */
    private final AtomicBoolean mDoNotSave = new AtomicBoolean(false);

    /** Linear layout which contains button pins. */
    private PinLayout mLinearLayoutPins;
    /** Handles events when pin buttons are touched. */
    private final PinLayout.PinInterceptListener mPinTouchListener
            = new PinLayout.PinInterceptListener() {

        /** Indicates if a pin has been altered in a touch event. */
        private final boolean[] mPinAltered = new boolean[5];
        /** State of first pin touched. */
        private boolean mInitialState;
        /** Indicates if an initial valid pin has been selected. */
        private boolean mInitialStateSet;
        /** Total width of pin container. */
        private int mLayoutWidth;
        /** Number of pins which were altered. */
        private int mPinAlteredCount;
        /** Indicates if the layout is being interacted with. */
        private boolean mIsDragging;
        /** Indicates if events to start dragging can begin. */
        private boolean mCanStartDragging = true;

        private void setFirstPinTouched(MotionEvent event) {
            final int pinTouched = (int) ((event.getX() / mLayoutWidth) * 5);
            if (!mImageViewPins[pinTouched].isEnabled()) {
                mInitialStateSet = false;
                return;
            }
            mInitialStateSet = true;
            mInitialState = mPinState[mCurrentFrame][mCurrentBall][pinTouched];
            mPinAltered[pinTouched] = true;
            mPinAlteredCount = 1;

            // Flip pin image while user is dragging
            mImageViewPins[pinTouched].post(new Runnable() {
                @Override
                public void run() {
                    if (!mPinState[mCurrentFrame][mCurrentBall][pinTouched])
                        mImageViewPins[pinTouched].setImageResource(
                                R.drawable.pin_disabled);
                    else
                        mImageViewPins[pinTouched].setImageResource(
                                R.drawable.pin_enabled);
                }
            });
        }

        @Override
        public void disablePinTouches() {
            mCanStartDragging = false;
        }

        @Override
        public void onPinTouch(MotionEvent event) {
            if (NavigationUtils.getDrawerOffset() > 0
                    || mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                return;

            final int pinTouched;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (!mCanStartDragging)
                        return;
                    mIsDragging = true;
                    for (int i = 0; i < mPinAltered.length; i++)
                        mPinAltered[i] = false;
                    mLayoutWidth = mLinearLayoutPins.getWidth();
                    if (event.getX() > mLayoutWidth || event.getX() < 0)
                        return;
                    setFirstPinTouched(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mCanStartDragging || !mInitialStateSet
                            || mLayoutWidth == 0 || event.getX() > mLayoutWidth || event.getX() < 0)
                        return;

                    // Flip pin image while user is dragging
                    pinTouched = (int) ((event.getX() / mLayoutWidth) * 5);
                    if (mImageViewPins[pinTouched].isEnabled()
                            && mPinState[mCurrentFrame][mCurrentBall][pinTouched] == mInitialState
                            && !mPinAltered[pinTouched]) {
                        mPinAlteredCount++;
                        mPinAltered[pinTouched] = true;
                        mImageViewPins[pinTouched].post(new Runnable() {
                            @Override
                            public void run() {
                                if (!mPinState[mCurrentFrame][mCurrentBall][pinTouched])
                                    mImageViewPins[pinTouched].setImageResource(R.drawable.pin_disabled);
                                else
                                    mImageViewPins[pinTouched].setImageResource(R.drawable.pin_enabled);
                            }
                        });
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mCanStartDragging = true;
                case MotionEvent.ACTION_UP:
                    if (!mIsDragging)
                        return;
                    mIsDragging = false;
                    int pinsUpdated = 0;
                    mInitialStateSet = false;

                    // Finalize flipped pins, calculate score
                    for (int i = 0; i < mPinAltered.length; i++) {
                        if (mPinAltered[i]) {
                            pinsUpdated++;
                            if (pinsUpdated == mPinAlteredCount)
                                alterPinState((byte) i, true);
                            else
                                alterPinState((byte) i, false);
                        }
                    }
                    if (mGameCallback != null && !(mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2))
                        mGameCallback.resetAutoAdvanceTimer();
                    break;
                default:
                    // does nothing
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         * This makes sure the container Activity has implemented
         * the callback interface. If not, an exception is thrown
         */
        try {
            mGameCallback = (GameFragmentCallback) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement GameFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Loads member variables from saved instance state, if one exists
        if (savedInstanceState != null) {
            mCurrentGame = savedInstanceState.getByte(Constants.EXTRA_CURRENT_GAME);
            mGameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = savedInstanceState.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = savedInstanceState.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
        }
    }

    @SuppressWarnings("deprecation") // uses newer APIs where available
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        // Density of screen to set proper width/height of views
        final float screenDensity = getResources().getDisplayMetrics().density;

        mHorizontalScrollViewFrames = (HorizontalScrollView) rootView.findViewById(R.id.hsv_frames);

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

        // Calculates most static view sizes beforehand so they don't have to be recalculated
        final int dp128 = DisplayUtils.getPixelsFromDP(screenDensity, 128);
        final int dp120 = DisplayUtils.getPixelsFromDP(screenDensity, 120);
        final int dp88 = DisplayUtils.getPixelsFromDP(screenDensity, 88);
        final int dp40 = DisplayUtils.getPixelsFromDP(screenDensity, 40);
        final int dp41 = DisplayUtils.getPixelsFromDP(screenDensity, 41);
        final int dp20 = DisplayUtils.getPixelsFromDP(screenDensity, 20);
        final int dp36 = DisplayUtils.getPixelsFromDP(screenDensity, 36);
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++) {
            // TextView to display score of a frame
            TextView frameText = new TextView(getActivity());
            switch (i) {
                // Id is set so when view is clicked, it can be identified
                case 0:
                    frameText.setId(R.id.text_frame_0);
                    break;
                case 1:
                    frameText.setId(R.id.text_frame_1);
                    break;
                case 2:
                    frameText.setId(R.id.text_frame_2);
                    break;
                case 3:
                    frameText.setId(R.id.text_frame_3);
                    break;
                case 4:
                    frameText.setId(R.id.text_frame_4);
                    break;
                case 5:
                    frameText.setId(R.id.text_frame_5);
                    break;
                case 6:
                    frameText.setId(R.id.text_frame_6);
                    break;
                case 7:
                    frameText.setId(R.id.text_frame_7);
                    break;
                case 8:
                    frameText.setId(R.id.text_frame_8);
                    break;
                case 9:
                    frameText.setId(R.id.text_frame_9);
                    break;
                default: // do nothing
            }
            frameText.setTextColor(0xff000000);
            frameText.setBackgroundResource(R.drawable.background_frame_text);
            frameText.setGravity(Gravity.CENTER);
            frameText.setOnClickListener(onClickListeners[LISTENER_TEXT_FRAMES]);

            layoutParams = new RelativeLayout.LayoutParams(dp120, dp88);
            layoutParams.leftMargin = DisplayUtils.getPixelsFromDP(screenDensity, 120 * i);
            layoutParams.topMargin = dp40;
            relativeLayout.addView(frameText, layoutParams);
            mTextViewFrames[i] = frameText;

            for (int j = 0; j < 3; j++) {
                // TextView to display value scored on a certain ball
                TextView text = new TextView(getActivity());
                text.setTextColor(0xff000000);
                text.setBackgroundResource(R.drawable.background_frame_text);
                text.setGravity(Gravity.CENTER);
                layoutParams = new RelativeLayout.LayoutParams(dp40, dp41);
                layoutParams.leftMargin = DisplayUtils.getPixelsFromDP(screenDensity,
                        120 * i + j * 40);
                relativeLayout.addView(text, layoutParams);
                mTextViewBallScores[i][j] = text;
                text.setOnClickListener(onClickListeners[LISTENER_TEXT_BALLS]);

                // TextView to display fouls invoked on a certain ball
                text = new TextView(getActivity());
                text.setGravity(Gravity.CENTER);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                layoutParams = new RelativeLayout.LayoutParams(dp40, dp20);
                layoutParams.leftMargin = DisplayUtils.getPixelsFromDP(screenDensity,
                        120 * i + j * 40);
                layoutParams.topMargin = dp40;
                relativeLayout.addView(text, layoutParams);
                mTextViewFouls[i][j] = text;
            }

            // TextView to display frame number under related frame information
            frameText = new TextView(getActivity());
            frameText.setText(String.valueOf(i + 1));
            frameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            frameText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            frameText.setTextColor(DisplayUtils.getColorResource(getResources(), android.R.color.white));
            layoutParams = new RelativeLayout.LayoutParams(dp120, dp36);
            layoutParams.leftMargin = DisplayUtils.getPixelsFromDP(screenDensity, 120 * i);
            layoutParams.addRule(RelativeLayout.BELOW, mTextViewFrames[i].getId());
            relativeLayout.addView(frameText, layoutParams);
        }

        // TextView to display final score of game
        mTextViewFinalScore = new TextView(getActivity());
        mTextViewFinalScore.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mTextViewFinalScore.setTextAppearance(android.R.style.TextAppearance_Large);
        else
            mTextViewFinalScore.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
        mTextViewFinalScore.setBackgroundResource(R.drawable.background_frame_text);
        layoutParams = new RelativeLayout.LayoutParams(dp120, dp128);
        layoutParams.leftMargin = DisplayUtils.getPixelsFromDP(screenDensity,
                Constants.NUMBER_OF_FRAMES * 120);
        relativeLayout.addView(mTextViewFinalScore, layoutParams);
        mHorizontalScrollViewFrames.addView(relativeLayout);

        // Buttons which indicate state of pins in a frame, provide user interaction methods
        mImageViewPins = new ImageView[5];
        mImageViewPins[0] = (ImageView) rootView.findViewById(R.id.button_pin_1);
        mImageViewPins[1] = (ImageView) rootView.findViewById(R.id.button_pin_2);
        mImageViewPins[2] = (ImageView) rootView.findViewById(R.id.button_pin_3);
        mImageViewPins[3] = (ImageView) rootView.findViewById(R.id.button_pin_4);
        mImageViewPins[4] = (ImageView) rootView.findViewById(R.id.button_pin_5);

        mLinearLayoutPins = (PinLayout) rootView.findViewById(R.id.ll_pins);
        mLinearLayoutPins.setInterceptListener(mPinTouchListener);

        // Loading other views into member variables, setting OnClickListeners
        mImageViewNextBall = (ImageView) rootView.findViewById(R.id.iv_next_ball);
        mImageViewNextBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewPrevBall = (ImageView) rootView.findViewById(R.id.iv_prev_ball);
        mImageViewPrevBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewNextBall = (TextView) rootView.findViewById(R.id.tv_next_ball);
        mTextViewNextBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mTextViewPrevBall = (TextView) rootView.findViewById(R.id.tv_prev_ball);
        mTextViewPrevBall.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewClear = (ImageView) rootView.findViewById(R.id.iv_clear);
        mImageViewClear.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mTextViewGameNumber = (TextView) rootView.findViewById(R.id.tv_game_number);
        layoutParams = (RelativeLayout.LayoutParams) mTextViewGameNumber.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewNextBall.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewNextBall.getId());

        mImageViewFoul = (ImageView) rootView.findViewById(R.id.iv_foul);
        mImageViewFoul.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewResetFrame = (ImageView) rootView.findViewById(R.id.iv_reset_frame);
        mImageViewResetFrame.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewLock = (ImageView) rootView.findViewById(R.id.iv_lock);
        mImageViewLock.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mImageViewMatchPlay = (ImageView) rootView.findViewById(R.id.iv_match);
        mImageViewMatchPlay.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mTextViewManualScore = (TextView) rootView.findViewById(R.id.tv_manual_score);
        mTextViewAutoAdvance = (TextView) rootView.findViewById(R.id.tv_auto_advance_status);

        mNextFab = (AnimatedFloatingActionButton) rootView.findViewById(R.id.fab_next_ball);
        mNextFab.setVisibility(View.GONE);
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), mNextFab);
        mNextFab.setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mPrevFab = (AnimatedFloatingActionButton) rootView.findViewById(R.id.fab_prev_ball);
        mPrevFab.setVisibility(View.GONE);
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), mPrevFab);
        mPrevFab.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.title_fragment_game, true);
            mainActivity.setFloatingActionButtonState(0, 0);
            mainActivity.createGameNavigationDrawer();
            mainActivity.setDrawerState(true);
        }

        // Loads colors for frame view backgrounds
        mColorBackground = DisplayUtils.getColorResource(getResources(), R.color.primary_background);
        mColorHighlight = DisplayUtils.getColorResource(getResources(), R.color.secondary_background);

        // If values were not loaded from saved instance state, they are loaded here
        if (mGameIds == null) {
            Bundle args = getArguments();
            mGameIds = args.getLongArray(Constants.EXTRA_ARRAY_GAME_IDS);
            mFrameIds = args.getLongArray(Constants.EXTRA_ARRAY_FRAME_IDS);
            mGameLocked = args.getBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED);
            mManualScoreSet = args.getBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET);
        }

        mGameScores = new short[((MainActivity) getActivity()).getNumberOfGamesForSeries()];
        mGameScoresMinusFouls
                = new short[((MainActivity) getActivity()).getNumberOfGamesForSeries()];

        updateTheme();

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean autoAdvanceEnabled =
                preferences.getBoolean(Constants.KEY_ENABLE_AUTO_ADVANCE, false);

        String strDelay = preferences.getString(Constants.KEY_AUTO_ADVANCE_TIME, "15 seconds");
        int autoAdvanceDelay = Integer.valueOf(strDelay.substring(0, strDelay.indexOf(" ")));

        if (mGameCallback != null) {
            mGameCallback.setAutoAdvanceConditions(mImageViewNextBall,
                    mTextViewAutoAdvance,
                    autoAdvanceEnabled,
                    autoAdvanceDelay);
            mGameCallback.stopAutoAdvanceTimer();

            // Loads scores of games being edited from database
            loadInitialScores();
            // Loads first game to edit
            loadGameFromDatabase(mCurrentGame);
            mGameCallback.loadGameScoresForDrawer(mGameIds);
        }

        DisplayUtils.hideKeyboard(getActivity());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentFrame > 0 || mCurrentBall > 0)
                    setFloatingActionButtonState(false, R.drawable.ic_chevron_left_black_24dp);
                else
                    setFloatingActionButtonState(false, 0);

                if (mCurrentFrame < Constants.LAST_FRAME || mCurrentBall < 2)
                    setFloatingActionButtonState(true, R.drawable.ic_chevron_right_black_24dp);
                else
                    setFloatingActionButtonState(true, 0);
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNextFab != null)
            setFloatingActionButtonState(true, 0);
        if (mPrevFab != null)
            setFloatingActionButtonState(false, 0);
        mGameCallback.setAutoAdvanceConditions(mImageViewNextBall,
                mTextViewAutoAdvance,
                false,
                Integer.MAX_VALUE);

        // Clears color changes to frames and saves the game being edited
        clearFrameColor();
        saveGame(false);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Puts member variables into outState so they can be loaded back
        outState.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, mGameIds);
        outState.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, mFrameIds);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, mGameLocked);
        outState.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, mManualScoreSet);
        outState.putByte(Constants.EXTRA_CURRENT_GAME, mCurrentGame);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_game, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Sets names/visibility of menu items
        menu.findItem(R.id.action_series_stats)
                .setTitle(((MainActivity) getActivity()).isEventMode()
                        ? R.string.action_event_stats
                        : R.string.action_series_stats);
        menu.findItem(R.id.action_set_score)
                .setTitle((mManualScoreSet[mCurrentGame])
                        ? R.string.action_clear_score
                        : R.string.action_set_score);

        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_stats).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        menuItem = menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        menu.findItem(R.id.action_series_stats).setVisible(!drawerOpen);
        menu.findItem(R.id.action_reset_game).setVisible(!drawerOpen);
        menu.findItem(R.id.action_set_score).setVisible(!drawerOpen);

        menu.findItem(R.id.action_best_possible).setVisible(
                !mManualScoreSet[mCurrentGame] && !drawerOpen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                // Prompts user to share or save an image of current series
                saveGame(false);
                MainActivity mainActivity = (MainActivity) getActivity();
                ShareUtils.showShareDialog(mainActivity, mainActivity.getSeriesId());
                return true;

            case R.id.action_set_score:
                // If a manual score is set, clear it
                // Otherwise, prompt for a new score from the user
                if (mManualScoreSet[mCurrentGame])
                    showClearManualScoreDialog();
                else
                    showManualScoreDialog();
                return true;

            case R.id.action_series_stats:
                // Displays all stats related to series of games
                if (mGameCallback != null)
                    mGameCallback.onSeriesStatsOpened();
                return true;

            case R.id.action_reset_game:
                // If the game is locked, it cannot be reset
                // Otherwise, prompts user to reset
                if (mGameLocked[mCurrentGame] && !mManualScoreSet[mCurrentGame])
                    showGameLockedDialog();
                else
                    showResetGameDialog();
                if (mGameCallback != null)
                    mGameCallback.stopAutoAdvanceTimer();
                return true;

            case R.id.action_best_possible:
                // Calculates possible score and displays
                showBestPossibleScoreDialog();
                return true;

            case R.id.action_stats:
                // Displays all stats related to current game
                if (mGameCallback != null)
                    mGameCallback.onGameStatsOpened(mGameIds[mCurrentGame],
                            (byte) (mCurrentGame + 1));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme() {
        View rootView = getView();
        if (rootView != null) {
            rootView.findViewById(R.id.rl_game_toolbar).setBackgroundColor(
                    Theme.getSecondaryThemeColor());
        }

        if (mNextFab != null) {
            DisplayUtils.setFloatingActionButtonColors(mNextFab,
                    Theme.getPrimaryThemeColor(),
                    Theme.getTertiaryThemeColor());
            mNextFab.setEnabled(false);
            mNextFab.performClick();
            mNextFab.setEnabled(true);
        }
        if (mPrevFab != null) {
            DisplayUtils.setFloatingActionButtonColors(mPrevFab,
                    Theme.getPrimaryThemeColor(),
                    Theme.getTertiaryThemeColor());
            mPrevFab.setEnabled(false);
            mPrevFab.performClick();
            mPrevFab.setEnabled(true);
        }
    }

    @Override
    public void onSetScore(short scoreToSet) {
        if (scoreToSet < 0 || scoreToSet > Constants.GAME_MAX_SCORE) {
            // If an invalid score is given, user is informed and method exists
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid score!")
                    .setMessage(R.string.dialog_bad_score)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // Clears state of game and sets the manual score of the game
        resetGame();
        setGameLocked(true);
        mManualScoreSet[mCurrentGame] = true;
        mGameScores[mCurrentGame] = scoreToSet;
        mGameScoresMinusFouls[mCurrentGame] = scoreToSet;
        clearAllText(false);
        getActivity().supportInvalidateOptionsMenu();
        if (mGameCallback != null)
            mGameCallback.updateGameScore((byte) (mCurrentGame + 1),
                    mGameScoresMinusFouls[mCurrentGame]);
        saveGame(true);
    }

    /**
     * Creates instances of OnClickListener to listen to events created by views in this activity.
     *
     * @return OnClickListener instances which are applied to views in this activity
     */
    private View.OnClickListener[] getOnClickListeners() {
        View.OnClickListener[] listeners = new View.OnClickListener[3];
        listeners[LISTENER_TEXT_FRAMES] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte frameToSet = 0;
                switch (v.getId()) {
                    case R.id.text_frame_9:
                        frameToSet++;
                    case R.id.text_frame_8:
                        frameToSet++;
                    case R.id.text_frame_7:
                        frameToSet++;
                    case R.id.text_frame_6:
                        frameToSet++;
                    case R.id.text_frame_5:
                        frameToSet++;
                    case R.id.text_frame_4:
                        frameToSet++;
                    case R.id.text_frame_3:
                        frameToSet++;
                    case R.id.text_frame_2:
                        frameToSet++;
                    case R.id.text_frame_1:
                        frameToSet++;
                    case R.id.text_frame_0:
                        // Changes the current frame and updates the GUI
                        clearFrameColor();
                        mCurrentFrame = frameToSet;
                        mCurrentBall = 0;
                        byte frameUpdateCount = 0;
                        for (int i = mCurrentFrame; i >= 0; i--) {
                            if (mHasFrameBeenAccessed[i])
                                break;
                            mHasFrameBeenAccessed[i] = true;
                            frameUpdateCount++;
                        }
                        setVisibilityOfNextAndPrevItems();
                        if (frameUpdateCount > 3)
                            updateAllBalls();
                        else
                            updateBalls(mCurrentFrame, (byte) 0);
                        updateScore();
                        updateFrameColor(false);
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;
                    default:
                        throw new RuntimeException("Invalid frame id");
                }
            }
        };

        listeners[LISTENER_OTHER] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();

                switch (viewId) {
                    case R.id.iv_match:
                        if (mGameLocked[mCurrentGame])
                            showSetMatchPlayLockedDialog();
                        else
                            showSetMatchPlayScreen();
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;

                    case R.id.iv_lock:
                        // Locks the game if it is unlocked,
                        // unlocks if locked
                        if (mManualScoreSet[mCurrentGame])
                            return;
                        setGameLocked(!mGameLocked[mCurrentGame]);
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;

                    case R.id.iv_foul:
                        // Sets or removes a foul and updates scores
                        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        mFouls[mCurrentFrame][mCurrentBall] = !mFouls[mCurrentFrame][mCurrentBall];
                        updateFouls();
                        if (mGameCallback != null && !(mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2))
                            mGameCallback.resetAutoAdvanceTimer();
                        break;

                    case R.id.iv_reset_frame:
                        // Resets the current frame to ball 0, no fouls, no pins knocked
                        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
                            return;
                        clearFrameColor();
                        mCurrentBall = 0;
                        for (int i = 0; i < 3; i++) {
                            mFouls[mCurrentFrame][i] = false;
                            for (int j = 0; j < 5; j++)
                                mPinState[mCurrentFrame][i][j] = false;
                        }
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
                        updateBalls(mCurrentFrame, (byte) 0);
                        updateScore();
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;

                    case R.id.iv_clear:
                        clearPins();
                        if (mGameCallback != null && !(mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2))
                            mGameCallback.resetAutoAdvanceTimer();
                        break;

                    case R.id.iv_next_ball:
                    case R.id.tv_next_ball:
                    case R.id.fab_next_ball:
                        // Changes the current frame and updates the GUI
                        if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2)
                            return;

                        clearFrameColor();
                        if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall],
                                Constants.FRAME_PINS_DOWN)) {
                            if (mCurrentFrame < Constants.LAST_FRAME) {
                                mCurrentBall = 0;
                                mCurrentFrame++;
                            } else if (mCurrentBall < 2) {
                                mCurrentBall++;
                            }
                        } else if (++mCurrentBall == 3) {
                            mCurrentBall = 0;
                            ++mCurrentFrame;
                        }
                        mHasFrameBeenAccessed[mCurrentFrame] = true;
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;

                    case R.id.iv_prev_ball:
                    case R.id.tv_prev_ball:
                    case R.id.fab_prev_ball:
                        // Changes the current frame and updates the GUI
                        if (mCurrentFrame == 0 && mCurrentBall == 0)
                            return;

                        clearFrameColor();
                        if (--mCurrentBall == -1) {
                            mCurrentBall = 0;
                            --mCurrentFrame;
                            while (!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall],
                                    Constants.FRAME_PINS_DOWN) && mCurrentBall < 2) {
                                mCurrentBall++;
                            }
                        }
                        setVisibilityOfNextAndPrevItems();
                        updateFrameColor(false);
                        if (mGameCallback != null)
                            mGameCallback.stopAutoAdvanceTimer();
                        break;

                    default:
                        throw new RuntimeException("Unknown other button id");
                }
            }
        };

        listeners[LISTENER_TEXT_BALLS] = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean viewFound = false;
                for (byte i = 0; i < mTextViewBallScores.length && !viewFound; i++) {
                    for (byte j = 0; j < mTextViewBallScores[i].length; j++) {
                        if (v == mTextViewBallScores[i][j]) {
                            viewFound = true;

                            // Changes the current frame and updates the GUI
                            clearFrameColor();
                            mCurrentFrame = i;
                            mCurrentBall = 0;
                            byte frameUpdateCount = 0;
                            for (int k = mCurrentFrame; k >= 0; k--) {
                                if (mHasFrameBeenAccessed[k])
                                    break;
                                mHasFrameBeenAccessed[k] = true;
                                frameUpdateCount++;
                            }
                            while (!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall],
                                    Constants.FRAME_PINS_DOWN) && mCurrentBall < j) {
                                mCurrentBall++;
                            }
                            setVisibilityOfNextAndPrevItems();
                            if (frameUpdateCount > 3)
                                updateAllBalls();
                            else
                                updateBalls(mCurrentFrame, (byte) 0);
                            updateScore();
                            updateFrameColor(false);
                            if (mGameCallback != null)
                                mGameCallback.stopAutoAdvanceTimer();
                            break;
                        }
                    }
                }
            }
        };

        return listeners;
    }

    /**
     * Displays prompt to user to inform them the game is locked. Included to prevent accidental changes of match play
     * results, but still allow changes if game is locked (such as by a manual score being set)
     */
    private void showSetMatchPlayLockedDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Game is locked")
                .setMessage(R.string.dialog_match_locked)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSetMatchPlayScreen();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts user to set results of match play for a game.
     */
    private void showSetMatchPlayScreen() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.openMatchPlayStats(mGameIds[mCurrentGame]);
    }

    /**
     * Prompts user to reset the current game and set a manual score
     */
    private void showManualScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set manual score?")
                .setMessage(R.string.dialog_set_score)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DialogFragment dialogFragment =
                                ManualScoreDialog.newInstance(GameFragment.this);
                        dialogFragment.show(getFragmentManager(), "ManualScoreDialog");
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts user to reset the current game and remove a manual score.
     */
    private void showClearManualScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Clear the set score?")
                .setMessage(R.string.dialog_clear_score)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Unlocks and resets game
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
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Hides or shows TextView objects in the app that display individual score elements.
     *
     * @param enabled if true, TextView objects related to scores will be shown
     */
    private void clearAllText(boolean enabled) {
        View rootView = getView();
        if (rootView == null)
            return;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean fabsEnabled = preferences.getBoolean(Constants.KEY_ENABLE_FAB, true);

        if (enabled) {
            mImageViewNextBall.setVisibility(View.VISIBLE);
            mImageViewPrevBall.setVisibility(View.VISIBLE);
            mTextViewNextBall.setVisibility(View.VISIBLE);
            mTextViewPrevBall.setVisibility(View.VISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.VISIBLE);
            for (ImageView imageView : mImageViewPins)
                imageView.setVisibility(View.VISIBLE);
            mTextViewManualScore.setText(null);
            mTextViewManualScore.setVisibility(View.INVISIBLE);
            mLinearLayoutPins.setEnabled(true);

            View pinsLayout = rootView.findViewById(R.id.ll_pins);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pinsLayout.getLayoutParams();
            if (fabsEnabled && preferences.getBoolean(Constants.KEY_PINS_BEHIND_FABS, false))
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.iv_lock);
            else
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.fab_container);
            pinsLayout.setLayoutParams(layoutParams);
        } else {
            mImageViewNextBall.setVisibility(View.INVISIBLE);
            mImageViewPrevBall.setVisibility(View.INVISIBLE);
            mTextViewNextBall.setVisibility(View.INVISIBLE);
            mTextViewPrevBall.setVisibility(View.INVISIBLE);
            mHorizontalScrollViewFrames.setVisibility(View.INVISIBLE);
            for (ImageView imageView : mImageViewPins)
                imageView.setVisibility(View.INVISIBLE);
            mTextViewManualScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
            mTextViewManualScore.setVisibility(View.VISIBLE);
            mLinearLayoutPins.setEnabled(false);
        }

        rootView.findViewById(R.id.fab_container).setVisibility((fabsEnabled && enabled)
                        ? View.VISIBLE
                        : View.GONE);

        mImageViewLock.setImageResource((mGameLocked[mCurrentGame])
                ? R.drawable.ic_lock
                : R.drawable.ic_lock_open);
    }

    private void setToolbarEnabled(boolean enabled) {
        if (enabled) {
            mImageViewClear.setVisibility(View.VISIBLE);
            mImageViewFoul.setVisibility(View.VISIBLE);
            mImageViewResetFrame.setVisibility(View.VISIBLE);
        } else {
            mImageViewClear.setVisibility(View.INVISIBLE);
            mImageViewFoul.setVisibility(View.INVISIBLE);
            mImageViewResetFrame.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Informs user with prompt that the game is current locked.
     */
    private void showGameLockedDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Invalid action!")
                .setMessage(R.string.dialog_game_locked)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts user to reset the current game.
     */
    private void showResetGameDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Reset Game?")
                .setMessage(R.string.dialog_reset_game)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Resets and saves the current game
                        resetGame();
                        saveGame(true);
                        setGameLocked(false);
                        mMatchPlay = 0;
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
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Sets mImageViewMatchPlay image resource depending on match play results set by user.
     */
    private void setMatchPlay() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (mMatchPlay) {
                    case Constants.MATCH_PLAY_NONE:
                        mImageViewMatchPlay.setImageResource(R.drawable.ic_match_none);
                        break;
                    case Constants.MATCH_PLAY_WON:
                        mImageViewMatchPlay.setImageResource(R.drawable.ic_match_win);
                        break;
                    case Constants.MATCH_PLAY_LOST:
                        mImageViewMatchPlay.setImageResource(R.drawable.ic_match_lose);
                        break;
                    case Constants.MATCH_PLAY_TIED:
                        mImageViewMatchPlay.setImageResource(R.drawable.ic_match_tie);
                        break;
                    default: // do nothing
                }
            }
        });
    }

    /**
     * Locks or unlocks a game and hides/shows settings which are only necessary if a game is unlocked.
     *
     * @param lock if true, settings will be hidden and game locked
     */
    private void setGameLocked(boolean lock) {
        mGameLocked[mCurrentGame] = lock;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setToolbarEnabled(!mGameLocked[mCurrentGame]);
                mImageViewLock.setImageResource((mGameLocked[mCurrentGame])
                        ? R.drawable.ic_lock
                        : R.drawable.ic_lock_open);
            }
        });
    }

    /**
     * Copies data of current game to variables and saves game to the database on a new thread.
     *
     * @param ignoreManualScore if false, game will only be saved if a manual score is not set. Otherwise, will save
     * regardless.
     */
    private void saveGame(boolean ignoreManualScore) {
        if ((!ignoreManualScore && mManualScoreSet[mCurrentGame]) || mDoNotSave.get())
            return;

        long[] framesToSave = new long[Constants.NUMBER_OF_FRAMES];
        boolean[] accessToSave = new boolean[Constants.NUMBER_OF_FRAMES];
        boolean[][][] pinStateToSave = new boolean[Constants.NUMBER_OF_FRAMES][3][5];
        boolean[][] foulsToSave = new boolean[Constants.NUMBER_OF_FRAMES][3];
        System.arraycopy(
                mFrameIds,
                mCurrentGame * Constants.NUMBER_OF_FRAMES,
                framesToSave,
                0,
                Constants.NUMBER_OF_FRAMES);
        System.arraycopy(
                mHasFrameBeenAccessed,
                0,
                accessToSave,
                0,
                Constants.NUMBER_OF_FRAMES);
        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++) {
            for (byte j = 0; j < mPinState[i].length; j++) {
                System.arraycopy(
                        mPinState[i][j],
                        0,
                        pinStateToSave[i][j],
                        0,
                        mPinState[i][j].length);
            }
            System.arraycopy(mFouls[i], 0, foulsToSave[i], 0, mFouls[i].length);
        }

        ((MainActivity) getActivity()).addSavingThread(
                saveGameToDatabase((MainActivity) getActivity(),
                        mGameIds[mCurrentGame],
                        framesToSave,
                        accessToSave,
                        pinStateToSave,
                        foulsToSave,
                        mGameScoresMinusFouls[mCurrentGame],
                        mGameLocked[mCurrentGame],
                        mManualScoreSet[mCurrentGame],
                        mMatchPlay));
    }

    /**
     * Resets a game to its original state with 0 score, 0 fouls.
     */
    private void resetGame() {
        clearFrameColor();
        mCurrentBall = 0;
        mCurrentFrame = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++) {
            mHasFrameBeenAccessed[i] = false;
            for (int j = 0; j < 3; j++) {
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
     * Calculates the highest score possible from the current state of the game and displays it in a dialog to the
     * user.
     */
    private void showBestPossibleScoreDialog() {
        byte initialFrame = mCurrentFrame;
        while (initialFrame > 0 && TextUtils.isEmpty(mTextViewFrames[initialFrame].getText().toString()))
            initialFrame--;

        StringBuilder alertMessageBuilder = new StringBuilder("If you get");
        short possibleScore;
        try {
            possibleScore = Short.parseShort(mTextViewFrames[initialFrame].getText().toString());
        } catch (NumberFormatException ex) {
            possibleScore = 0;
        }

        if (mCurrentFrame < Constants.LAST_FRAME) {
            if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN)) {
                int firstBallNextFrame = Score.getValueOfFrame(mPinState[mCurrentFrame + 1][0], true);
                possibleScore -= firstBallNextFrame;
                if (firstBallNextFrame == 15) {
                    if (mCurrentFrame < Constants.LAST_FRAME - 1)
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 2][0], true);
                    else
                        possibleScore -= Score.getValueOfFrame(mPinState[mCurrentFrame + 1][1], true);
                } else
                    possibleScore -= Score.getValueOfFrameDifference(mPinState[mCurrentFrame][0],
                            mPinState[mCurrentFrame][1]);
            } else if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN)) {
                int firstBallNextFrame = Score.getValueOfFrame(mPinState[mCurrentFrame + 1][0], true);
                possibleScore -= firstBallNextFrame;
            }
        } else {
            for (int i = mCurrentBall + 1; i < 3; i++)
                possibleScore -= Score.getValueOfFrameDifference(mPinState[mCurrentFrame][i - 1],
                        mPinState[mCurrentFrame][i]);
        }

        int pinsLeftStanding = Score.getValueOfFrame(mPinState[mCurrentFrame][mCurrentBall], false);

        boolean strikeLastFrame = false;
        boolean strikeTwoFramesAgo = false;
        boolean spareLastFrame = false;

        if (mCurrentFrame > 0) {
            if (Arrays.equals(mPinState[mCurrentFrame - 1][0], Constants.FRAME_PINS_DOWN)) {
                strikeLastFrame = true;
                if (mCurrentFrame > 1 && Arrays.equals(
                        mPinState[mCurrentFrame - 2][0], Constants.FRAME_PINS_DOWN))
                    strikeTwoFramesAgo = true;
            } else {
                if (Arrays.equals(mPinState[mCurrentFrame - 1][1], Constants.FRAME_PINS_DOWN))
                    spareLastFrame = true;
            }
        }

        if (mCurrentBall == 0) {
            alertMessageBuilder.append(" a strike");
            possibleScore += pinsLeftStanding + 30;
            int secondBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][0],
                    mPinState[mCurrentFrame][1]);
            int thirdBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][1],
                    mPinState[mCurrentFrame][2]);

            if (mCurrentFrame < Constants.LAST_FRAME)
                possibleScore -= secondBall + thirdBall;
            if (strikeLastFrame) {
                possibleScore -= secondBall;
                possibleScore += pinsLeftStanding + 15;
                if (strikeTwoFramesAgo)
                    possibleScore += pinsLeftStanding;
            } else if (spareLastFrame)
                possibleScore += pinsLeftStanding;
        } else if (mCurrentBall == 1) {
            int thirdBall = Score.getValueOfFrameDifference(mPinState[mCurrentFrame][1],
                    mPinState[mCurrentFrame][2]);
            if (mCurrentFrame < Constants.LAST_FRAME)
                possibleScore -= thirdBall;

            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][0],
                    Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" a spare");
            possibleScore += pinsLeftStanding + 15;
            if (strikeLastFrame)
                possibleScore += pinsLeftStanding;
        } else {
            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][1],
                    Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" fifteen");
            possibleScore += pinsLeftStanding;
        }
        possibleScore += 45 * (Constants.LAST_FRAME - mCurrentFrame);

        for (int i = 0; i <= mCurrentFrame; i++) {
            for (int j = 0; j < 3 && !(i == mCurrentFrame && j >= mCurrentBall); j++) {
                if (mFouls[i][j])
                    possibleScore -= 15;
            }
        }
        if (possibleScore < 0)
            possibleScore = 0;

        alertMessageBuilder.append(" this ball, and strikes onwards, your final score will be ");
        alertMessageBuilder.append(possibleScore);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(alertMessageBuilder.toString())
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create()
                .show();
    }

    /**
     * Sets the visibility of text and image views which indicate available navigation options in the fragment,
     * depending on whether they are available at the time.
     */
    private void setVisibilityOfNextAndPrevItems() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentFrame == 0 && mCurrentBall == 0) {
                    mTextViewPrevBall.setVisibility(View.GONE);
                    mImageViewPrevBall.setVisibility(View.GONE);
                    setFloatingActionButtonState(false, 0);
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) mTextViewGameNumber.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewNextBall.getId());
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewNextBall.getId());
                } else {
                    mTextViewPrevBall.setVisibility(View.VISIBLE);
                    mImageViewPrevBall.setVisibility(View.VISIBLE);
                    setFloatingActionButtonState(false, R.drawable.ic_chevron_left_black_24dp);
                }

                if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2) {
                    mTextViewNextBall.setVisibility(View.GONE);
                    mImageViewNextBall.setVisibility(View.GONE);
                    setFloatingActionButtonState(true, 0);
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) mTextViewGameNumber.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, mTextViewPrevBall.getId());
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mTextViewPrevBall.getId());
                } else {
                    mTextViewNextBall.setVisibility(View.VISIBLE);
                    mImageViewNextBall.setVisibility(View.VISIBLE);
                    setFloatingActionButtonState(true, R.drawable.ic_chevron_right_black_24dp);
                }
            }
        });
    }

    /**
     * Sets text of all TextView instances which display individual ball values.
     */
    private void updateAllBalls() {
        for (byte i = Constants.LAST_FRAME; i >= 0; i -= 3)
            updateBalls(i, (byte) 0);
    }

    /**
     * Sets the text of the three TextView instances which correspond to frameToUpdate.
     *
     * @param frameToUpdate frame of which text should be updated
     * @param leftToUpdate number of frames to the left to update
     */
    private void updateBalls(final byte frameToUpdate, final byte leftToUpdate) {
        if (frameToUpdate < 0 || frameToUpdate > Constants.LAST_FRAME)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Sets text depending on state of pins in the frame
                final String[] ballString = new String[3];
                if (mHasFrameBeenAccessed[frameToUpdate]) {
                    if (frameToUpdate == Constants.LAST_FRAME) // Treat last frame differently
                    {
                        if (Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN)) {
                            // If first ball is a strike, next two can be strikes/spares
                            ballString[0] = Constants.BALL_STRIKE;
                            if (Arrays.equals(mPinState[frameToUpdate][1],
                                    Constants.FRAME_PINS_DOWN)) {
                                ballString[1] = Constants.BALL_STRIKE;
                                ballString[2] = Score.getValueOfBall(
                                        mPinState[frameToUpdate][2], 2, true, false);
                            } else {
                                ballString[1] = Score.getValueOfBall(
                                        mPinState[frameToUpdate][1], 1, false, false);
                                if (Arrays.equals(mPinState[frameToUpdate][2],
                                        Constants.FRAME_PINS_DOWN))
                                    ballString[2] = Constants.BALL_SPARE;
                                else
                                    ballString[2] = Score.getValueOfBallDifference(
                                            mPinState[frameToUpdate], 2, false, false);
                            }
                        } else {
                            // If first ball is not a strike, score is calculated normally
                            ballString[0] = Score.getValueOfBall(
                                    mPinState[frameToUpdate][0], 0, false, false);
                            if (Arrays.equals(mPinState[frameToUpdate][1],
                                    Constants.FRAME_PINS_DOWN)) {
                                ballString[1] = Constants.BALL_SPARE;
                                ballString[2] = Score.getValueOfBall(
                                        mPinState[frameToUpdate][2], 2, true, true);
                            } else {
                                ballString[1] = Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate], 1, false, false);
                                ballString[2] = Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate], 2, false, false);
                            }
                        }
                    } else {
                        ballString[0] = Score.getValueOfBallDifference(
                                mPinState[frameToUpdate], 0, false, false);
                        if (!Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN)) {
                            if (Arrays.equals(mPinState[frameToUpdate][1],
                                    Constants.FRAME_PINS_DOWN)) {
                                ballString[1] = Constants.BALL_SPARE;
                                ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 1])
                                        ? Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate + 1], 0, false, true)
                                        : Constants.BALL_EMPTY;
                            } else {
                                ballString[1] = Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate], 1, false, false);
                                ballString[2] = Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate], 2, false, false);
                            }
                        } else {
                            // Either displays pins knocked down in next frames
                            // or shows empty frames
                            if (mHasFrameBeenAccessed[frameToUpdate + 1]) {
                                ballString[1] = Score.getValueOfBallDifference(
                                        mPinState[frameToUpdate + 1], 0, false, true);
                                if (Arrays.equals(mPinState[frameToUpdate + 1][0],
                                        Constants.FRAME_PINS_DOWN)) {
                                    if (frameToUpdate < Constants.LAST_FRAME - 1) {
                                        ballString[2] = (mHasFrameBeenAccessed[frameToUpdate + 2])
                                                ? Score.getValueOfBallDifference(
                                                mPinState[frameToUpdate + 2], 0, false, true)
                                                : Constants.BALL_EMPTY;
                                    } else {
                                        ballString[2] = Score.getValueOfBall(
                                                mPinState[frameToUpdate + 1][1], 1, false, true);
                                    }
                                } else {
                                    ballString[2] = Score.getValueOfBallDifference(
                                            mPinState[frameToUpdate + 1], 1, false, true);
                                }
                            } else {
                                ballString[1] = Constants.BALL_EMPTY;
                                ballString[2] = Constants.BALL_EMPTY;
                            }
                        }
                    }
                } else {
                    ballString[0] = "";
                    ballString[1] = "";
                    ballString[2] = "";
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean highlightStrikesAndSpares = PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getBoolean(Constants.KEY_ENABLE_STRIKE_HIGHLIGHTS, true);
                        for (byte i = 0; i < 3; i++) {
                            mTextViewBallScores[frameToUpdate][i].setText(ballString[i]);
                            if (highlightStrikesAndSpares) {
                                if (frameToUpdate == Constants.LAST_FRAME
                                        && Arrays.equals(mPinState[frameToUpdate][i], Constants.FRAME_PINS_DOWN)
                                        && !(i == 2 && !Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN)
                                        && !Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN)))
                                    mTextViewBallScores[frameToUpdate][i].setTextColor(Theme.getTertiaryThemeColor());
                                else if ((i == 0 && Arrays.equals(mPinState[frameToUpdate][i],
                                        Constants.FRAME_PINS_DOWN))
                                        || (i == 1 && Arrays.equals(mPinState[frameToUpdate][i],
                                        Constants.FRAME_PINS_DOWN)
                                        && !Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN)))
                                    mTextViewBallScores[frameToUpdate][i].setTextColor(Theme.getTertiaryThemeColor());
                                else
                                    mTextViewBallScores[frameToUpdate][i].setTextColor(DisplayUtils.COLOR_BLACK);
                            } else {
                                mTextViewBallScores[frameToUpdate][i].setTextColor(DisplayUtils.COLOR_BLACK);
                            }

                            mTextViewFouls[frameToUpdate][i].setText(
                                    (mFouls[frameToUpdate][i])
                                            ? "F"
                                            : null);
                        }
                    }
                });

                // Updates previous frames as well, to display balls after strikes
                switch (leftToUpdate) {
                    case 0:
                        updateBalls((byte) (frameToUpdate - 1), (byte) (leftToUpdate + 1));
                        break;
                    case 1:
                        updateBalls((byte) (frameToUpdate - 1), (byte) (leftToUpdate + 1));
                        break;
                    default:
                        // do nothing
                }
            }
        }).start();
    }

    /**
     * Sets text of the TextView instances which display the score up to the frame to the user.
     */
    private void updateScore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Calculates and keeps running total of scores of each frame
                final short[] frameScores = new short[Constants.NUMBER_OF_FRAMES];
                for (byte f = Constants.LAST_FRAME; f >= 0; f--) {
                    if (f == Constants.LAST_FRAME) {
                        for (byte b = 2; b >= 0; b--) {
                            switch (b) {
                                case 2:
                                    frameScores[f] += Score.getValueOfFrame(mPinState[f][b], true);
                                    break;
                                case 1:
                                case 0:
                                    if (Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN)) {
                                        frameScores[f] += Score.getValueOfFrame(mPinState[f][b], true);
                                    }
                                    break;
                                default: // do nothing
                            }
                        }
                    } else {
                        for (byte b = 0; b < 3; b++) {
                            if (b < 2 && Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN)) {
                                frameScores[f] += Score.getValueOfFrame(mPinState[f][b], true);
                                frameScores[f] += Score.getValueOfFrame(mPinState[f + 1][0], true);
                                if (b == 0) {
                                    if (f == Constants.LAST_FRAME - 1) {
                                        if (frameScores[f] == 30) {
                                            frameScores[f] +=
                                                    Score.getValueOfFrame(mPinState[f + 1][1], true);
                                        } else {
                                            frameScores[f] +=
                                                    Score.getValueOfFrameDifference(
                                                            mPinState[f + 1][0],
                                                            mPinState[f + 1][1]);
                                        }
                                    } else if (frameScores[f] < 30) {
                                        frameScores[f] +=
                                                Score.getValueOfFrameDifference(
                                                        mPinState[f + 1][0], mPinState[f + 1][1]);
                                    } else {
                                        frameScores[f] +=
                                                Score.getValueOfFrame(mPinState[f + 2][0], true);
                                    }
                                }
                                break;
                            } else if (b == 2) {
                                frameScores[f] += Score.getValueOfFrame(mPinState[f][b], true);
                            }
                        }
                    }
                }

                short totalScore = 0;
                for (byte i = 0; i < frameScores.length; i++) {
                    totalScore += frameScores[i];
                    frameScores[i] = totalScore;
                }
                mGameScores[mCurrentGame] = totalScore;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Sets scores calculated from running total as text of TextViews
                        for (byte i = 0; i < frameScores.length; i++) {
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
     * Counts fouls of the frames and calculates scores minus 15 points for each foul, then sets score in last
     * TextView.
     */
    private void updateFouls() {
        byte foulCount = 0;
        for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++) {
            for (int j = 0; j < 3; j++) {
                if (mFouls[i][j])
                    foulCount++;
            }
        }
        short scoreWithFouls = (short) (mGameScores[mCurrentGame] - 15 * foulCount);
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        mGameScoresMinusFouls[mCurrentGame] = scoreWithFouls;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewFinalScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
                mImageViewFoul.setImageResource(
                        !mFouls[mCurrentFrame][mCurrentBall]
                                ? R.drawable.ic_foul_remove
                                : R.drawable.ic_foul);
                mTextViewFouls[mCurrentFrame][mCurrentBall]
                        .setText(mFouls[mCurrentFrame][mCurrentBall]
                                ? "F"
                                : "");
                if (mGameCallback != null)
                    mGameCallback.updateGameScore((byte) (mCurrentGame + 1),
                            mGameScoresMinusFouls[mCurrentGame]);
            }
        });
    }

    /**
     * Sets background color of current ball and frame TextView instances to mColorBackground.
     */
    private void clearFrameColor() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GradientDrawable drawable
                        = (GradientDrawable) mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(mColorBackground);
                drawable = (GradientDrawable) mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(mColorBackground);
            }
        });
    }

    /**
     * Sets background color of current ball and frame TextView instances to mColorHighlight and sets color of pin and
     * whether its enabled or not depending on its state.
     *
     * @param initialLoad indicates if this method was called when a game was first loaded
     */
    private void updateFrameColor(final boolean initialLoad) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GradientDrawable drawable
                        = (GradientDrawable) mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(mColorHighlight);
                drawable = (GradientDrawable) mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(mColorHighlight);

                // Sets images of pins to enabled/disabled depending on
                // if they were knocked down in current frame or a previous one
                int numberOfPinsStanding = 0;
                for (byte i = 0; i < 5; i++) {
                    if (mPinState[mCurrentFrame][mCurrentBall][i])
                        mImageViewPins[i].setImageResource(R.drawable.pin_disabled);
                    else {
                        mImageViewPins[i].setImageResource(R.drawable.pin_enabled);
                        numberOfPinsStanding++;
                    }

                    if (mCurrentBall > 0 && (mPinState[mCurrentFrame][mCurrentBall - 1][i])
                            && !(mCurrentFrame == Constants.LAST_FRAME
                            && Arrays.equals(mPinState[mCurrentFrame][mCurrentBall - 1],
                            Constants.FRAME_PINS_DOWN)))
                        mImageViewPins[i].setEnabled(false);
                    else
                        mImageViewPins[i].setEnabled(true);
                }

                if (mCurrentFrame == Constants.LAST_FRAME) {
                    switch (mCurrentBall) {
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
                        default: // do nothing
                    }
                } else {
                    switch (mCurrentBall) {
                        case 0:
                            mImageViewClear.setImageResource(R.drawable.ic_strike);
                            break;
                        case 1:
                            mImageViewClear.setImageResource(R.drawable.ic_spare);
                            break;
                        case 2:
                            mImageViewClear.setImageResource(R.drawable.ic_fifteen);
                            break;
                        default: // do nothing
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
     * Either sets a pin to be standing or knocked down, and updates the score accordingly.
     *
     * @param pinToSet the pin which was altered
     * @param updateScore indicates if score and text views should be updated. If altering more than one pin at a time,
     * this should only be true for the last call.
     */
    private void alterPinState(final byte pinToSet, final boolean updateScore) {
        final boolean isPinKnockedOver = mPinState[mCurrentFrame][mCurrentBall][pinToSet];
        final boolean allPinsKnockedOver;
        if (!isPinKnockedOver) {
            for (int i = mCurrentBall; i < 3; i++)
                mPinState[mCurrentFrame][i][pinToSet] = true;

            if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN)) {
                for (int i = mCurrentBall + 1; i < 3; i++)
                    mFouls[mCurrentFrame][i] = false;

                if (mCurrentFrame == Constants.LAST_FRAME) {
                    if (mCurrentBall < 2) {
                        for (int j = mCurrentBall + 1; j < 3; j++) {
                            for (int i = 0; i < 5; i++)
                                mPinState[mCurrentFrame][j][i] = false;
                        }
                    }
                }
                allPinsKnockedOver = true;
            } else
                allPinsKnockedOver = false;
        } else {
            allPinsKnockedOver = false;
            for (int i = mCurrentBall; i < 3; i++)
                mPinState[mCurrentFrame][i][pinToSet] = false;

            if (mCurrentFrame == Constants.LAST_FRAME) {
                if (mCurrentBall == 1) {
                    System.arraycopy(mPinState[mCurrentFrame][1],
                            0,
                            mPinState[mCurrentFrame][2],
                            0,
                            mPinState[mCurrentFrame][1].length);
                } else if (mCurrentBall == 0 && Score.countStandingPins(mPinState[mCurrentFrame][mCurrentBall]) == 4) {
                    System.arraycopy(mPinState[mCurrentFrame][0],
                            0,
                            mPinState[mCurrentFrame][1],
                            0,
                            mPinState[mCurrentFrame][0].length);
                    System.arraycopy(mPinState[mCurrentFrame][0],
                            0,
                            mPinState[mCurrentFrame][2],
                            0,
                            mPinState[mCurrentFrame][0].length);
                }
            }
        }

        mImageViewPins[pinToSet].post(new Runnable() {
            @Override
            public void run() {
                if (mPinState[mCurrentFrame][mCurrentBall][pinToSet])
                    mImageViewPins[pinToSet].setImageResource(R.drawable.pin_disabled);
                else
                    mImageViewPins[pinToSet].setImageResource(R.drawable.pin_enabled);
                mImageViewClear.setEnabled(!allPinsKnockedOver);
            }
        });

        if (updateScore) {
            updateBalls(mCurrentFrame, (byte) 0);
            updateScore();
        }
    }

    /**
     * Clears all the pins which are currently standing in the frame and updates the TextViews with new score.
     */
    private void clearPins() {
        if (mGameLocked[mCurrentGame] || mManualScoreSet[mCurrentGame])
            return;

        if (!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN)) {
            for (int j = mCurrentBall; j < 3; j++) {
                for (int i = 0; i < 5; i++) {
                    mPinState[mCurrentFrame][j][i] =
                            (mCurrentFrame != Constants.LAST_FRAME || (j == mCurrentBall));
                    if (j > mCurrentBall)
                        mFouls[mCurrentFrame][j] = false;
                }
            }

            updateBalls(mCurrentFrame, (byte) 0);
            updateScore();
            updateFrameColor(false);
        }
    }

    /**
     * Scrolls the position of hsvFrames so the current frame is centred.
     *
     * @param initialLoad indicates if this method was called when a game was first loaded
     */
    private void focusOnFrame(final boolean initialLoad) {
        mHorizontalScrollViewFrames.post(new Runnable() {
            @Override
            public void run() {
                if (initialLoad && mCurrentFrame == Constants.LAST_FRAME)
                    mHorizontalScrollViewFrames
                            .smoothScrollTo(mTextViewFrames[mCurrentFrame].getLeft(), 0);
                else if (mCurrentFrame >= 1)
                    mHorizontalScrollViewFrames
                            .smoothScrollTo(mTextViewFrames[mCurrentFrame - 1].getLeft(), 0);
                else
                    mHorizontalScrollViewFrames
                            .smoothScrollTo(mTextViewFrames[mCurrentFrame].getLeft(), 0);
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
            final byte matchPlay) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(srcActivity).getWritableDatabase();
                ContentValues values;

                database.beginTransaction();
                try {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_SCORE, finalScore);
                    values.put(GameEntry.COLUMN_IS_LOCKED,
                            (gameLocked
                                    ? 1
                                    : 0));
                    values.put(GameEntry.COLUMN_IS_MANUAL,
                            (manualScoreSet)
                                    ? 1
                                    : 0);
                    values.put(GameEntry.COLUMN_MATCH_PLAY, matchPlay);
                    database.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{String.valueOf(gameId)});

                    for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++) {
                        StringBuilder foulsOfFrame = new StringBuilder();
                        for (int ballCount = 0; ballCount < 3; ballCount++) {
                            if (fouls[i][ballCount])
                                foulsOfFrame.append(ballCount + 1);
                        }
                        if (foulsOfFrame.length() == 0)
                            foulsOfFrame.append(0);

                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_PIN_STATE[0],
                                Score.booleanFrameToInt(pinState[i][0]));
                        values.put(FrameEntry.COLUMN_PIN_STATE[1],
                                Score.booleanFrameToInt(pinState[i][1]));
                        values.put(FrameEntry.COLUMN_PIN_STATE[2],
                                Score.booleanFrameToInt(pinState[i][2]));
                        values.put(FrameEntry.COLUMN_IS_ACCESSED,
                                (hasFrameBeenAccessed[i])
                                        ? 1
                                        : 0);
                        values.put(FrameEntry.COLUMN_FOULS,
                                Score.foulStringToInt(foulsOfFrame.toString()));
                        database.update(FrameEntry.TABLE_NAME,
                                values,
                                FrameEntry._ID + "=?",
                                new String[]{String.valueOf(frameIds[i])});
                    }
                    database.setTransactionSuccessful();
                } catch (Exception ex) {
                    throw new RuntimeException("Fatal error: Game could not save. "
                            + ex.getMessage());
                } finally {
                    database.endTransaction();
                }
            }
        });
    }

    /**
     * Loads a game from the database to member variables.
     *
     * @param newGame index of id in mGameIds to load
     */
    private void loadGameFromDatabase(final byte newGame) {
        clearFrameColor();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.waitForSaveThreads(new WeakReference<>((MainActivity) getActivity()));

                mCurrentGame = newGame;
                if (mGameCallback != null)
                    mGameCallback.onGameChanged(mCurrentGame);
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(getContext()).getReadableDatabase();

                Cursor cursor = null;
                try {
                    cursor = database.query(GameEntry.TABLE_NAME,
                            new String[]{GameEntry.COLUMN_MATCH_PLAY},
                            GameEntry._ID + "=?",
                            new String[]{String.valueOf(mGameIds[mCurrentGame])},
                            null,
                            null,
                            null);
                    if (cursor.moveToFirst()) {
                        mMatchPlay = (byte) cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));
                    }

                    cursor = database.query(FrameEntry.TABLE_NAME,
                            new String[]{
                                    FrameEntry.COLUMN_IS_ACCESSED,
                                    FrameEntry.COLUMN_PIN_STATE[0],
                                    FrameEntry.COLUMN_PIN_STATE[1],
                                    FrameEntry.COLUMN_PIN_STATE[2],
                                    FrameEntry.COLUMN_FOULS
                            },
                            FrameEntry.COLUMN_GAME_ID + "=?",
                            new String[]{String.valueOf(mGameIds[mCurrentGame])},
                            null,
                            null,
                            FrameEntry.COLUMN_FRAME_NUMBER);

                    mFouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
                    byte currentFrameIterator = 0;
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            byte frameAccessed = (byte) cursor.getInt(
                                    cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED));
                            mHasFrameBeenAccessed[currentFrameIterator] = (frameAccessed == 1);
                            for (int i = 0; i < 3; i++) {
                                int ball = cursor.getInt(cursor.getColumnIndex(
                                        FrameEntry.COLUMN_PIN_STATE[i]));
                                boolean[] ballBoolean = Score.ballIntToBoolean(ball);
                                mPinState[currentFrameIterator][i] = ballBoolean;
                            }
                            String fouls = Score.foulIntToString(cursor.getInt(
                                    cursor.getColumnIndex(FrameEntry.COLUMN_FOULS)));
                            for (int ballCount = 0; ballCount < 3; ballCount++) {
                                mFouls[currentFrameIterator][ballCount]
                                        = fouls.contains(String.valueOf(ballCount + 1));
                            }

                            currentFrameIterator++;
                            cursor.moveToNext();
                        }
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearAllText(!mManualScoreSet[mCurrentGame]);
                        getActivity().supportInvalidateOptionsMenu();
                        mTextViewGameNumber.setText(
                                String.format(getResources().getString(R.string.text_game_placeholder),
                                        mCurrentGame + 1));
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

                while (mCurrentFrame < Constants.LAST_FRAME
                        && mHasFrameBeenAccessed[mCurrentFrame + 1])
                    mCurrentFrame++;

                setVisibilityOfNextAndPrevItems();
                updateFrameColor(true);
            }
        }).start();
    }

    /**
     * Sets the icon of one of the floating action buttons.
     *
     * @param nextFab if true, the icon of the "next ball" fab is set. If false, the "previous ball" fab icon is set.
     * @param drawableId icon for fab
     */
    private void setFloatingActionButtonState(boolean nextFab, int drawableId) {
        if (nextFab && mNextFab != null)
            mNextFab.animate(drawableId, Theme.getPrimaryThemeColor(), Theme.getTertiaryThemeColor());
        else if (!nextFab && mPrevFab != null)
            mPrevFab.animate(drawableId, Theme.getPrimaryThemeColor(), Theme.getTertiaryThemeColor());
    }

    /**
     * Saves current game and loads new game from database.
     *
     * @param gameNumber index in mGameIds to load from database
     */
    public void switchGame(byte gameNumber) {
        if (gameNumber == mCurrentGame)
            return;

        saveGame(false);
        loadGameFromDatabase(gameNumber);
    }

    /**
     * Loads the initial scores for the games being displayed from the database so they can be shown and updated.
     */
    private void loadInitialScores() {
        MainActivity.waitForSaveThreads(new WeakReference<>((MainActivity) getActivity()));

        byte numberOfGames = ((MainActivity) getActivity()).getNumberOfGamesForSeries();
        SQLiteDatabase database = DatabaseHelper.getInstance(getContext()).getReadableDatabase();
        StringBuilder whereBuilder = new StringBuilder(GameEntry._ID + "=?");
        String[] whereArgs = new String[numberOfGames];
        whereArgs[0] = String.valueOf(mGameIds[0]);
        for (byte i = 1; i < numberOfGames; i++) {
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
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                mGameScoresMinusFouls[currentGamePosition++] = gameScore;
                cursor.moveToNext();
            }
        } else {
            mDoNotSave.set(true);
            throw new RuntimeException("Fatal error: could not load initial scores.");
        }
        cursor.close();
    }

    public void externalStoragePermissionGranted() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            ShareUtils.showShareDialog(mainActivity, mainActivity.getSeriesId());
    }

    /**
     * Gets the current game being edited.
     *
     * @return value of mCurrentGame
     */
    public byte getCurrentGame() {
        return mCurrentGame;
    }

    /**
     * Callback interface offers methods upon user interaction.
     */
    public interface GameFragmentCallback {

        /**
         * Tells activity to open new StatsFragment with current game id and game number.
         *
         * @param gameId id of the game to display
         * @param gameNumber number in a series of the game to display
         */
        void onGameStatsOpened(long gameId, byte gameNumber);

        /**
         * Tells activity to open new StatsFragment with current series.
         */
        void onSeriesStatsOpened();

        /**
         * Tells activity that the game has been changed.
         *
         * @param newGameNumber number of the new game, starting at index 0
         */
        void onGameChanged(byte newGameNumber);

        /**
         * Sets auto advance values in activity.
         *
         * @param clickToAdvance view to click to advance ball
         * @param textViewStatus text view to display updates
         * @param enabled indicates if the timer should be enabled
         * @param delay seconds of inactivity before auto advancing
         */
        void setAutoAdvanceConditions(View clickToAdvance,
                                      TextView textViewStatus,
                                      boolean enabled,
                                      int delay);

        /**
         * Starts the auto advance timer.
         */
        void resetAutoAdvanceTimer();

        /**
         * Stops the auto advance timer.
         */
        void stopAutoAdvanceTimer();

        /**
         * Invoked when a score is updated.
         *
         * @param gameNumber number of the game
         * @param gameScore score of the game
         */
        void updateGameScore(byte gameNumber, short gameScore);

        /**
         * Loads the game scores from the database and sets them in the navigation drawer.
         *
         * @param gameIds ids of games to load
         */
        void loadGameScoresForDrawer(long[] gameIds);
    }

    /**
     * Creates a new instance and sets parameters as arguments for the instance.
     *
     * @param gameIds ids of the games being displayed
     * @param frameIds ids of frames belonging to gameIds
     * @param gameLocked whether the games being displayed are locked or not
     * @param manualScore whether the games being displayed have manual scores set
     * @return the newly created instance
     */
    public static GameFragment newInstance(long[] gameIds,
                                           long[] frameIds,
                                           boolean[] gameLocked,
                                           boolean[] manualScore) {
        GameFragment gameFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putLongArray(Constants.EXTRA_ARRAY_GAME_IDS, gameIds);
        args.putLongArray(Constants.EXTRA_ARRAY_FRAME_IDS, frameIds);
        args.putBooleanArray(Constants.EXTRA_ARRAY_GAME_LOCKED, gameLocked);
        args.putBooleanArray(Constants.EXTRA_ARRAY_MANUAL_SCORE_SET, manualScore);
        gameFragment.setArguments(args);
        return gameFragment;
    }
}
