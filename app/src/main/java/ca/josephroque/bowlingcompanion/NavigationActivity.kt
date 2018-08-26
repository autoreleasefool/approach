package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.View
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.FabController
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.common.interfaces.INavigationDrawerHandler
import ca.josephroque.bowlingcompanion.series.SeriesListFragment
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.BaseStatisticsFragment
import ca.josephroque.bowlingcompanion.teams.details.TeamDetailsFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import kotlinx.android.synthetic.main.activity_navigation.bottom_navigation as bottomNavigation
import kotlinx.android.synthetic.main.activity_navigation.drawer_layout as drawerLayout
import kotlinx.android.synthetic.main.activity_navigation.fab as fab
import kotlinx.android.synthetic.main.activity_navigation.nav_drawer as navDrawer
import kotlinx.android.synthetic.main.activity_navigation.toolbar as toolbar
import java.lang.ref.WeakReference

/**
 * Activity to handle navigation across the app and through sub-fragments.
 */
class NavigationActivity : BaseActivity(),
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        BaseFragment.FragmentNavigation,
        BaseFragment.FabProvider,
        TabbedFragment.TabbedFragmentDelegate {

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
                fun toInt(type: BottomTab) = available.indexOf(type)
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
                    map.entries.filter { it.value.isAvailable }.map { it.value }
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

    /** Controller for navigation drawer. */
    private lateinit var navDrawerController: NavigationDrawerController

    /** Controller for floating action button. */
    private lateinit var fabController: FabController

    /** @Override */
    override val stackSize: Int
        get() = fragNavController?.currentStack?.size ?: 0

    /** The current visible fragment in the activity. */
    private val currentFragment: Fragment?
        get() {
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null && fragment.isVisible) {
                    return fragment
                }
            }
            return null
        }

    /** @Override. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // If you don't have an analytics token available, comment out the following line and uncomment
        // the one after it to disable analytics altogether.
        // This is only available for debug builds, and a token must be provided for release builds
        Analytics.initialize(this)
        // Analytics.disableTracking()

        setupToolbar()
        setupNavigationDrawer()
        setupBottomNavigation()
        setupFab()
        setupFragNavController(savedInstanceState)
    }

    /** @Override */
    override fun onDestroy() {
        super.onDestroy()
        Analytics.flush()
    }

    /** @Override */
    override fun onBackPressed() {
        if (fragNavController?.isRootFragment == true || fragNavController?.popFragment()?.not() == true) {
            super.onBackPressed()
        }
    }

    /** @Override */
    override fun onSupportNavigateUp(): Boolean {
        return if (fragNavController?.isRootFragment == true || fragNavController?.popFragment()?.not() == true) {
            false
        } else {
            super.onSupportNavigateUp()
        }
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        currentFragment?.let {
            if (item.itemId == android.R.id.home && currentFragment is INavigationDrawerHandler) {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
    override fun pushDialogFragment(fragment: BaseDialogFragment) {
        fragNavController?.showDialogFragment(fragment)
    }

    /** @Override */
    override fun showBottomSheet(fragment: BottomSheetDialogFragment, tag: String) {
        fragment.show(supportFragmentManager, tag)
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
            unavailableTabs.forEach { bottomNavigation.menu.removeItem(BottomTab.toId(it)) }
            bottomNavigation.invalidate()
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            fragNavController?.switchTab(BottomTab.fromId(it.itemId).ordinal)
            return@setOnNavigationItemSelectedListener true
        }

        bottomNavigation.setOnNavigationItemReselectedListener {
            // TODO: probably refresh the current fragment, not reset the stack
//            fragNavController?.clearStack()
        }
    }

    /** Add listeners to navigation drawer. */
    private fun setupNavigationDrawer() {
        navDrawerController = NavigationDrawerController(WeakReference(navDrawer))
        navDrawer.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.isCheckable) {
                menuItem.isChecked = true
            }
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_bowlers_teams -> popBackTo(BowlerTeamTabbedFragment::class.java.name)
                R.id.nav_leagues_events -> {
                    popBackTo(TeamDetailsFragment::class.java.name)
                    popBackTo(LeagueEventTabbedFragment::class.java.name)
                }
                R.id.nav_series -> popBackTo(SeriesListFragment::class.java.name)
                R.id.nav_feedback -> prepareFeedbackEmail()
                R.id.nav_settings -> openSettings()
                else -> {
                    currentFragment?.let {
                        if (it is INavigationDrawerHandler) {
                            it.onNavDrawerItemSelected(menuItem.itemId)
                        }
                    }
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    /** @Override */
    override fun invalidateFab() {
        val fragment = currentFragment
        fabController.image = if (fragment is IFloatingActionButtonHandler) {
            fragment.getFabImage()
        } else {
            null
        }
    }

    /**
     * Configure floating action button for rendering.
     */
    private fun setupFab() {
        fabController = FabController(fab, View.OnClickListener {
            val currentFragment = currentFragment ?: return@OnClickListener
            if (currentFragment is IFloatingActionButtonHandler) {
                currentFragment.onFabClick()
            }
        })
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
            BottomTab.Statistics -> BaseStatisticsFragment::class.java.name
        }

        return BaseFragment.newInstance(fragmentName)
    }

    /** @Override */
    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType?) {
        handleFragmentChange(fragment)
    }

    /** @Override */
    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        handleFragmentChange(fragment)

        if (BottomTab.fromInt(index) == BottomTab.Statistics) {
            fragNavController?.clearStack()
        }
    }

    /**
     * Update activity state for fragment changes.
     *
     * @param fragment the new fragment being displayed
     */
    private fun handleFragmentChange(fragment: Fragment?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController?.isRootFragment?.not() ?: false)
        fabController.image = if (fragment is IFloatingActionButtonHandler) {
            fragment.getFabImage()
        } else {
            null
        }

        if (fragment is INavigationDrawerHandler) {
            fragment.navigationDrawerController = navDrawerController
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        toolbar.elevation = if (fragment is TabbedFragment) {
            0F
        } else {
            resources.getDimension(R.dimen.base_elevation)
        }

        if (fragment is BaseStatisticsFragment) {
            val statisticsContext = fragNavController?.getStack(BottomTab.toInt(BottomTab.Record))?.peek() as? IStatisticsContext
                    ?: return
            fragment.arguments = BaseStatisticsFragment.buildArguments(statisticsContext.statisticsProviders)
        }
    }

    /** @Override */
    override fun onTabSwitched() {
        invalidateFab()
    }

    /**
     * Set the title and subtitle of the toolbar.
     *
     * @param title title for the toolbar
     * @param subtitle subtitle for the toolbar
     */
    fun setToolbarTitle(title: String? = null, subtitle: String? = null) {
        supportActionBar?.title = title
        supportActionBar?.subtitle = subtitle
    }

    /**
     * Pop back to a fragment in the stack.
     *
     * @param fragmentName name of the fragment to show
     */
    private fun popBackTo(fragmentName: String) {
        val fragments = fragNavController?.currentStack ?: return
        var popTarget: Int? = null

        for (i in 0 until fragments.size) {
            if (fragments[i]::class.java.name == fragmentName) {
                popTarget = fragments.size - i - 1
                break
            }
        }

        popTarget?.let { fragNavController?.popFragments(it) }
    }
}
