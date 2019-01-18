package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.reset
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.utils.Analytics
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
class GameState(
    private val series: Series,
    initialGames: MutableList<Game> = ArrayList(),
    initialGameIdx: Int = 0,
    initialFrameIdx: Int = 0,
    initialBallIdx: Int = 0
) : KParcelable {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameState"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GameState)
    }

    // MARK: Parcelable

    constructor(p: Parcel): this(
            series = p.readParcelable<Series>(Series::class.java.classLoader)!!,
            initialGames = arrayListOf<Game>().apply {
                val parcelableArray = p.readParcelableArray(Game::class.java.classLoader)!!
                this.addAll(parcelableArray.map { return@map it as Game })
            },
            initialGameIdx = p.readInt(),
            initialFrameIdx = p.readInt(),
            initialBallIdx = p.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(series, 0)
        writeParcelableArray(games.toTypedArray(), 0)
        writeInt(currentGameIdx)
        writeInt(currentFrameIdx)
        writeInt(currentBallIdx)
    }

    // MARK: Properties

    var delegate: GameStateDelegate? = null

    var gamesLoaded: Boolean = initialGames.size > 0

    private val games: MutableList<Game> = initialGames
        get() {
            check(gamesLoaded) { "The games have not been loaded before accessing." }
            return field
        }

    var currentGameIdx: Int = initialGameIdx
        set(newGame) {
            if (newGame >= 0 && newGame < series.numberOfGames) {
                field = newGame
                moveToLastSavedFrame()
            }
        }

    var currentFrameIdx: Int = initialFrameIdx
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

    var currentBallIdx: Int = initialBallIdx
        set(newBall) {
            if (newBall >= 0 && newBall < Frame.NUMBER_OF_BALLS) {
                field = newBall
                delegate?.onBallChanged()
            }
        }

    private var skipBallListenerUpdate: Boolean = false

    // MARK: Calculated properties

    val currentGame: Game
        get() = games[currentGameIdx]

    val currentFrame: Frame
        get() = currentGame.frames[currentFrameIdx]

    private val isGameEditable: Boolean
        get() = !currentGame.isLocked && !currentGame.isManual

    val scores: List<Int>
        get() = games.map { it.score }

    val currentBallFouled: Boolean
        get() = currentFrame.ballFouled[currentBallIdx]

    val currentPinState: Deck
        get() = currentFrame.pinState[currentBallIdx]

    val isFirstBall: Boolean
        get() = currentFrameIdx == 0 && currentBallIdx == 0

    val isLastBall: Boolean
        get() = isLastFrame && currentBallIdx == Frame.LAST_BALL

    val isLastFrame: Boolean
        get() = currentFrameIdx == Game.LAST_FRAME

    val frameHasNextBall: Boolean
        get() = currentBallIdx != Frame.LAST_BALL && (isLastFrame || !currentPinState.arePinsCleared)

    val enabledPins: IntArray
        get() {
            return (0 until Game.NUMBER_OF_PINS).filter {
                return@filter (
                    currentBallIdx == 0 ||
                    !currentFrame.pinState[currentBallIdx - 1][it].isDown ||
                    (isLastFrame && currentFrame.pinState[currentBallIdx - 1].arePinsCleared)
                )
            }.toIntArray()
        }

    val disabledPins: IntArray
        get() {
            val enabled = enabledPins
            return (0 until Game.NUMBER_OF_PINS).filter { it !in enabled }.toIntArray()
        }

    val shareableGames: List<Game>
        get() = games.map { it.deepCopy() }

    // MARK: GameState

    fun toggleFoul() {
        if (!isGameEditable) { return }
        currentFrame.ballFouled[currentBallIdx] = !currentFrame.ballFouled[currentBallIdx]
        currentGame.markDirty()
    }

    fun toggleLock() {
        if (currentGame.isManual) { return }
        currentGame.isLocked = !currentGame.isLocked

        if (currentGame.isLocked) {
            Analytics.trackLockGame()
        }
    }

    fun lockGame() {
        if (currentGame.isManual) { return }
        currentGame.isLocked = true
    }

    fun setManualScore(context: WeakReference<Context>, score: Int) {
        resetGame(context)
        currentGame.isLocked = true
        currentGame.isManual = true
        currentGame.score = score
        saveGame(context, true)
        attemptToSetFrameAndBall(0, 0)
        delegate?.onManualScoreSet()
    }

    fun clearManualScore(context: WeakReference<Context>) {
        currentGame.isLocked = false
        currentGame.isManual = false
        saveGame(context, false)
        attemptToSetFrameAndBall(0, 0)
        delegate?.onManualScoreCleared()
    }

    fun setMatchPlay(opponentName: String, opponentScore: Int, result: MatchPlayResult) {
        currentGame.matchPlay.apply {
            this.opponentName = opponentName
            this.opponentScore = opponentScore
            this.result = result
        }
        currentGame.markDirty()
    }

    fun nextBall() {
        if (frameHasNextBall) {
            attemptToSetFrameAndBall(currentFrameIdx, currentBallIdx + 1)
        } else if (!isLastFrame) {
            attemptToSetFrameAndBall(currentFrameIdx + 1, 0)
        }
    }

    fun prevBall() {
        if (currentBallIdx == 0 && currentFrameIdx > 0) {
            attemptToSetFrameAndBall(currentFrameIdx - 1, Frame.LAST_BALL)
        } else if (currentBallIdx > 0) {
            attemptToSetFrameAndBall(currentFrameIdx, currentBallIdx - 1)
        }
    }

    fun attemptToSetFrameAndBall(frame: Int, ball: Int) {
        skipBallListenerUpdate = true
        currentFrameIdx = frame
        skipBallListenerUpdate = false

        if (frame == Game.LAST_FRAME) {
            currentBallIdx = ball
            return
        }

        var newBall = 0
        while (newBall < ball && !currentFrame.pinState[newBall].arePinsCleared) {
            newBall++
        }
        currentBallIdx = newBall

        if (gamesLoaded) {
            currentGame.markDirty()
        }
    }

    private fun moveToLastSavedFrame() {
        var lastSavedFrame = Game.LAST_FRAME
        while (!currentGame.frames[lastSavedFrame].isAccessed && lastSavedFrame > 0) {
            lastSavedFrame--
        }
        attemptToSetFrameAndBall(lastSavedFrame, 0)
    }

    fun setPins(pins: IntArray, isDown: Boolean) {
        if (!isGameEditable) { return }

        if (isDown) {
            for (i in currentBallIdx..Frame.LAST_BALL) {
                pins.forEach { currentFrame.pinState[i][it].isDown = isDown }

                if (currentFrame.pinState[i].arePinsCleared) {
                    for (j in (i + 1)..Frame.LAST_BALL) {
                        currentFrame.ballFouled[j] = false

                        if (isLastFrame && currentBallIdx < Frame.LAST_BALL) {
                            currentFrame.pinState[j].forEach { it.isDown = false }
                        }
                    }

                    if (isLastFrame) {
                        break
                    }
                }
            }
        } else {
            val werePinsCleared = currentPinState.arePinsCleared
            for (i in currentBallIdx..Frame.LAST_BALL) {
                pins.forEach { currentFrame.pinState[i][it].isDown = isDown }
            }

            // In the last frame, when the first/second ball was a strike and no longer is, copy the state to the later balls
            if (isLastFrame) {
                if (currentBallIdx < Frame.LAST_BALL && werePinsCleared && !currentPinState.arePinsCleared) {
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

    fun resetGame(context: WeakReference<Context>) {
        currentGame.isManual = false
        currentGame.isLocked = false
        currentGame.frames.forEach { frame ->
            for (i in 0 until Frame.NUMBER_OF_BALLS) {
                frame.ballFouled[i] = false
                frame.pinState[i].reset()
            }

            frame.isAccessed = false
        }

        attemptToSetFrameAndBall(0, 0)
        currentFrame.isAccessed = true
        saveGame(context, true)
    }

    fun loadGames(context: Context): Deferred<Boolean> {
        return async(CommonPool) {
            if (!gamesLoaded) {
                val games = series.fetchGames(context).await()
                if (games.size != series.numberOfGames) {
                    return@async false
                }

                gamesLoaded = true
                this@GameState.games.clear()
                this@GameState.games.addAll(games)

                moveToLastSavedFrame()
                while (currentFrameIdx == Game.LAST_FRAME && currentGameIdx < games.lastIndex) {
                    currentGameIdx += 1
                }
            }

            launch(Android) {
                delegate?.onGamesLoaded()
            }

            return@async true
        }
    }

    fun saveFrame(context: WeakReference<Context>, ignoreManualScore: Boolean) {
        if (!gamesLoaded) { return }
        if (!ignoreManualScore && currentGame.isManual) { return }
        val copy = currentFrame.deepCopy()
        Saviour.instance.saveFrame(context, currentGame.score, copy)
    }

    fun saveGame(context: WeakReference<Context>, ignoreManualScore: Boolean) {
        if (!gamesLoaded) { return }
        if (!ignoreManualScore && currentGame.isManual) { return }
        val copy = currentGame.deepCopy()
        Saviour.instance.saveGame(context, copy)
    }

    fun saveMatchPlay(context: WeakReference<Context>) {
        if (!gamesLoaded) { return }
        val copy = currentGame.matchPlay.deepCopy()
        Saviour.instance.saveMatchPlay(context, copy)
    }

    // MARK: GameStateDelegate

    interface GameStateDelegate {
        fun onBallChanged()
        fun onGamesLoaded()
        fun onManualScoreSet()
        fun onManualScoreCleared()
    }
}
