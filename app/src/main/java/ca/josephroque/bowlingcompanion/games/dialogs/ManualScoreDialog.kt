package ca.josephroque.bowlingcompanion.games.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R
import android.view.LayoutInflater
import android.widget.EditText

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Present dialogs to set and clear a manual score in a game.
 */
object ManualScoreDialog {

    @SuppressLint("InflateParams")
    fun showSetScoreDialog(context: Context, onSetScore: (score: Int) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_set_score, null, false)

        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_set_score_title)
                .setView(view)
                .setPositiveButton(R.string.set_score) { dialog, _ ->
                    val scoreText = view.findViewById<EditText>(R.id.score)
                    if (scoreText.length() > 0) {
                        val gameScore = try {
                            scoreText.text.toString().toInt()
                        } catch (ex: NumberFormatException) {
                            -1
                        }

                        onSetScore(gameScore)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    fun showClearScoreDialog(context: Context, onClearScore: () -> Unit) {
        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_clear_score_title)
                .setMessage(R.string.dialog_clear_score_message)
                .setPositiveButton(R.string.clear_score) { _, _ ->
                    onClearScore()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
    }
}
