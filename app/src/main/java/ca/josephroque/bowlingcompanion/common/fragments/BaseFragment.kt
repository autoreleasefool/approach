package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.v4.app.Fragment
import ca.josephroque.bowlingcompanion.NavigationActivity
import ca.josephroque.bowlingcompanion.utils.Permission

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * BaseFragment for all fragments in the application.
 */
abstract class BaseFragment : Fragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseFragment"

        fun newInstance(name: String): BaseFragment {
            try {
                val fragmentClass = Class.forName(name)
                return fragmentClass.newInstance() as BaseFragment
            } catch (ex: Exception) {
                throw RuntimeException("All fragments must be a subclass of BaseFragment")
            }
        }
    }

    protected var fragmentNavigation: FragmentNavigation? = null
    protected var fabProvider: FabProvider? = null

    protected val navigationActivity: NavigationActivity?
        get() = activity as? NavigationActivity

    // MARK: BaseFragment

    abstract fun updateToolbarTitle()

    open fun popChildFragment(): Boolean = false

    open fun permissionGranted(permission: Permission) {}

    // MARK: Lifecycle functions

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentNavigation = context as? BaseFragment.FragmentNavigation
        fabProvider = context as? BaseFragment.FabProvider
    }

    override fun onDetach() {
        super.onDetach()
        fragmentNavigation = null
        fabProvider = null
    }

    override fun onStart() {
        super.onStart()
        updateToolbarTitle()
    }

    // MARK: FragmentNavigation

    interface FragmentNavigation {

        val stackSize: Int

        fun pushFragment(fragment: BaseFragment)
        fun pushDialogFragment(fragment: BaseDialogFragment)
        fun showBottomSheet(fragment: BaseBottomSheetDialogFragment, tag: String)
    }

    // MARK: FabProvider

    interface FabProvider {
        fun invalidateFab()
    }
}
