package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AlertDialog
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
import ca.josephroque.bowlingcompanion.games.dialogs.ManualScoreDialog
import ca.josephroque.bowlingcompanion.games.dialogs.PossibleScoreDialog
import ca.josephroque.bowlingcompanion.games.dialogs.ResetGameDialog
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.overview.GameOverviewFragment
import ca.josephroque.bowlingcompanion.games.views.FrameView
import ca.josephroque.bowlingcompanion.games.views.GameFooterView
import ca.josephroque.bowlingcompanion.games.views.GameHeaderView
import ca.josephroque.bowlingcompanion.games.views.PinLayout
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.settings.Settings
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.android.synthetic.main.fragment_game.game_footer as gameFooter
import kotlinx.android.synthetic.main.fragment_game.game_header as gameHeader
import kotlinx.android.synthetic.main.fragment_game.score_sheet as scoreSheet
import kotlinx.android.synthetic.main.fragment_game.manual_score as manualScore
import kotlinx.android.synthetic.main.fragment_game.pin_layout as pinLayout
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

    companion object {
        @Suppress("unused")
        private const val TAG = "GameFragment"

        private const val ARG_SERIES = "${TAG}_series"
        private const val ARG_GAME_STATE = "${TAG}_game_state"

        fun newInstance(series: Series): GameFragment {
            val fragment = GameFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_SERIES, series) }
            return fragment
        }
    }

    private var delegate: GameFragmentDelegate? = null

    var gameNumber: Int = 0
        set(value) {
            saveCurrentGame(false)
            field = value
            gameHeader.currentGame = gameNumber
            gameState.currentGameIdx = gameNumber
            gameState.currentFrame.isAccessed = true
            render(ballChanged = true, isGameFirstRender = true)

            if (gameState.currentGame.isLocked) {
                autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
                autoEventController.disable(GameAutoEventController.AutoEvent.AdvanceFrame)
            } else {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                autoEventController.init(preferences)
            }
        }

    val gamesForStatistics: List<Game>
        get() = gameState.shareableGames

    private lateinit var series: Series
    private lateinit var gameState: GameState
    private lateinit var autoEventController: GameAutoEventController

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        setupBottomSheet(view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        series = arguments!!.getParcelable(ARG_SERIES)!!

        // Enable automatic events
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        autoEventController = GameAutoEventController(preferences, autoEventDelegate)

        val context = context ?: return
        gameState = if (savedInstanceState == null) {
            GameState(series)
        } else {
            savedInstanceState.getParcelable(ARG_GAME_STATE)!!
        }
        gameState.delegate = gameStateDelegate
        gameState.loadGames(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_game, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        if (!gameState.gamesLoaded) { return }
        menu?.findItem(R.id.action_set_score)?.isVisible = !gameState.currentGame.isManual
        menu?.findItem(R.id.action_clear_score)?.isVisible = gameState.currentGame.isManual
        menu?.findItem(R.id.action_best_possible)?.isVisible = !gameState.currentGame.isManual
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? GameFragmentDelegate ?: throw RuntimeException("${parentFragment!!} must implement GameFragmentDelegate")
        delegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_GAME_STATE, gameState)
    }

    override fun onStart() {
        super.onStart()

        // Enable automatic events
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        autoEventController.init(preferences)
        autoEventController.pauseAll()

        scoreSheet.frameViewDelegate = this
        scoreSheet.shouldHighlightMarks = Settings.BooleanSetting.EnableStrikeHighlights.getValue(preferences)

        pinLayout.delegate = this
        gameFooter.delegate = this
        gameHeader.delegate = this

        // Advance to the last edited frame of the game
        if (gameState.gamesLoaded) {
            gameState.moveToLastSavedFrame()
        }

        fabProvider?.invalidateFab()
    }

    override fun onStop() {
        super.onStop()
        autoEventController.pauseAll()
    }

    override fun onResume() {
        super.onResume()
        render(ballChanged = false, isGameFirstRender = false)
    }

    override fun onPause() {
        prepareToPause()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        autoEventController.pauseAll()
        return when (item?.itemId) {
            R.id.action_overview -> {
                showOverview()
                true
            }
            R.id.action_set_score -> {
                showManualScoreDialog()
                true
            }
            R.id.action_clear_score -> {
                showClearManualScoreDialog()
                true
            }
            R.id.action_best_possible -> {
                showBestScorePossible()
                true
            }
            R.id.action_reset_game -> {
                showResetGameDialog()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // MARK: GameFragment

    fun prepareToPause() {
        context?.let { gameState.saveGame(WeakReference(it), true) }
    }

    fun invalidateFab() {
        val hasNextBowlerOrGame = delegate?.hasNextBowlerOrGame == true
        val isManual = gameState.gamesLoaded && gameState.currentGame.isManual
        if (hasNextBowlerOrGame || !gameState.isLastBall) {
            if (delegate?.isFabEnabled == false) {
                delegate?.enableFab(true)
            }
        } else if (isManual || gameState.isLastBall) {
            delegate?.enableFab(false)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: Private functions

    private fun setupBottomSheet(rootView: View) {
        val bottomSheet = rootView.sheet_match_play
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun render(ballChanged: Boolean = false, isGameFirstRender: Boolean = false) {
        if (!gameState.gamesLoaded) { return }
        launch(Android) {
            if (view == null) { return@launch }

            scoreSheet.apply(gameState.currentFrameIdx, gameState.currentBallIdx, gameState.currentGame)

            // Update up/down pins
            gameState.currentPinState.forEachIndexed { index, pin ->
                pinLayout.updatePinImage(index, pin.isDown)
            }

            // Set which pins are enabled/disabled
            if (gameState.currentGame.isLocked) {
                for (i in 0 until Game.NUMBER_OF_PINS) { pinLayout.setPinEnabled(i, false) }
            } else {
                gameState.enabledPins.forEach { pinLayout.setPinEnabled(it, true) }
                gameState.disabledPins.forEach { pinLayout.setPinEnabled(it, false) }
            }

            // Show/hide the pin layout and score when the score is manual
            gameHeader.isManualScoreSet = gameState.currentGame.isManual
            if (gameState.currentGame.isManual) {
                pinLayout.visibility = View.GONE
                scoreSheet.visibility = View.GONE

                manualScore.text = gameState.currentGame.score.toString()
                manualScore.visibility = View.VISIBLE
            } else {
                pinLayout.visibility = View.VISIBLE
                scoreSheet.visibility = View.VISIBLE

                manualScore.text = null
                manualScore.visibility = View.GONE
            }

            // Set icons in game footer
            gameFooter.apply {
                isFullscreen = this@GameFragment.delegate?.isFullscreen == true
                isFoulActive = gameState.currentFrame.ballFouled[gameState.currentBallIdx]
                isGameLocked = gameState.currentGame.isLocked
                matchPlayResult = gameState.currentGame.matchPlay.result
                clear = when {
                    gameState.currentBallIdx == 0 ||
                        (gameState.isLastFrame && gameState.currentBallIdx == 1 && gameState.currentFrame.pinState[0].arePinsCleared) ||
                        (gameState.isLastFrame && gameState.currentBallIdx == 2 && gameState.currentFrame.pinState[1].arePinsCleared) -> {
                        GameFooterView.Companion.Clear.Strike
                    }
                    gameState.currentBallIdx == 1 ||
                        (gameState.currentBallIdx == 2 && gameState.currentFrame.pinState[0].arePinsCleared) -> {
                        GameFooterView.Companion.Clear.Spare
                    }
                    else -> GameFooterView.Companion.Clear.Fifteen
                }
                isManualScoreSet = gameState.currentGame.isManual
            }

            // Update the score of this game in the navigation drawer
            gameState.scores.forEachIndexed { index, score -> delegate?.updateGameScore(index, score) }

            if (ballChanged || isGameFirstRender) {
                focusOnFrame(isGameFirstRender)
            }
        }
    }

    private fun saveCurrentFrame(ignoreManualScore: Boolean) {
        context?.let { gameState.saveFrame(WeakReference(it), ignoreManualScore) }
    }

    private fun saveCurrentGame(ignoreManualScore: Boolean) {
        context?.let { gameState.saveGame(WeakReference(it), ignoreManualScore) }
    }

    private fun saveMatchPlay() {
        context?.let { gameState.saveMatchPlay(WeakReference(it)) }
    }

    private fun focusOnFrame(isGameFirstRender: Boolean) {
        scoreSheet.focusOnFrame(isGameFirstRender, gameState.isLastFrame, gameState.currentFrameIdx)
    }

    private fun resetGame() {
        context?.let { gameState.resetGame(WeakReference(it)) }
    }

    private fun showOverview() {
        val newFragment = GameOverviewFragment.newInstance(gameState.shareableGames)
        fragmentNavigation?.pushFragment(newFragment)

        Analytics.trackViewOverview()
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return null
    }

    override fun onFabClick() {
        onNextBall()
    }

    // MARK: FrameInteractionDelegate

    override fun onBallSelected(ball: Int, frame: Int) {
        if (gameState.gamesLoaded) { saveCurrentFrame(false) }
        gameState.attemptToSetFrameAndBall(frame, ball)
    }

    override fun onFrameSelected(frame: Int) {
        onBallSelected(0, frame)
    }

    // MARK: PinLayoutInteractionDelegate

    override fun isPinDown(pin: Int): Boolean {
        return if (gameState.gamesLoaded) gameState.currentPinState[pin].isDown else false
    }

    override fun setPins(pins: IntArray, isDown: Boolean) {
        gameState.setPins(pins, isDown)
        if (!gameState.currentGame.isLocked && gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.Lock) }
        if (!gameState.currentGame.isLocked && !gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.AdvanceFrame) }
        render()
    }

    // MARK: GameFooterInteractionDelegate

    override fun onClearPins() {
        setPins((0 until Game.NUMBER_OF_PINS).toList().toIntArray(), true)
    }

    override fun onFoulToggle() {
        gameState.toggleFoul()
        scoreSheet.setFoulEnabled(gameState.currentFrameIdx, gameState.currentBallIdx, gameState.currentBallFouled)
        if (!gameState.currentGame.isLocked && gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.Lock) }
        if (!gameState.currentGame.isLocked && !gameState.isLastBall) { autoEventController.delay(GameAutoEventController.AutoEvent.AdvanceFrame) }
        render()
    }

    override fun onLockToggle() {
        gameState.toggleLock()
        autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
        autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
        saveCurrentGame(false)
        render()
    }

    override fun onMatchPlaySettings() {
        val fragment = MatchPlaySheet.newInstance(gameState.currentGame.matchPlay)
        fragmentNavigation?.showBottomSheet(fragment, MatchPlaySheet.FRAGMENT_TAG)
        autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
    }

    override fun onFullscreenToggle() {
        delegate?.toggleFullscreen()
        render()
    }

    // MARK: GameHeaderInteractionDelegate

    override fun onNextBall() {
        saveCurrentFrame(false)

        if (!gameState.frameHasNextBall || gameState.currentGame.isManual) {
            val nextBowlerResult = delegate?.nextBowlerOrGame(gameState.isLastFrame || gameState.currentGame.isManual)
            when (nextBowlerResult) {
                NextBowlerResult.NextBowlerGame, NextBowlerResult.NextGame -> { return }
                else -> {} // does nothing
            }
        }

        gameState.nextBall()
    }

    override fun onPrevBall() {
        saveCurrentFrame(false)
        gameState.prevBall()
    }

    // MARK: MatchPlaySheetDelegate

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

    // MARK: GameStateDelegate

    private val gameStateDelegate = object : GameState.GameStateDelegate {
        override fun onGamesLoaded() {
            if (gameState.currentGame.isLocked) {
                autoEventController.disable(GameAutoEventController.AutoEvent.Lock)
                autoEventController.disable(GameAutoEventController.AutoEvent.AdvanceFrame)
            }
            gameState.currentFrame.isAccessed = true
            gameNumber = gameState.currentGameIdx

            delegate?.onGamesLoaded(gameState.currentGameIdx, this@GameFragment)
            activity?.invalidateOptionsMenu()
            render(ballChanged = true, isGameFirstRender = true)
        }

        override fun onBallChanged() {
            render(ballChanged = true)
            invalidateFab()

            gameHeader.hasPreviousFrame = !gameState.isFirstBall && !gameState.currentGame.isManual
            gameHeader.hasNextFrame = delegate?.isFabEnabled == true
            if (gameState.isLastBall) {
                autoEventController.delay(GameAutoEventController.AutoEvent.Lock)
            } else {
                autoEventController.pause(GameAutoEventController.AutoEvent.Lock)
            }
            autoEventController.pause(GameAutoEventController.AutoEvent.AdvanceFrame)
        }

        override fun onManualScoreSet() {
            invalidateFab()
            activity?.invalidateOptionsMenu()
            render(ballChanged = true, isGameFirstRender = false)
        }

        override fun onManualScoreCleared() {
            invalidateFab()
            activity?.invalidateOptionsMenu()
            render(ballChanged = true, isGameFirstRender = false)
        }
    }

    // MARK: GameAutoEventDelegate

    private val autoEventDelegate = object : GameAutoEventController.GameAutoEventDelegate {
        override fun autoAdvanceCountDown(secondsRemaining: Int) {
            val autoAdvanceStringId = if (gameState.frameHasNextBall) R.plurals.time_until_auto_advance_ball else R.plurals.time_until_auto_advance_frame
            autoAdvance.text = resources.getQuantityString(
                autoAdvanceStringId,
                secondsRemaining,
                secondsRemaining
            )
            autoAdvance.visibility = View.VISIBLE
        }

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

        override fun autoEventDelayed(event: GameAutoEventController.AutoEvent) {
            when (event) {
                GameAutoEventController.AutoEvent.AdvanceFrame -> {
                    autoAdvance.visibility = View.GONE
                }
                GameAutoEventController.AutoEvent.Lock -> {} // Do nothing
            }
        }

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

    private fun showBestScorePossible() {
        context?.let { PossibleScoreDialog.show(it, gameState.currentGame.deepCopy(), gameState.currentFrameIdx, gameState.currentBallIdx) }
    }

    private fun showResetGameDialog() {
        context?.let {
            ResetGameDialog.show(it, WeakReference({
                resetGame()

                Analytics.trackResetGame()
            }))
        }
    }

    private fun showManualScoreDialog() {
        context?.let { context ->
            AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_set_score_title)
                    .setMessage(R.string.dialog_set_score_message)
                    .setPositiveButton(R.string.set_score) { _, _ ->
                        ManualScoreDialog.showSetScoreDialog(context) {
                            if (!Game.isValidScore(it)) {
                                BCError(
                                        R.string.error_manual_score_invalid_title,
                                        R.string.error_manual_score_invalid_message,
                                        BCError.Severity.Warning
                                ).show(context)
                                return@showSetScoreDialog
                            }

                            gameState.setManualScore(WeakReference(context), it)

                            Analytics.trackSetGameManualScore()
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show()
        }
    }

    private fun showClearManualScoreDialog() {
        context?.let {
            ManualScoreDialog.showClearScoreDialog(it) {
                gameState.clearManualScore(WeakReference(it))
            }
        }
    }

    // MARK: GameFragmentDelegate

    interface GameFragmentDelegate {
        val hasNextBowlerOrGame: Boolean
        val isFabEnabled: Boolean
        val isFullscreen: Boolean

        fun enableFab(enabled: Boolean)
        fun nextBowlerOrGame(isEndOfGame: Boolean): NextBowlerResult
        fun toggleFullscreen()
        fun updateGameScore(gameIdx: Int, score: Int)
        fun onGamesLoaded(currentGame: Int, srcFragment: GameFragment)
    }

    enum class NextBowlerResult {
        NextBowler, NextGame, NextBowlerGame, None;
    }
}
