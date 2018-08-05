package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.games.dialogs.ResetGameDialog
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.views.FrameView
import ca.josephroque.bowlingcompanion.games.views.GameFooterView
import ca.josephroque.bowlingcompanion.games.views.GameHeaderView
import ca.josephroque.bowlingcompanion.games.views.PinLayout
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.android.synthetic.main.fragment_game.game_footer as gameFooter
import kotlinx.android.synthetic.main.fragment_game.game_header as gameHeader
import kotlinx.android.synthetic.main.fragment_game.hsv_frames as hsvFrames
import kotlinx.android.synthetic.main.fragment_game.pin_layout as pinLayout
import kotlinx.android.synthetic.main.fragment_game.tv_final_score as finalScore
import kotlinx.android.synthetic.main.fragment_game.tv_auto_advance as autoAdvance
import kotlinx.android.synthetic.main.sheet_match_play.sheet_match_play as matchPlaySheet
import kotlinx.android.synthetic.main.sheet_match_play.view.*
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display game details and allow the user to edit.
 */
class GameFragment : BaseFragment(),
        IFloatingActionButtonHandler,
        FrameView.FrameInteractionDelegate,
        PinLayout.PinLayoutInteractionDelegate,
        GameFooterView.GameFooterInteractionDelegate,
        GameHeaderView.GameHeaderInteractionDelegate,
        MatchPlaySheet.MatchPlaySheetDelegate {

    /** Interaction listener. */
    private var listener: OnGameFragmentInteractionListener? = null

    /** IDs for frame views. */
    private val frameViewIds = intArrayOf(R.id.frame_0, R.id.frame_1, R.id.frame_2, R.id.frame_3,
            R.id.frame_4, R.id.frame_5, R.id.frame_6, R.id.frame_7, R.id.frame_8, R.id.frame_9)

    /** Frame view instances. */
    private lateinit var frameViews: Array<FrameView?>

    /** The number of the current game in its series. */
    var gameNumber: Int = 0
        set(value) {
            saveCurrentGame(false)
            field = value
            gameHeader.currentGame = gameNumber
            gameState.currentGameIdx = gameNumber
            render(ballChanged = true, isGameFirstRender = true)

            if (gameState.currentGame.isLocked) {
                autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
                autoEventController.disable(GameAutoEventController.AutoEvent.AdvanceFrame)
            } else {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                autoEventController.init(preferences)
            }
        }

    /** The series being edited. */
    private var series: Series? = null

    /** Manage the state of the current game. */
    private lateinit var gameState: GameState

    /** Manage the automatic game events. */
    private lateinit var autoEventController: GameAutoEventController

    /** Indicates if the current ball is the last ball prior to ball changes. */
    private var wasLastBall: Boolean = false

    // MARK: Overrides

    /** @Override */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        series = arguments?.getParcelable(ARG_SERIES)
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        frameViews = arrayOfNulls(frameViewIds.size)
        frameViewIds.forEachIndexed { index, it ->
            frameViews[index] = view.findViewById(it)
        }

        setupBottomSheet(view)

        return view
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable automatic events
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        autoEventController = GameAutoEventController(preferences, autoEventDelegate)
        autoEventController.pauseAll()
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_game, menu)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? OnGameFragmentInteractionListener ?: throw RuntimeException("${parentFragment!!} must implement OnGameFragmentInteractionListener")
        listener = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onStart() {
        super.onStart()

        frameViews.forEach { it?.delegate = this }
        pinLayout.delegate = this
        gameFooter.delegate = this
        gameHeader.delegate = this
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        val context = context ?: return
        series?.let {
            gameState = GameState(it, gameStateListener)
            gameState.loadGames(context)
        }

        // Enable automatic events
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        autoEventController.init(preferences)

        onBallSelected(0, 0)
    }

    /** @Override */
    override fun onPause() {
        super.onPause()
        autoEventController.pauseAll()
        context?.let { gameState.saveGame(WeakReference(it), true) }
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        autoEventController.pauseAll()
        return when (item?.itemId) {
            R.id.action_reset_game -> {
                showResetGameDialog()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Set behaviour and appearance of bottom sheet.
     */
    private fun setupBottomSheet(rootView: View) {
        val bottomSheet = rootView.sheet_match_play
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /**
     * Refresh the UI.
     *
     * @param ballChanged only focus on the next frame if the current ball changed
     * @param isGameFirstRender if this is the first render of a game
     */
    private fun render(ballChanged: Boolean = false, isGameFirstRender: Boolean = false) {
        if (!gameState.gamesLoaded) { return }
        launch(Android) {
            val scoreText = gameState.currentGame.getScoreTextForFrames().await()
            val ballText = gameState.currentGame.getBallTextForFrames().await()

            // Update active frames
            frameViews.forEachIndexed { index, it ->
                it?.isCurrentFrame = (index == gameState.currentFrameIdx)
                it?.currentBall = gameState.currentBallIdx
            }

            // Update scores of the frames
            scoreText.forEachIndexed { index, score ->
                frameViews[index]?.setFrameText(score)
            }
            finalScore.text = gameState.currentGame.score.toString()

            // Update balls of the frames
            ballText.forEachIndexed { index, balls ->
                balls.forEachIndexed { ballIdx, ball ->
                    frameViews[index]?.setBallText(ballIdx, ball)
                }
            }

            // Update fouls of the frames
            gameState.currentGame.frames.forEachIndexed { index, frame ->
                frame.ballFouled.forEachIndexed { ballIdx, foul ->
                    frameViews[index]?.setFoulEnabled(ballIdx, foul)
                }
            }

            // Update up/down pins
            gameState.currentPinState.forEachIndexed { index, pin ->
                pinLayout.updatePinImage(index, pin.isDown)
            }

            // Set which pins are enabled/disabled
            gameState.enabledPins.forEach { pinLayout.setPinEnabled(it, true) }
            gameState.disabledPins.forEach { pinLayout.setPinEnabled(it, false) }

            // Set icons in game footer
            gameFooter.apply {
                isFoulActive = gameState.currentFrame.ballFouled[gameState.currentBallIdx]
                isGameLocked = gameState.currentGame.isLocked
                matchPlayResult = gameState.currentGame.matchPlay.result
                clearIcon = when {
                    gameState.currentBallIdx == 0 ||
                        (gameState.isLastFrame && gameState.currentBallIdx == 1 && gameState.currentFrame.pinState[0].arePinsCleared()) ||
                        (gameState.isLastFrame && gameState.currentBallIdx == 2 && gameState.currentFrame.pinState[1].arePinsCleared()) -> {
                        R.drawable.ic_clear_pins_strike
                    }
                    gameState.currentBallIdx == 1 ||
                        (gameState.currentBallIdx == 2 && gameState.currentFrame.pinState[0].arePinsCleared()) -> {
                        R.drawable.ic_clear_pins_spare
                    }
                    else -> R.drawable.ic_clear_pins_fifteen
                }
            }

            if (ballChanged || isGameFirstRender) {
                focusOnFrame(isGameFirstRender)
            }
        }
    }

    /**
     * Save the current frame of the game state to the database.
     *
     * @param ignoreManualScore ignore any manual score set and save the frame
     */
    private fun saveCurrentFrame(ignoreManualScore: Boolean) {
        context?.let { gameState.saveFrame(WeakReference(it), ignoreManualScore) }
    }

    /**
     * Save the current game of the game state to the database.
     *
     * @param ignoreManualScore ignore any manual score set and save the game
     */
    private fun saveCurrentGame(ignoreManualScore: Boolean) {
        context?.let { gameState.saveGame(WeakReference(it), ignoreManualScore) }
    }

    /**
     * Save the match play results for the game.
     */
    private fun saveMatchPlay() {
        context?.let { gameState.saveMatchPlay(WeakReference(it)) }
    }

    /**
     * Scrolls the position of the frames so the current frame is 1 from the left, or at least visible.
     *
     * @param isGameFirstRender indicates if this method was called on the game's first load
     */
    private fun focusOnFrame(isGameFirstRender: Boolean) {
        val left = if (gameState.currentFrameIdx >= 1 && !(isGameFirstRender && gameState.isLastFrame)) {
            val prevFrame = frameViews[gameState.currentFrameIdx - 1] ?: return
            prevFrame.left
        } else {
            val frame = frameViews[gameState.currentFrameIdx] ?: return
            frame.left
        }
        hsvFrames.post { hsvFrames.smoothScrollTo(left, 0) }
    }

    /**
     * Reset the current game to a new state.
     */
    private fun resetGame() {
        context?.let { gameState.resetGame(WeakReference(it)) }
    }

    // MARK: IFloatingActionButtonHandler

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward
    }

    /** @Override */
    override fun onFabClick() {
        onNextBall()
    }

    // MARK: FrameInteractionDelegate

    /** @Override */
    override fun onBallSelected(ball: Int, frame: Int) {
        if (gameState.gamesLoaded) { saveCurrentFrame(false) }
        gameState.attemptToSetFrameAndBall(frame, ball)
    }

    /** @Override */
    override fun onFrameSelected(frame: Int) {
        onBallSelected(0, frame)
    }

    // MARK: PinLayoutInteractionDelegate

    /** @Override */
    override fun isPinDown(pin: Int): Boolean {
        return if (gameState.gamesLoaded) gameState.currentPinState[pin].isDown else false
    }

    /** @Override */
    override fun setPins(pins: IntArray, isDown: Boolean) {
        gameState.setPins(pins, isDown)
        if (gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.Lock) }
        autoEventController.delay(GameAutoEventController.AutoEvent.AdvanceFrame)
        render()
    }

    // MARK: GameFooterInteractionDelegate

    /** @Override */
    override fun onClearPins() {
        setPins((0 until Game.NUMBER_OF_PINS).toList().toIntArray(), true)
    }

    /** @Override */
    override fun onFoulToggle() {
        val frameView = frameViews[gameState.currentFrameIdx] ?: return
        gameState.toggleFoul()
        frameView.setFoulEnabled(gameState.currentBallIdx, gameState.currentBallFouled)
        if (gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.Lock) }
        autoEventController.delay(GameAutoEventController.AutoEvent.AdvanceFrame)
        render()
    }

    /** @Override */
    override fun onLockToggle() {
        gameState.toggleLock()
        autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
        autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
        saveCurrentGame(false)
        render()
    }

    /** @Override */
    override fun onMatchPlaySettings() {
        val fragment = MatchPlaySheet.newInstance()
        fragmentNavigation?.showBottomSheet(fragment, MatchPlaySheet.FRAGMENT_TAG)
        autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
    }

    // MARK: GameHeaderInteractionDelegate

    /** @Override */
    override fun onNextBall() {
        saveCurrentFrame(false)
        gameState.nextBall()
        // TODO: change bowler if necessary
    }

    /** @Override */
    override fun onPrevBall() {
        saveCurrentFrame(false)
        gameState.prevBall()
    }

    // MARK: MatchPlaySheetDelegate

    /** @Override */
    override fun onFinishedSettingMatchPlayResults(
        opponentName: String,
        opponentScore: Int,
        matchPlayResult: MatchPlayResult,
        inputValid: Boolean
    ) {
        if (!inputValid) {
            val sheetBehavior = BottomSheetBehavior.from(matchPlaySheet)
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return
        }

        gameState.setMatchPlay(opponentName, opponentScore, matchPlayResult)
        saveMatchPlay()
        render()
    }

    // MARK: GameStateListener

    /** Handle events from game state changes. */
    private val gameStateListener = object : GameState.GameStateListener {
        /** @Override */
        override fun onGamesLoaded() {
            if (gameState.currentGame.isLocked) {
                autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
                autoEventController.disable(GameAutoEventController.AutoEvent.AdvanceFrame)
            }
            gameState.currentFrame.isAccessed = true
            render(ballChanged = true, isGameFirstRender = true)
        }

        /** @Override */
        override fun onBallChanged() {
            if (wasLastBall && !gameState.isLastBall) {
                listener?.enableFab(true)
            } else if (!wasLastBall && gameState.isLastBall) {
                listener?.enableFab(false)
            }

            wasLastBall = gameState.isLastBall
            gameHeader.currentFrame = gameState.currentFrameIdx
            gameHeader.currentBall = gameState.currentBallIdx
            if (gameState.isLastBall) {
                autoEventController.delay(GameAutoEventController.AutoEvent.Lock)
            } else {
                autoEventController.pause(GameAutoEventController.AutoEvent.Lock)
            }
            autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
            render(ballChanged = true)
        }
    }

    // MARK: GameAutoEventDelegate

    /** Handle events from auto event controller. */
    private val autoEventDelegate = object : GameAutoEventController.GameAutoEventDelegate {
        /** @Override */
        override fun autoAdvanceCountDown(secondsRemaining: Int) {
            val autoAdvanceStringId = if (gameState.frameHasNextBall) R.plurals.time_until_auto_advance_ball else R.plurals.time_until_auto_advance_frame
            autoAdvance.text = resources.getQuantityString(
                autoAdvanceStringId,
                secondsRemaining,
                secondsRemaining
            )
            autoAdvance.visibility = View.VISIBLE
        }

        /** @Override */
        override fun autoEventFired(event: GameAutoEventController.AutoEvent) {
            when (event) {
                GameAutoEventController.AutoEvent.Lock -> {
                    gameState.lockGame()
                    render()
                }
                GameAutoEventController.AutoEvent.AdvanceFrame -> {
                    onNextBall()
                }
            }
        }

        /** @Override */
        override fun autoEventDelayed(event: GameAutoEventController.AutoEvent) {
            when (event) {
                GameAutoEventController.AutoEvent.AdvanceFrame -> {
                    autoAdvance.visibility = View.GONE
                }
                GameAutoEventController.AutoEvent.Lock -> {} // Do nothing
            }
        }

        /** @Override */
        override fun autoEventPaused(event: GameAutoEventController.AutoEvent) {
            when (event) {
                GameAutoEventController.AutoEvent.AdvanceFrame -> {
                    autoAdvance.visibility = View.GONE
                }
                GameAutoEventController.AutoEvent.Lock -> {} // Do nothing
            }
        }
    }

    // MARK: Dialogs

    /**
     * Prompt the user to reset the current game.
     */
    private fun showResetGameDialog() {
        context?.let {
            ResetGameDialog.show(it, WeakReference({
                resetGame()
            }))
        }
    }

    // MARK: Companion Object

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameFragment"

        /** Argument identifier for passing a [Series] to this fragment. */
        private const val ARG_SERIES = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param series the series to edit games for
         * @return the new instance
         */
        fun newInstance(series: Series): GameFragment {
            val fragment = GameFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_SERIES, series) }
            return fragment
        }
    }

    // MARK: OnGameFragmentInteractionListener

    /**
     * Handle interactions with the game fragment.
     */
    interface OnGameFragmentInteractionListener {
        fun enableFab(enabled: Boolean)
    }
}
