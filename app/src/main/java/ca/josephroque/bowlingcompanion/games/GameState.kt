package ca.josephroque.bowlingcompanion.games

import android.content.Context
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manages the loading, saving, and updating of the state of a game.
 */
class GameState(private val series: Series, private val listener: GameStateListener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameState"
    }

    /** Indicates if the games for the series have been loaded. */
    var gamesLoaded: Boolean = false

    /** The list of games to edit. */
    private val games: MutableList<Game> = ArrayList()
        get() {
            check(gamesLoaded) { "The games have not been loaded before accessing." }
            return field
        }

    /** The current game. */
    val currentGame: Game
        get() = games[currentGameIdx]

    /**
     * Index of the current game being edited.
     * When changed, updates currentFrame and currentBall.
     */
    var currentGameIdx: Int = 0
        set(newGame) {
            if (newGame >= 0 && newGame < series.numberOfGames) {
                field = newGame
                currentFrameIdx = games[currentGameIdx].firstNewFrame
            }
        }

    /** The current frame. */
    val currentFrame: Frame
        get() = currentGame.frames[currentFrameIdx]

    /** The index of the current frame being edited. When changed, sets currentBall to 0. */
    var currentFrameIdx: Int = 0
        set(newFrame) {
            if (newFrame >= 0 && newFrame < Game.NUMBER_OF_FRAMES) {
                if (newFrame > field) {
                    for (i in 0..newFrame) {
                        currentGame.frames[i].isAccessed = true
                    }
                }

                field = newFrame
                if (!skipBallListenerUpdate) {
                    currentBallIdx = 0
                }
            }
        }

    /** The current ball being edited. */
    var currentBallIdx: Int = 0
        set(newBall) {
            if (newBall >= 0 && newBall < Frame.NUMBER_OF_BALLS) {
                field = newBall
                listener.onBallChanged()
            }
        }

    /** When true, do not update the current ball when the frame is changed. */
    private var skipBallListenerUpdate: Boolean = false

    /** Checks if the current game can be edited or not. */
    private val isGameEditable: Boolean
        get() = !currentGame.isLocked && !currentGame.isManual

    /** Indicates if the current ball for the current game/frame has a foul. */
    val currentBallFouled: Boolean
        get() = currentFrame.ballFouled[currentBallIdx]

    /** The state of the pins in the current frame and ball. */
    val currentPinState: Deck
        get() = currentFrame.pinState[currentBallIdx]

    /** Returns true if the user is currently on the last ball of the game. */
    val isLastBall: Boolean
        get() = isLastFrame && currentBallIdx == Frame.LAST_BALL

    /** Returns true if the user is currently on the last frame of the game. */
    val isLastFrame: Boolean
        get() = currentFrameIdx == Game.LAST_FRAME

    /** Get an array of pin indices indicating which pins are enabled for the current frame. */
    val enabledPins: IntArray
        get() {
            return (0 until Game.NUMBER_OF_PINS).filter {
                return@filter (
                    currentBallIdx == 0 ||
                    !currentFrame.pinState[currentBallIdx - 1][it].isDown ||
                    (isLastFrame && currentFrame.pinState[currentBallIdx - 1].arePinsCleared())
                )
            }.toIntArray()
        }

    /** Get an array of pin indices indicating which pins are disabled for the current frame. */
    val disabledPins: IntArray
        get() {
            val enabled = enabledPins
            return (0 until Game.NUMBER_OF_PINS).filter { it !in enabled }.toIntArray()
        }

    /**
     * Toggle the foul on or off for the current ball.
     */
    fun toggleFoul() {
        if (!isGameEditable) { return }
        currentFrame.ballFouled[currentBallIdx] = !currentFrame.ballFouled[currentBallIdx]
        currentGame.markDirty()
    }

    /**
     * Toggle the lock for the current game on or off.
     */
    fun toggleLock() {
        if (currentGame.isManual) { return }
        currentGame.isLocked = !currentGame.isLocked
    }

    /**
     * Lock the current game.
     */
    fun lockGame() {
        if (currentGame.isManual) { return }
        currentGame.isLocked = true
    }

    /**
     * Set the game match play results.
     *
     * @param opponentName name of the opponent for match play
     * @param opponentScore score of the opponent for match play
     * @param result result of the match play
     */
    fun setMatchPlay(opponentName: String, opponentScore: Int, result: MatchPlayResult) {
        currentGame.matchPlay.apply {
            this.opponentName = opponentName
            this.opponentScore = opponentScore
            this.result = result
        }
        currentGame.markDirty()
    }

    /**
     * Go to the next ball. Increment the frame if necessary.
     */
    fun nextBall() {
        if (!isLastFrame && (currentBallIdx == Frame.LAST_BALL || currentPinState.arePinsCleared())) {
            attemptToSetFrameAndBall(currentFrameIdx + 1, 0)
        } else if (currentBallIdx < Frame.LAST_BALL) {
            attemptToSetFrameAndBall(currentFrameIdx, currentBallIdx + 1)
        }
    }

    /**
     * Go to the previous ball. Decrement the frame if necessary.
     */
    fun prevBall() {
        if (currentBallIdx == 0 && currentFrameIdx > 0) {
            attemptToSetFrameAndBall(currentFrameIdx - 1, Frame.LAST_BALL)
        } else if (currentBallIdx > 0) {
            attemptToSetFrameAndBall(currentFrameIdx, currentBallIdx - 1)
        }
    }

    /**
     * Set the frame and ball, if possible according to the current game state, or ignored.
     *
     * @param frame the new frame
     * @param ball the new ball
     */
    fun attemptToSetFrameAndBall(frame: Int, ball: Int) {
        skipBallListenerUpdate = true
        currentFrameIdx = frame
        skipBallListenerUpdate = false

        if (frame == Game.LAST_FRAME) {
            currentBallIdx = ball
            return
        }

        var newBall = 0
        while (newBall < ball && !currentFrame.pinState[newBall].arePinsCleared()) {
            newBall++
        }
        currentBallIdx = newBall

        if (gamesLoaded) {
            currentGame.markDirty()
        }
    }

    /**
     * Set the state of the pins for the current ball.
     *
     * @param pins the pin indices to update
     * @param isDown new state for the pins
     */
    fun setPins(pins: IntArray, isDown: Boolean) {
        if (!isGameEditable) { return }

        if (isDown) {
            for (i in currentBallIdx..Frame.LAST_BALL) {
                pins.forEach { currentFrame.pinState[i][it].isDown = isDown }

                if (currentFrame.pinState[i].arePinsCleared()) {
                    for (j in (i + 1)..Frame.LAST_BALL) {
                        currentFrame.ballFouled[j] = false

                        if (isLastFrame && currentBallIdx < Frame.LAST_BALL) {
                            currentFrame.pinState[j].forEach { it.isDown = false }
                        }
                    }
                }
            }
        } else {
            val werePinsCleared = currentPinState.arePinsCleared()
            for (i in currentBallIdx..Frame.LAST_BALL) {
                pins.forEach { currentFrame.pinState[i][it].isDown = isDown }
            }

            // In the last frame, when the first/second ball was a strike and no longer is, copy the state to the later balls
            if (isLastFrame) {
                if (currentBallIdx < Frame.LAST_BALL && werePinsCleared && !currentPinState.arePinsCleared()) {
                    currentPinState.forEachIndexed { index, pin ->
                        for (i in (currentBallIdx + 1)..Frame.LAST_BALL) {
                            currentFrame.pinState[i][index].isDown = pin.isDown
                        }
                    }
                }
            }
        }

        currentGame.markDirty()
    }

    /**
     * Load the games for the series and cache them for editing.
     *
     * @param context to get database instance
     * @return true if the games are successfully loaded, false otherwise
     */
    fun loadGames(context: Context): Deferred<Boolean> {
        return async(CommonPool) {
            val games = series.fetchGames(context).await()
            if (games.size != series.numberOfGames) {
                return@async false
            }

            gamesLoaded = true
            this@GameState.games.clear()
            this@GameState.games.addAll(games)

            launch(Android) {
                listener.onGamesLoaded()
            }

            return@async true
        }
    }

    /**
     * Save the current frame/game state to the database.
     *
     * @param context to access database
     * @param ignoreManualScore ignore any manual score set and save the frame
     */
    fun saveFrame(context: WeakReference<Context>, ignoreManualScore: Boolean) {
        if (!ignoreManualScore && currentGame.isManual) { return }
        val copy = currentFrame.deepCopy()
        Saviour.instance.saveFrame(context, currentGame.score, copy)
    }

    /**
     * Save the current game state to the database.
     *
     * @param context to access database
     * @param ignoreManualScore ignore any manual score set and save the game
     */
    fun saveGame(context: WeakReference<Context>, ignoreManualScore: Boolean) {
        if (!ignoreManualScore && currentGame.isManual) { return }
        val copy = currentGame.deepCopy()
        Saviour.instance.saveGame(context, copy)
    }

    /**
     * Save the current match play results to the database.
     *
     * @param context to access database
     */
    fun saveMatchPlay(context: WeakReference<Context>) {
        val copy = currentGame.matchPlay.deepCopy()
        Saviour.instance.saveMatchPlay(context, copy)
    }

    /**
     * Handle events from game state changes.
     */
    interface GameStateListener {
        /**
         * Called when the current ball is updated.
         */
        fun onBallChanged()

        /**
         * Called when the games finish loading.
         */
        fun onGamesLoaded()
    }
}
