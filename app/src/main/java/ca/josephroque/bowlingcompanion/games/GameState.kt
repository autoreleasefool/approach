package ca.josephroque.bowlingcompanion.games

import android.content.Context
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manages the loading, saving, and updating of the state of a game.
 */
class GameState(private val series: Series, private val listener: GameStateListener) {

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
                currentBallIdx = 0
            }
        }

    /** The current ball being edited. */
    var currentBallIdx: Int = 0
        set(newBall) {
            val wasLastBall = isLastBall
            if (newBall >= 0 && newBall < Frame.NUMBER_OF_BALLS) {
                field = newBall
                if (isLastBall) {
                    listener.onLastBallEntered()
                } else if (wasLastBall) {
                    listener.onLastBallExited()
                }
            }
        }

    /** Indicates if the current ball for the current game/frame has a foul. */
    val currentBallFouled: Boolean
        get() = currentFrame.ballFouled[currentBallIdx]

    /** The state of the pins in the current frame and ball. */
    val currentPinState: Deck
        get() = currentFrame.pinState[currentBallIdx]

    /** Returns true if the user is currently on the last ball of the game. */
    private val isLastBall: Boolean
        get() = currentFrameIdx == Game.LAST_FRAME && currentBallIdx == Frame.LAST_BALL

    /**
     * Create a deep copy of this game state.
     *
     * @return a new instance of [GameState]
     */
    fun deepyCopy(): GameState {
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
        if (currentBallIdx == 0 && currentFrameIdx > 0) {
            currentFrameIdx -= 1
        } else if (currentBallIdx > 0) {
            currentBallIdx -= 1
        }
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
     * Handle events from game state changes.
     */
    interface GameStateListener {
        /**
         * Called when the user enters the last ball of a game.
         */
        fun onLastBallEntered()

        /**
         * Called when the user exits the last ball of a game.
         */
        fun onLastBallExited()
    }
}