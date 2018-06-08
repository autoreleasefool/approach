package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import ca.josephroque.bowlingcompanion.R

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

    /** Fab provider instance. */
    protected var fabProvider: BaseFragment.FabProvider? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? BaseFragment.FragmentNavigation ?: throw RuntimeException("Parent activity must implement FragmentNavigation")
        fragmentNavigation = context
        context as? BaseFragment.FabProvider ?: throw RuntimeException("Parent activity must implement FabProvider")
        fabProvider = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        fragmentNavigation = null
        fabProvider = null
    }
}
