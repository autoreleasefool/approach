package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import ca.josephroque.bowlingcompanion.NavigationActivity
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * BaseDialogFragment for all dialog fragments in the application.
 */
abstract class BaseDialogFragment : DialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseDialogFragment"
    }

    protected var fragmentNavigation: BaseFragment.FragmentNavigation? = null

    protected var fabProvider: BaseFragment.FabProvider? = null

    var onDismissListener: OnDismissListener? = null

    protected val navigationActivity: NavigationActivity?
        get() = activity as? NavigationActivity

    // MARK: Lifecycle functions

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentNavigation = context as? BaseFragment.FragmentNavigation
        fabProvider = context as? BaseFragment.FabProvider
        onDismissListener = context as? OnDismissListener
    }

    override fun onDetach() {
        super.onDetach()
        fragmentNavigation = null
        fabProvider = null
        onDismissListener = null
    }

    override fun dismiss() {
        onDismissListener?.onDismiss(this)
        super.dismiss()
    }

    // MARK: OnDismissListener

    interface OnDismissListener {
        fun onDismiss(dismissedFragment: BaseDialogFragment)
    }
}
