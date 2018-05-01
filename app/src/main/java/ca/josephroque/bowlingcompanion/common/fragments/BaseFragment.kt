package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.v4.app.Fragment
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity

/**
 * Copyright (C) 2018 Joseph Roque
 */
open class BaseFragment : Fragment() {

    companion object {
        /** Logging identifier. */
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

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as? BaseActivity) ?: throw RuntimeException("Parent activity must be instance of BaseActivity")
    }

}