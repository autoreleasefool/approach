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
        @Suppress("unused")
        private const val TAG = "GameAutoEventController"

        private var autoAdvanceTotalDelay: Int = 0
    }

    init { init(preferences) }

    fun init(preferences: SharedPreferences) {
        // Enable auto lock
        val autoLockEnabled = Settings.BooleanSetting.EnableAutoLock.getValue(preferences)
        if (autoLockEnabled) {
            enable(AutoEvent.Lock)
        } else {
            AutoEvent.Lock.isEnabled = false
        }

        // Enable auto advance
        val autoAdvanceEnabled = Settings.BooleanSetting.EnableAutoAdvance.getValue(preferences)
        if (autoAdvanceEnabled) {
            enable(AutoEvent.AdvanceFrame)
        } else {
            AutoEvent.AdvanceFrame.isEnabled = false
        }

        // Set auto advance delay time
        val strDelay = Settings.StringSetting.AutoAdvanceTime.getValue(preferences)
        val strDelayComponents = strDelay.split(" ")
        autoAdvanceTotalDelay = Integer.valueOf(strDelayComponents[0])
    }

    private val autoEventHandler: HashMap<AutoEvent, Handler> by lazy {
        val map = HashMap<AutoEvent, Handler>()
        AutoEvent.values().forEach { map[it] = Handler() }
        return@lazy map
    }

    private val autoEventRunnable: HashMap<AutoEvent, Runnable> by lazy {
        val map = HashMap<AutoEvent, Runnable>()
        map[AutoEvent.AdvanceFrame] = Runnable {
            launch(Android) {
                if (!AutoEvent.AdvanceFrame.isEnabled) { return@launch }
                if (autoAdvanceSecondsRemaining > 0) {
                    autoAdvanceSecondsRemaining -= 1
                    start(AutoEvent.AdvanceFrame)
                    delegate.autoAdvanceCountDown(autoAdvanceSecondsRemaining)
                } else {
                    delegate.autoEventFired(AutoEvent.AdvanceFrame)
                }
            }
        }
        map[AutoEvent.Lock] = Runnable {
            launch(Android) {
                if (!AutoEvent.Lock.isEnabled) { return@launch }
                delegate.autoEventFired(AutoEvent.Lock)
                pause(AutoEvent.Lock)
            }
        }
        return@lazy map
    }

    private var autoAdvanceSecondsRemaining: Int = 0

    // MARK: GameAutoEventController

    fun disable(event: AutoEvent) {
        autoEventHandler[event]?.removeCallbacks(autoEventRunnable[event])
        event.isEnabled = false
    }

    fun pause(event: AutoEvent) {
        autoEventHandler[event]?.removeCallbacks(autoEventRunnable[event])
        delegate.autoEventPaused(event)
    }

    fun start(event: AutoEvent) {
        if (!event.isEnabled) { return }
        autoEventHandler[event]?.postDelayed(autoEventRunnable[event], event.delay)
    }

    fun delay(event: AutoEvent) {
        pause(event)
        when (event) {
            AutoEvent.AdvanceFrame -> autoAdvanceSecondsRemaining = autoAdvanceTotalDelay
            AutoEvent.Lock -> {} // Do nothing
        }
        start(event)
        delegate.autoEventDelayed(event)
    }

    fun pauseAll() {
        AutoEvent.values().forEach { pause(it) }
    }

    // MARK: Private functions

    private fun enable(event: AutoEvent) {
        event.isEnabled = true
    }

    // MARK: AutoEvent

    enum class AutoEvent {
        AdvanceFrame, Lock;

        val delay: Long
            get() {
                return when (this) {
                    AdvanceFrame -> ADVANCE_FRAME_DELAY
                    Lock -> LOCK_DELAY
                }
            }

        var isEnabled: Boolean = false

        companion object {
            var ADVANCE_FRAME_DELAY = 1000L
            var LOCK_DELAY = 5000L
        }
    }

    // MARK: GameAutoEventDelegate

    interface GameAutoEventDelegate {
        fun autoEventFired(event: AutoEvent)
        fun autoEventDelayed(event: AutoEvent)
        fun autoEventPaused(event: AutoEvent)
        fun autoAdvanceCountDown(secondsRemaining: Int)
    }
}
