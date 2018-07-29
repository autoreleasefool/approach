package ca.josephroque.bowlingcompanion.games

import android.os.Handler
import ca.josephroque.bowlingcompanion.common.Android
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Control automatic game events.
 */
class GameAutoEventController(private val delegate: GameAutoEventDelegate) {

    init {
        AutoEvent.values().forEach { enabledEvents[it] = false }
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameAutoEventController"
    }

    /** Set of events mapped to their enabled status. */
    private val enabledEvents: HashMap<AutoEvent, Boolean> = HashMap()

    /** Handlers to perform events. */
    private val autoEventHandler: HashMap<AutoEvent, Handler> = HashMap()

    /** Runnable to execute the automatic events. */
    private val autoEventRunnable: HashMap<AutoEvent, Runnable> by lazy {
        val map = HashMap<AutoEvent, Runnable>()
        map[AutoEvent.AdvanceFrame] = Runnable {  }
        map[AutoEvent.Lock] = Runnable {
            launch(Android) {
                if (enabledEvents[AutoEvent.Lock] == true) {
                    delegate.autoLockGame()
                }
            }
        }
        return@lazy map
    }

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
        autoEventHandler[event]?.postDelayed(autoEventRunnable[event], event.delay)
    }

    /**
     * Delay the specified event.
     *
     * @param event the event to delay
     */
    fun delay(event: AutoEvent) {
        autoEventHandler[event]?.removeCallbacks(autoEventRunnable[event])
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
                    AdvanceFrame -> 5000L
                    Lock -> 5000L
                }
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
    }
}
