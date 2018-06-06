package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.v4.app.DialogFragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * BaseDialogFragment for all dialog fragments in the application.
 */
abstract class BaseDialogFragment : DialogFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "BaseDialogFragment"
    }

    /** Fragment navigation instance. */
    protected var fragmentNavigation: BaseFragment.FragmentNavigation? = null

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? BaseFragment.FragmentNavigation ?: throw RuntimeException("Parent activity must implement FragmentNavigation")
        fragmentNavigation = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        fragmentNavigation = null
    }
}
