package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display game details and allow the user to edit.
 */
class GameFragment : BaseFragment(),
        IFloatingActionButtonHandler {

    /** IDs for frame views. */
    private val frameViewIds = intArrayOf(R.id.frame_1, R.id.frame_2, R.id.frame_3, R.id.frame_4,
            R.id.frame_5, R.id.frame_6, R.id.frame_7, R.id.frame_8, R.id.frame_9, R.id.frame_10)

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        return view
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward_white_24dp
    }

    /** @Override */
    override fun onFabClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "GameFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}
