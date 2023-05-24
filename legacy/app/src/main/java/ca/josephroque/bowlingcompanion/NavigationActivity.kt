package ca.josephroque.bowlingcompanion

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
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
import ca.josephroque.bowlingcompanion.common.fragments.BaseBottomSheetDialogFragment
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IRefreshable
import ca.josephroque.bowlingcompanion.games.GameControllerFragment
import ca.josephroque.bowlingcompanion.series.SeriesListFragment
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProviderListFragment
import ca.josephroque.bowlingcompanion.teams.details.TeamDetailsFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.Permission
import ca.josephroque.bowlingcompanion.utils.StartupManager
import ca.josephroque.bowlingcompanion.utils.isVisible
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import kotlinx.android.synthetic.main.activity_navigation.bottom_navigation as bottomNavigation
import kotlinx.android.synthetic.main.activity_navigation.drawer_layout as drawerLayout
import kotlinx.android.synthetic.main.activity_navigation.fab as fab
import kotlinx.android.synthetic.main.activity_navigation.nav_drawer as navDrawer
import kotlinx.android.synthetic.main.activity_navigation.toolbar as toolbar
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to handle navigation across the app and through sub-fragments.
 */
class NavigationActivity : BaseActivity(),
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        BaseFragment.FragmentNavigation,
        BaseFragment.FabProvider,
        NavigationDrawerController.NavigationDrawerProvider,
        TabbedFragment.TabbedFragmentDelegate,
        BaseDialogFragment.OnDismissListener,
        BaseBottomSheetDialogFragment.BaseBottomSheetDialogFragmentDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "NavigationActivity"

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

                val available: List<BottomTab> by lazy {
                    map.entries.asSequence().filter { it.value.isAvailable }.map { it.value }.toList()
                }
            }

            val isAvailable: Boolean
                get() {
                    return when (this) {
                        Record -> true
                        Statistics -> true
                        Equipment -> false // FIXME: enable equipments tab when ready
                    }
                }
        }
    }

    private var fragNavController: FragNavController? = null
    override lateinit var navigationDrawerController: NavigationDrawerController
    private lateinit var fabController: FabController

    private var poppedBack = false

    override val stackSize: Int
        get() = fragNavController?.currentStack?.size ?: 0

    private val currentFragment: BaseFragment?
        get() {
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null && fragment.isVisible) {
                    return fragment as? BaseFragment ?: throw java.lang.RuntimeException("$fragment is not a BaseFragment")
                }
            }
            return null
        }

    val isFullscreen: Boolean
        get() = !bottomNavigation.isVisible

    private var currentBottomSheet: BaseBottomSheetDialogFragment.Companion.BottomSheetType? = null

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // If you don't have an analytics token available, comment out the following line and uncomment
        // the one after it to disable analytics altogether.
        // This is only available for debug builds, and a token must be provided for release builds
        Analytics.initialize(this)
        // Analytics.disableTracking()

        setupToolbar()
        setupNavigationDrawer() // Must be called after setupToolbar so that `handleNavigationDrawer` can set home indicator properly
        setupBottomNavigation()
        setupFab()
        setupFragNavController(savedInstanceState)
        StartupManager.start(this)
    }

    override fun onStop() {
        super.onStop()
        if (isFullscreen) {
            toggleFullscreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Analytics.flush()
    }

    override fun onBackPressed() {
        if (fragNavController?.isStateSaved == true) {
            super.onBackPressed()
            return
        }

        val fragNavController = fragNavController
        if (fragNavController != null) {
            if (currentFragment?.popChildFragment() == true) {
                return
            }

            if (BottomTab.fromInt(fragNavController.currentStackIndex) == BottomTab.Statistics && fragNavController.isRootFragment) {
                fragNavController.switchTab(BottomTab.Record.ordinal)
                bottomNavigation.selectedItemId = BottomTab.toId(BottomTab.Record)
                return
            }

            if (fragNavController.isRootFragment || fragNavController.popFragment().not()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        currentFragment?.let {
            if (item.itemId == android.R.id.home && currentFragment is NavigationDrawerController.NavigationDrawerHandler) {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragNavController?.onSaveInstanceState(outState!!)
    }

    // MARK: ActivityCompat

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            currentFragment?.permissionGranted(Permission.fromRequestCode(requestCode))
        }
    }

    // MARK: FragmentNavigation

    override fun pushFragment(fragment: BaseFragment) {
        val transactionOptions = FragNavTransactionOptions.newBuilder()
                .transition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .build()
        fragNavController?.pushFragment(fragment, transactionOptions)
    }

    override fun pushDialogFragment(fragment: BaseDialogFragment) {
        fragment.onDismissListener = this
        fragNavController?.showDialogFragment(fragment)
    }

    override fun showBottomSheet(fragment: BaseBottomSheetDialogFragment, tag: String) {
        currentBottomSheet = BaseBottomSheetDialogFragment.getBottomSheetType(fragment)
        fragment.show(supportFragmentManager, tag)
    }

    // MARK: BaseBottomSheetDialogFragmentDelegate

    @Suppress("UNCHECKED_CAST")
    override fun <Delegate> getBottomSheetDelegate(): Delegate? {
        return when (currentBottomSheet) {
            BaseBottomSheetDialogFragment.Companion.BottomSheetType.MatchPlay -> currentFragment as? Delegate
            else -> null
        }
    }

    override fun bottomSheetDetached() {
        currentBottomSheet = null
    }

    // MARK: FabProvider

    override fun invalidateFab() {
        val fragment = currentFragment
        fabController.image = if (fragment is IFloatingActionButtonHandler) {
            fragment.getFabImage()
        } else {
            null
        }
    }

    // MARK: RootFragmentListener

    override fun getRootFragment(index: Int): Fragment {
        val tab = BottomTab.fromInt(index)
        val fragmentName: String
        fragmentName = when (tab) {
            BottomTab.Record -> BowlerTeamTabbedFragment::class.java.name
            BottomTab.Equipment -> BowlerListFragment::class.java.name // FIXME: enable equipment tab
            BottomTab.Statistics -> StatisticsProviderListFragment::class.java.name
        }

        return BaseFragment.newInstance(fragmentName)
    }

    // MARK: TransactionListener

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType?) {
        handleFragmentChange(fragment, transactionType)
    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        handleFragmentChange(fragment)
    }

    // MARK: TabbedFragmentDelegate

    override fun onTabSwitched() {
        invalidateFab()
    }

    // MARK: NavigationActivity

    fun setToolbarTitle(title: String? = null, subtitle: String? = null) {
        supportActionBar?.title = title
        supportActionBar?.subtitle = subtitle
    }

    fun toggleFullscreen() {
        if (!isFullscreen) {
            supportActionBar?.hide()
            bottomNavigation.visibility = View.GONE
        } else {
            supportActionBar?.show()
            bottomNavigation.visibility = View.VISIBLE
        }
    }

    // MARK: Private functions

    private fun handleFragmentChange(fragment: Fragment?, transactionType: FragNavController.TransactionType? = null) {
        fragNavController?.let {
            val showBackButton = it.isRootFragment.not() || BottomTab.fromInt(it.currentStackIndex) == BottomTab.Statistics
            supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
        }

        fabController.image = if (fragment is IFloatingActionButtonHandler) {
            fragment.getFabImage()
        } else {
            null
        }

        handleNavigationDrawer(fragment)

        toolbar.elevation = if (fragment is TabbedFragment) {
            0F
        } else {
            resources.getDimension(R.dimen.base_elevation)
        }

        if (isFullscreen) {
            toggleFullscreen()
        }

        if (poppedBack) {
            poppedBack = false
            refreshCurrentFragment()
        }

        if (fragment is StatisticsProviderListFragment && transactionType != FragNavController.TransactionType.POP) {
            val statisticsContext = fragNavController?.getStack(BottomTab.toInt(BottomTab.Record))?.peek() as? IStatisticsContext
            fragment.arguments = StatisticsProviderListFragment.buildArguments(statisticsContext?.statisticsProviders ?: emptyList())
        }
    }

    private fun handleNavigationDrawer(fragment: Fragment?) {
        if (fragment is NavigationDrawerController.NavigationDrawerHandler) {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            navigationDrawerController.apply {
                isTeamMember = false
                isEvent = false
                gameNumber = 0
                numberOfGames = 0
                bowlerName = null
                leagueName = null
            }
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

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
    }

    private fun setupNavigationDrawer() {
        navigationDrawerController = NavigationDrawerController(WeakReference(navDrawer))
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
                        if (it is NavigationDrawerController.NavigationDrawerHandler) {
                            it.onNavDrawerItemSelected(menuItem.itemId)
                        }
                    }
                }
            }

            return@setNavigationItemSelectedListener true
        }

        supportFragmentManager.fragments.forEach {
            if (it is NavigationDrawerController.NavigationDrawerHandler) {
                handleNavigationDrawer(it)
            }
        }
    }

    private fun setupFab() {
        fabController = FabController(fab, View.OnClickListener {
            val currentFragment = currentFragment ?: return@OnClickListener
            if (currentFragment is IFloatingActionButtonHandler) {
                currentFragment.onFabClick()
            }
        })
    }

    private fun setupFragNavController(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.fragment_container)
                .rootFragmentListener(this@NavigationActivity, BottomTab.available.size)
                .transactionListener(this@NavigationActivity)
        // FIXME: look into .fragmentHideStrategy(FragNavController.HIDE), .eager(true)
        fragNavController = builder.build()
    }

    private fun popBackTo(fragmentName: String) {
        val fragments = fragNavController?.currentStack ?: return
        var popTarget: Int? = null

        for (i in 0 until fragments.size) {
            if (fragments[i]::class.java.name == fragmentName) {
                popTarget = fragments.size - i - 1
                break
            }
        }

        popTarget?.let {
            poppedBack = true
            val currentFragment = this@NavigationActivity.currentFragment
            if (currentFragment is GameControllerFragment) {
                currentFragment.prepareToPop()
            }
            fragNavController?.popFragments(it)
        }
    }

    private fun refreshCurrentFragment() {
        val currentFragment = currentFragment ?: return
        if (currentFragment is IRefreshable) {
            currentFragment.refresh()
        }
    }

    // MARK: OnDismissListener

    override fun onDismiss(dismissedFragment: BaseDialogFragment) {
        refreshCurrentFragment()
    }
}
