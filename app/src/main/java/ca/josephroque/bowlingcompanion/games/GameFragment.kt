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
}