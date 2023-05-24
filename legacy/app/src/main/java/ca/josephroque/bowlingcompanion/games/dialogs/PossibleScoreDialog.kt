package ca.josephroque.bowlingcompanion.games.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.utils.Analytics

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Calculate the best score the user could possibly get and display it.
 */
object PossibleScoreDialog {
    fun show(context: Context, currentGame: Game, currentFrame: Int, currentBall: Int) {
        // Get the best ball the user could throw for this frame
        val ball = if (currentFrame == Game.LAST_FRAME) {
            when {
                currentBall == 0 || currentGame.frames[currentFrame].pinState[currentBall - 1].arePinsCleared ->
                    context.resources.getString(R.string.best_possible_strike)
                currentBall == 1 || currentGame.frames[currentFrame].pinState[currentBall - 2].arePinsCleared ->
                    context.resources.getString(R.string.best_possible_spare)
                else ->
                    context.resources.getString(R.string.best_possible_spare)
            }
            context.resources.getString(R.string.best_possible_strike)
        } else {
            when (currentBall) {
                0 -> context.resources.getString(R.string.best_possible_strike)
                1 -> context.resources.getString(R.string.best_possible_spare)
                else -> context.resources.getString(R.string.best_possible_fifteen)
            }
        }

        // Set rest of game to strikes and clear all fouls
        currentGame.frames.forEachIndexed { index, frame ->
            for (i in 0 until Frame.NUMBER_OF_BALLS) {
                frame.ballFouled[i] = false

                if (index > currentFrame) {
                    frame.pinState[i].forEach { it.isDown = true }
                }
            }
        }

        // Set rest of frame to be cleared
        currentGame.frames[currentFrame].pinState.forEachIndexed { index, deck ->
            if (index >= currentBall) {
                deck.forEach { it.isDown = true }
            }
        }

        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_best_possible_title)
                .setMessage(context.resources.getString(R.string.dialog_best_possible_message, ball, currentGame.score))
                .setPositiveButton(R.string.okay, null)
                .create()
                .show()

        Analytics.trackViewPossibleScore(currentGame.score, currentFrame)
    }
}
