package ca.josephroque.bowlingcompanion.matchplay

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.android.synthetic.main.sheet_match_play.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays settings for the match play results of a game.
 */
class MatchPlaySheet : BottomSheetDialogFragment() {

    /** Interaction delegate. */
    private var delegate: MatchPlaySheetDelegate? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_match_play, container, false)
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            if (it is BottomSheetDialog) {
                val bottomSheet = it.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                    /** @Override */
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            handleUserExit()
                            dismiss()
                        }
                    }

                    /** @Override */
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }

        return dialog
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parentFragment = parentFragment as? MatchPlaySheetDelegate ?: throw RuntimeException("${parentFragment!!} must implement MatchPlaySheetDelegate")
        delegate = parentFragment
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    /** @Override */
    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        handleUserExit()
    }

    /**
     * Form validation when user dismisses the dialog.
     */
    private fun handleUserExit() {
        var inputValid = true
        val name = input_opponent_name.text.toString()
        val matchPlayResult = when {
            radio_match_play_won.isChecked -> MatchPlayResult.WON
            radio_match_play_lost.isChecked -> MatchPlayResult.LOST
            radio_match_play_tied.isChecked -> MatchPlayResult.TIED
            else -> MatchPlayResult.NONE
        }
        var score: Int = -1
        try {
            score = input_opponent_score.text.toString().toInt()
            if (score < 0 || score > Game.MAX_SCORE) {
                throw NumberFormatException()
            }
        } catch (ex: NumberFormatException) {
            inputValid = false
            context?.let {
                BCError(
                        R.string.issue_setting_match_play,
                        R.string.error_match_play_score_invalid,
                        BCError.Severity.Warning
                ).show(it)
            }
        }

        delegate?.onFinishedSettingMatchPlayResults(name, score, matchPlayResult, inputValid)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "MatchPlaySheet"

        /** Fragment Manager identifier. */
        const val FRAGMENT_TAG = TAG

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): MatchPlaySheet {
            return MatchPlaySheet()
        }
    }

    /**
     * Handle interactions with the fragment.
     */
    interface MatchPlaySheetDelegate {

        /**
         * Update match play settings when the user has finished editing.
         *
         * @param opponentName name of the opponent for match play
         * @param opponentScore score of the opponent for match play
         * @param matchPlayResult result of the match play
         * @param inputValid indicates if the user input is valid or not
         */
        fun onFinishedSettingMatchPlayResults(
            opponentName: String,
            opponentScore: Int,
            matchPlayResult: MatchPlayResult,
            inputValid: Boolean
        )
    }
}
