package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * BaseFragment for all fragments in the application.
 */
abstract class BaseFragment : Fragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "BaseFragment"

        /**
         * Create a new instance of [BaseFragment] based on [name] and return it.
         *
         * @param name class name to instantiate
         */
        fun newInstance(name: String): BaseFragment {
            try {
                val fragmentClass = Class.forName(name)
                return fragmentClass.newInstance() as BaseFragment
            } catch (ex: Exception) {
                throw RuntimeException("All fragments must be a subclass of BaseFragment")
            }
        }
    }

    /** Fragment navigation instance. */
    protected var fragmentNavigation: FragmentNavigation? = null

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? FragmentNavigation ?: throw RuntimeException("Parent activity must implement FragmentNavigation")
        fragmentNavigation = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        fragmentNavigation = null
    }

    /**
     * Accessor to activity's fragment stack
     */
    interface FragmentNavigation {

        /**
         * Push a new [Fragment] onto the stack
         *
         * @param fragment the fragment to push
         */
        fun pushFragment(fragment: BaseFragment)

        /**
         * Push a new [DialogFragment] onto the stack
         *
         * @param fragment the fragment to push
         */
        fun pushDialogFragment(fragment: DialogFragment)

        /**
         * Push a new [BottomSheetDialogFragment] onto the stack
         *
         * @param fragment the fragment to push
         * @param tag necessary tag for the fragment
         */
        fun showBottomSheet(fragment: BottomSheetDialogFragment, tag: String)
    }
}
