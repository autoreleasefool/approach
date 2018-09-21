package ca.josephroque.bowlingcompanion.matchplay

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseBottomSheetDialogFragment
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.android.synthetic.main.sheet_match_play.view.*
import kotlinx.android.synthetic.main.sheet_match_play.input_opponent_name as opponentName
import kotlinx.android.synthetic.main.sheet_match_play.input_opponent_score as opponentScore
import kotlinx.android.synthetic.main.sheet_match_play.radio_match_play_lost as matchPlayLost
import kotlinx.android.synthetic.main.sheet_match_play.radio_match_play_won as matchPlayWon
import kotlinx.android.synthetic.main.sheet_match_play.radio_match_play_tied as matchPlayTied
import kotlinx.android.synthetic.main.sheet_match_play.radio_match_play_none as matchPlayNone

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays settings for the match play results of a game.
 */
class MatchPlaySheet : BaseBottomSheetDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "MatchPlaySheet"

        const val FRAGMENT_TAG = TAG
        const val ARG_MATCH_PLAY = "${TAG}_match_play"

        fun newInstance(matchPlay: MatchPlay): MatchPlaySheet {
            val fragment = MatchPlaySheet()
            fragment.arguments = Bundle().apply { putParcelable(ARG_MATCH_PLAY, matchPlay) }
            return fragment
        }
    }

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.sheet_match_play, container, false)

        if (savedInstanceState == null) {
            val initialMatchPlay: MatchPlay = arguments?.getParcelable(ARG_MATCH_PLAY)!!
            populateInitialInputs(rootView, initialMatchPlay)
        }

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            if (it is BottomSheetDialog) {
                val bottomSheet = it.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            handleUserExit()
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }

        return dialog
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        handleUserExit()
    }

    // MARK: Private functions

    private fun populateInitialInputs(rootView: View, initialMatchPlay: MatchPlay) {
        rootView.input_opponent_name.setText(initialMatchPlay.opponentName)
        rootView.input_opponent_score.setText(initialMatchPlay.opponentScore.toString())
        rootView.radio_match_play_none.isChecked = initialMatchPlay.result == MatchPlayResult.NONE
        rootView.radio_match_play_lost.isChecked = initialMatchPlay.result == MatchPlayResult.LOST
        rootView.radio_match_play_tied.isChecked = initialMatchPlay.result == MatchPlayResult.TIED
        rootView.radio_match_play_won.isChecked = initialMatchPlay.result == MatchPlayResult.WON
    }

    private fun handleUserExit() {
        var inputValid = true
        val name = opponentName.text.toString()
        val matchPlayResult = when {
            matchPlayWon.isChecked -> MatchPlayResult.WON
            matchPlayLost.isChecked -> MatchPlayResult.LOST
            matchPlayTied.isChecked -> MatchPlayResult.TIED
            else -> MatchPlayResult.NONE
        }
        var score: Int = -1
        try {
            score = opponentScore.text.toString().toInt()
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

        delegate?.getBottomSheetDelegate<MatchPlaySheetDelegate>()?.onFinishedSettingMatchPlayResults(name, score, matchPlayResult, inputValid)

        Analytics.trackRecordMatchPlay()
    }

    // MARK: MatchPlaySheetDelegate

    interface MatchPlaySheetDelegate {
        fun onFinishedSettingMatchPlayResults(
            opponentName: String,
            opponentScore: Int,
            matchPlayResult: MatchPlayResult,
            inputValid: Boolean
        )
    }
}
