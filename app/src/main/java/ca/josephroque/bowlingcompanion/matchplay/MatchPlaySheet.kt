package ca.josephroque.bowlingcompanion.matchplay

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays settings for the match play results of a game.
 */
class MatchPlaySheet : BottomSheetDialogFragment() {

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_match_play, container, false)
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
}
