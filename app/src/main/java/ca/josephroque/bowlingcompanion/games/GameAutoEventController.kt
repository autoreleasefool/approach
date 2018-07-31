package ca.josephroque.bowlingcompanion.games

import android.content.SharedPreferences
import android.os.Handler
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.settings.Settings
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Control automatic game events.
 */
class GameAutoEventController(
    preferences: SharedPreferences,
    private val delegate: GameAutoEventDelegate
) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameAutoEventController"

        /** Total seconds to delay before auto advance. */
        private var autoAdvanceTotalDelay: Int = 0

        /** Total milliseconds to delay before auto advance. */
        private val autoAdvanceTotalDelayMilliseconds = autoAdvanceTotalDelay * 1000L
    }

    init {
        AutoEvent.values().forEach { enabledEvents[it] = false }

        // Enable auto lock
        val autoLockEnabled = preferences.getBoolean(Settings.ENABLE_AUTO_LOCK, true)
        if (autoLockEnabled) enable(GameAutoEventController.AutoEvent.Lock)

        // Enable auto advance
        val autoAdvanceEnabled = preferences.getBoolean(Settings.ENABLE_AUTO_ADVANCE, false)
        if (autoAdvanceEnabled) enable(AutoEvent.AdvanceFrame)

        // Set auto advance delay time
        val strDelay = preferences.getString(Settings.AUTO_ADVANCE_TIME, "15 seconds")
        autoAdvanceTotalDelay = Integer.valueOf(strDelay.substring(0, strDelay.indexOf(" ")))
        AutoEvent.ADVANCE_FRAME_DELAY = autoAdvanceTotalDelayMilliseconds
    }

    /** Set of events mapped to their enabled status. */
    private val enabledEvents: HashMap<AutoEvent, Boolean> = HashMap()

    /** Handlers to perform events. */
    private val autoEventHandler: HashMap<AutoEvent, Handler> = HashMap()

    /** Runnable to execute the automatic events. */
    private val autoEventRunnable: HashMap<AutoEvent, Runnable> by lazy {
        val map = HashMap<AutoEvent, Runnable>()
        map[AutoEvent.AdvanceFrame] = Runnable {
            launch(Android) {
                if (enabledEvents[AutoEvent.AdvanceFrame] != true) { return@launch }
                if (autoAdvanceSecondsRemaining > 0) {
                    autoAdvanceSecondsRemaining -= 1
                    start(AutoEvent.AdvanceFrame)
                    delegate.autoAdvanceCountDown(autoAdvanceSecondsRemaining)
                } else {
                    delegate.autoAdvanceGame()
                }
            }
        }
        map[AutoEvent.Lock] = Runnable {
            launch(Android) {
                if (enabledEvents[AutoEvent.Lock] != true) { return@launch }
                delegate.autoLockGame()
                pause(AutoEvent.Lock)
            }
        }
        return@lazy map
    }

    /** Number of seconds remaining before the next auto advance event. */
    private var autoAdvanceSecondsRemaining: Int = 0

    /**
     * Enable the specified event.
     *
     * @param event the event to enable
     */
    fun enable(event: AutoEvent) {
        enabledEvents[event] = true
    }

    /**
     * Disable the specified event.
     *
     * @param event the event to disable
     */
    fun disable(event: AutoEvent) {
        autoEventHandler[event]?.removeCallbacks(autoEventRunnable[event])
        enabledEvents[event] = false
    }

    /**
     * Pause the specified event.
     *
     * @param event the event to pause
     */
    fun pause(event: AutoEvent) {
        autoEventHandler[event]?.removeCallbacks(autoEventRunnable[event])
    }

    /**
     * Start the specified event.
     *
     * @param event the event to pause
     */
    fun start(event: AutoEvent) {
        if (enabledEvents[event] != true) { return }
        autoEventHandler[event]?.postDelayed(autoEventRunnable[event], event.delay)
    }

    /**
     * Delay the specified event.
     *
     * @param event the event to delay
     */
    fun delay(event: AutoEvent) {
        pause(event)
        when (event) {
            AutoEvent.AdvanceFrame -> {
                autoAdvanceSecondsRemaining = autoAdvanceTotalDelay
            }
            else -> {} // Do nothing
        }
        start(event)
    }

    /**
     * Pause all events.
     */
    fun pauseAll() {
        AutoEvent.values().forEach { pause(it) }
    }

    /**
     * Automatic events.
     */
    enum class AutoEvent {
        AdvanceFrame, Lock;

        /** Delay before the event occurs, in milliseconds. */
        val delay: Long
            get() {
                return when (this) {
                    AdvanceFrame -> ADVANCE_FRAME_DELAY
                    Lock -> LOCK_DELAY
                }
            }

        companion object {
            /** Current delay for frame auto advance. */
            var ADVANCE_FRAME_DELAY = 1000L

            /** Current delay for auto lock. */
            var LOCK_DELAY = 5000L
        }
    }

    /**
     * Callbacks for automatic events.
     */
    interface GameAutoEventDelegate {
        /**
         * Auto lock the game.
         */
        fun autoLockGame()

        /**
         * Auto advance the current ball.
         */
        fun autoAdvanceGame()

        /**
         * Count down the auto advance timer.
         *
         * @param secondsRemaining seconds remaining until frame should auto advance
         */
        fun autoAdvanceCountDown(secondsRemaining: Int)
    }
}
