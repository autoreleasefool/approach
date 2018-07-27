package ca.josephroque.bowlingcompanion.games

import android.content.Context
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
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
    var skipBallListenerUpdate: Boolean = false

    /** Indicates if the current ball for the current game/frame has a foul. */
    val currentBallFouled: Boolean
        get() = currentFrame.ballFouled[currentBallIdx]

    /** The state of the pins in the current frame and ball. */
    val currentPinState: Deck
        get() = currentFrame.pinState[currentBallIdx]

    /** Returns true if the user is currently on the last ball of the game. */
    val isLastBall: Boolean
        get() = currentFrameIdx == Game.LAST_FRAME && currentBallIdx == Frame.LAST_BALL

    /**
     * Create a deep copy of this game state.
     *
     * @return a new instance of [GameState]
     */
    fun deepCopy(): GameState {
        val copy = GameState(series, listener)
        copy.games.addAll(this.games.map { it.deepCopy() })
        copy.currentGameIdx = this.currentGameIdx
        copy.currentFrameIdx = this.currentFrameIdx
        copy.currentBallIdx = this.currentBallIdx
        copy.gamesLoaded = true
        return copy
    }

    /**
     * Toggle the foul on or off for the current ball.
     */
    fun toggleFoul() {
        currentFrame.ballFouled[currentBallIdx] = !currentFrame.ballFouled[currentBallIdx]
    }

    /**
     * Toggle the lock for the current game on or off.
     */
    fun toggleLock() {
        currentGame.isLocked = !currentGame.isLocked
    }

    /**
     * Go to the next ball. Increment the frame if necessary.
     */
    fun nextBall() {
        if (currentBallIdx == Frame.LAST_BALL && currentFrameIdx < Game.LAST_FRAME) {
            currentFrameIdx += 1
        } else if (currentBallIdx < Frame.LAST_BALL) {
            currentBallIdx += 1
        }
    }

    /**
     * Go to the previous ball. Decrement the frame if necessary.
     */
    fun prevBall() {
        // TODO: go to the last ball of the previous frame
        if (currentBallIdx == 0 && currentFrameIdx > 0) {
            currentFrameIdx -= 1
        } else if (currentBallIdx > 0) {
            currentBallIdx -= 1
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

        var newBall = 0
        while (newBall < ball && !currentFrame.pinState[newBall].arePinsCleared()) {
            newBall++
        }
        currentBallIdx = newBall
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
            return@async true
        }
    }

    /**
     * Save the current frame/game state to the database.
     *
     * @param context to access database
     */
    fun saveFrame(context: WeakReference<Context>) {
        val copy = currentFrame.deepCopy()
        Saviour.instance.saveFrame(context, currentGame.score, copy)
    }

    /**
     * Save the current game state to the database.
     *
     * @param context to access database
     */
    fun saveGame(context: WeakReference<Context>) {
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
    }
}
