package ca.josephroque.bowlingcompanion.games

import android.os.Handler
import ca.josephroque.bowlingcompanion.common.Android
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Auto lock the game.
 */
class AutoLockController(private var isEnabled: Boolean, private val delegate: AutoLockDelegate) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "AutoLockController"

        /** Number of seconds before auto locking a game in the final frame. */
        private const val AUTO_LOCK_DELAY = 5000L
    }

    /** Handler to lock the game after editing. */
    private val autoLockHandler = Handler()

    /** Runnable to lock the game. */
    private val autoLockRunnable = Runnable {
        launch(Android) {
            if (isEnabled) {
                delegate.autoLockGame()
            }
        }
    }

    /**
     * Pause the auto lock function.
     */
    fun pause() {
        autoLockHandler.removeCallbacks(autoLockRunnable)
    }

    /**
     * Start the auto lock delay.
     */
    fun start() {
        if (isEnabled) {
            autoLockHandler.postDelayed(autoLockRunnable, AUTO_LOCK_DELAY)
        }
    }

    /**
     * Disable the auto lock functionality.
     */
    fun manualOverride() {
        pause()
        isEnabled = false
    }

    /**
     * Resume the auto lock function.
     */
    fun extend() {
        autoLockHandler.removeCallbacks(autoLockRunnable)
        start()
    }

    /**
     * Callbacks for auto lock events.
     */
    interface AutoLockDelegate {
        /**
         * Auto lock the game.
         */
        fun autoLockGame()
    }
}
