package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.games.views.FrameView
import ca.josephroque.bowlingcompanion.games.views.GameFooterView
import ca.josephroque.bowlingcompanion.games.views.GameHeaderView
import ca.josephroque.bowlingcompanion.games.views.PinLayout
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.sheet_match_play.*
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
            saveCurrentGame()
            field = value
            game_header.currentGame = gameNumber
            gameState.currentGameIdx = gameNumber
        }

    /** The series being edited. */
    private var series: Series? = null

    /** Manage the state of the current game. */
    private lateinit var gameState: GameState

    // MARK: Overrides

    /** @Override */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        pin_layout.delegate = this
        game_footer.delegate = this
        game_header.delegate = this
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        val context = context ?: return
        series?.let {
            gameState = GameState(it, gameStateListener)
            launch(Android) {
                gameState.loadGames(context).await()
                refresh()
            }
        }

        onBallSelected(0, 0)
    }

    /** @Override */
    override fun onPause() {
        super.onPause()
        context?.let { gameState.saveGame(WeakReference(it)) }
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
     */
    private fun refresh() {
        if (!gameState.gamesLoaded) { return }

        // Update active frames
        frameViews.forEachIndexed { index, it ->
            it?.isCurrentFrame = (index == gameState.currentFrameIdx)
            it?.currentBall = gameState.currentBallIdx
        }

        // Update scores of the frames
        val scoreText = gameState.currentGame.getScoreTextForFrames()
        scoreText.forEachIndexed({ index, score ->
            frameViews[index]?.setFrameText(score)
        })
        tv_final_score.text = gameState.currentGame.score.toString()

        // Update balls of the frames
        val ballText = gameState.currentGame.getBallTextForFrames()
        ballText.forEachIndexed({ index, balls ->
            balls.forEachIndexed({ ballIdx, ball ->
                frameViews[index]?.setBallText(ballIdx, ball)
            })
        })

        // Update fouls of the frames
        gameState.currentGame.frames.forEachIndexed({ index, frame ->
            frame.ballFouled.forEachIndexed({ ballIdx, foul ->
                frameViews[index]?.setFoulEnabled(ballIdx, foul)
            })
        })

        game_footer.apply {
            isFoulActive = gameState.currentFrame.ballFouled[gameState.currentBallIdx]
            isGameLocked = gameState.currentGame.isLocked
            currentBall = gameState.currentBallIdx
            matchPlayResult = gameState.currentGame.matchPlay.result
        }
    }

    /**
     * Save the current frame of the game state to the database.
     */
    private fun saveCurrentFrame() {
        context?.let { gameState.saveFrame(WeakReference(it)) }
    }

    /**
     * Save the current game of the game state to the database.
     */
    private fun saveCurrentGame() {
        context?.let { gameState.saveGame(WeakReference(it)) }
    }

    // MARK: IFloatingActionButtonHandler

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward
    }

    /** @Override */
    override fun onFabClick() {
        gameState.nextBall()
        refresh()
        // TODO: change bowler if necessary
    }

    // MARK: FrameInteractionDelegate

    /** @Override */
    override fun onBallSelected(ball: Int, frame: Int) {
        if (gameState.gamesLoaded) {
            saveCurrentFrame()
        }
        gameState.currentFrameIdx = frame
        gameState.currentBallIdx = ball
        game_header.currentFrame = gameState.currentFrameIdx
        game_header.currentBall = gameState.currentBallIdx
        refresh()
    }

    /** @Override */
    override fun onFrameSelected(frame: Int) {
        onBallSelected(0, frame)
    }

    // MARK: PinLayoutInteractionDelegate

    /** @Override */
    override fun getPinState(pin: Int): Boolean {
        return if (gameState.gamesLoaded) gameState.currentPinState[pin].isDown else false
    }

    /** @Override */
    override fun updatePinState(pins: IntArray, state: Boolean) {
        if (!gameState.gamesLoaded) { return }
        pins.forEach { gameState.currentPinState[it].isDown = state }
        refresh()
    }

    // MARK: GameFooterInteractionDelegate

    /** @Override */
    override fun onClearPins() {
        updatePinState((1..Game.NUMBER_OF_PINS).map { it - 1 }.toIntArray(), true)
    }

    /** @Override */
    override fun onFoulToggle() {
        val frameView = frameViews[gameState.currentFrameIdx] ?: return
        gameState.toggleFoul()
        frameView.setFoulEnabled(gameState.currentBallIdx, gameState.currentBallFouled)
        refresh()
    }

    /** @Override */
    override fun onLockToggle() {
        gameState.toggleLock()
        refresh()
    }

    /** @Override */
    override fun onMatchPlaySettings() {
        val fragment = MatchPlaySheet.newInstance()
        fragmentNavigation?.showBottomSheet(fragment, MatchPlaySheet.FRAGMENT_TAG)
    }

    // MARK: GameHeaderInteractionDelegate

    /** @Override */
    override fun onNextBall() {
        gameState.nextBall()
        onBallSelected(gameState.currentBallIdx, gameState.currentFrameIdx)
        // TODO: change bowler if necessary
    }

    /** @Override */
    override fun onPrevBall() {
        gameState.prevBall()
        onBallSelected(gameState.currentBallIdx, gameState.currentFrameIdx)
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
            val sheetBehavior = BottomSheetBehavior.from(sheet_match_play)
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return
        }

        gameState.currentGame.matchPlay.apply {
            this.opponentName = opponentName
            this.opponentScore = opponentScore
            this.result = matchPlayResult
        }

        context?.let { gameState.saveMatchPlay(WeakReference(it)) }
        refresh()
    }

    // MARK: GameStateListener

    /** Handle events from game state changes. */
    private val gameStateListener = object : GameState.GameStateListener {
        /** @Override */
        override fun onLastBallEntered() {
            listener?.enableFab(false)
        }

        /** @Override */
        override fun onLastBallExited() {
            listener?.enableFab(true)
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
