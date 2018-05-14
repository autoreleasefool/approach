package ca.josephroque.bowlingcompanion

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import kotlinx.android.synthetic.main.activity_navigation.*

/**
 * Activity to handle navigation across the app and through sub-fragments.
 */
class NavigationActivity : BaseActivity(),
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        BaseFragment.FragmentNavigation,
        TabbedFragment.TabbedFragmentDelegate
{

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "NavigationActivity"

        /**
         * Tabs at the bottom of the screen
         */
        enum class BottomTab {
            Record, Statistics, Equipment;

            companion object {
                private val map = BottomTab.values().associateBy(BottomTab::ordinal)
                fun fromInt(type: Int) = available[type]
                fun fromId(@IdRes id: Int): BottomTab {
                    return when (id) {
                        R.id.action_record -> Record
                        R.id.action_statistics -> Statistics
                        R.id.action_equipment -> Equipment
                        else -> throw RuntimeException("$id is not valid BottomTab id")
                    }
                }
                fun toId(tab: BottomTab): Int {
                    return when (tab) {
                        Record -> R.id.action_record
                        Statistics -> R.id.action_statistics
                        Equipment -> R.id.action_equipment
                    }
                }

                /** List of available tabs. */
                val available: List<BottomTab> by lazy {
                    map.entries.filter({ it.value.isAvailable }).map { it.value }
                }
            }

            /** Indicate if the tab is active and should be shown. */
            val isAvailable: Boolean
                get() {
                    return when (this) {
                        Record -> true
                        Statistics -> true
                        Equipment -> false // TODO: enable equipments tab when ready
                    }
                }
        }
    }

    /** Controller for fragment navigation. */
    private var fragNavController: FragNavController? = null

    /** Drawable to display in the floating action button. */
    private var fabImage: Int? = null
        set(value) {
            field = value
            if (fab.visibility == View.VISIBLE) {
                fab.hide(fabVisibilityChangeListener)
            } else {
                fabVisibilityChangeListener.onHidden(fab)
            }
        }

    /** Handle visibility changes in the fab. */
    private val fabVisibilityChangeListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.let {
                it.setColorFilter(Color.BLACK)
                val image = fabImage ?: return
                it.setImageResource(image)
                it.show()
            }
        }
    }

    /** @Override. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        setupToolbar()
        setupBottomNavigation()
        setupFab()
        setupFragNavController(savedInstanceState)
    }

    /** @Override */
    override fun onBackPressed() {
        if (fragNavController?.isRootFragment == true || fragNavController?.popFragment()?.not() == true) {
            super.onBackPressed()
        }
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragNavController?.onSaveInstanceState(outState!!)
    }

    /** @Override */
    override fun pushFragment(fragment: BaseFragment) {
        val transactionOptions = FragNavTransactionOptions.newBuilder()
                .transition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .build()
        fragNavController?.pushFragment(fragment, transactionOptions)
    }

    /** @Override */
    override fun pushDialogFragment(fragment: DialogFragment) {
        fragNavController?.showDialogFragment(fragment)
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Add listeners to bottom view navigation.
     */
    private fun setupBottomNavigation() {
        val unavailableTabs: Set<BottomTab> = BottomTab.values().toSet() - BottomTab.available.toSet()
        if (unavailableTabs.isNotEmpty()) {
            unavailableTabs.forEach { bottom_navigation.menu.removeItem(BottomTab.toId(it)) }
            bottom_navigation.invalidate()
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            fragNavController?.switchTab(BottomTab.fromId(it.itemId).ordinal)
            return@setOnNavigationItemSelectedListener true
        }

        bottom_navigation.setOnNavigationItemReselectedListener {
            // TODO: probably refresh the current fragment, not reset the stack
//            fragNavController?.clearStack()
        }
    }

    /**
     * Configure floating action button for rendering.
     */
    private fun setupFab() {
        fab.setOnClickListener {
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null && fragment.isVisible && fragment is IFloatingActionButtonHandler) {
                    fragment.onFabClick()
                }
            }
        }
    }

    /**
     * Build the [FragNavController] for bottom tab navigation.
     *
     * @param savedInstanceState the activity saved instance state
     */
    private fun setupFragNavController(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.fragment_container)
                .rootFragmentListener(this@NavigationActivity, BottomTab.available.size)
                .transactionListener(this@NavigationActivity)
        // TODO: look into .fragmentHideStrategy(FragNavController.HIDE), .eager(true)
        fragNavController = builder.build()
    }

    /** @Override */
    override fun getRootFragment(index: Int): Fragment {
        val tab = BottomTab.fromInt(index)
        val fragmentName: String
        fragmentName = when (tab) {
            BottomTab.Record -> BowlerTeamTabbedFragment::class.java.name
            BottomTab.Equipment -> BowlerListFragment::class.java.name // TODO: enable equipment tab
            BottomTab.Statistics -> BowlerListFragment::class.java.name // TODO: enable statistics tab
        }

        return BaseFragment.newInstance(fragmentName)
    }

    /** @Override */
    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController?.isRootFragment?.not() ?: false)
        if (fragment is IFloatingActionButtonHandler) {
            fabImage = fragment.getFabImage()
        }
    }

    /** @Override */
    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController?.isRootFragment?.not() ?: false)
        if (fragment is IFloatingActionButtonHandler) {
            fabImage = fragment.getFabImage()
        }
    }

    /** @Override */
    override fun onTabSwitched() {
        // Refresh floating action button image from the current fragment
        val fragment = fragNavController?.currentFrag
        if (fragment != null && fragment is IFloatingActionButtonHandler) {
            fabImage = fragment.getFabImage()
        }
    }
}
