package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.v4.app.Fragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * BaseFragment for all fragments in the application.
 */
abstract class BaseFragment : Fragment() {

    companion object {
        /** Logging identifier. */
        private val TAG = BaseFragment::class.java.simpleName

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
         * Push a new fragment onto the stack
         *
         * @param fragment the fragment to push
         */
        fun pushFragment(fragment: BaseFragment)
    }

}