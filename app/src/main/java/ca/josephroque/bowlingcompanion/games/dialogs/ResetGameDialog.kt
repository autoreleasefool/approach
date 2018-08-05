package ca.josephroque.bowlingcompanion.games.dialogs

import android.content.Context
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A dialog which prompts the user to reset their game.
 */
object ResetGameDialog {

    /**
     * Prompt the user to reset their game
     *
     * @param context to build the dialog
     * @param onResetGame weak reference to callback if user says yes
     */
    fun show(context: Context, onResetGame: WeakReference<() -> Unit>) {
        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_reset_game_title)
                .setMessage(R.string.dialog_reset_game_message)
                .setPositiveButton(R.string.reset) { _, _ ->
                    onResetGame.get()?.invoke()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
    }
}
