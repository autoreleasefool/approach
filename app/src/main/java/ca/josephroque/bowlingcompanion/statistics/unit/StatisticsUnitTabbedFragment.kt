package ca.josephroque.bowlingcompanion.statistics.unit

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * View a list of statistics for each unit in a tabbed layout.
 */
class StatisticsUnitTabbedFragment : TabbedFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "StatUnitTabbedFragment"

        private const val ARG_STATISTICS_PROVIDER_TYPE = "${TAG}_type"
        private const val ARG_STATISTICS_PROVIDER = "${TAG}_stats"

        fun newInstance(statisticsProvider: StatisticsProvider): StatisticsUnitTabbedFragment {
            val newInstance = StatisticsUnitTabbedFragment()
            newInstance.arguments = Bundle().apply {
                putInt(ARG_STATISTICS_PROVIDER_TYPE, statisticsProvider.describeContents())
                putParcelable(ARG_STATISTICS_PROVIDER, statisticsProvider)
            }
            return newInstance
        }
    }

    private lateinit var statisticsProvider: StatisticsProvider

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type = arguments?.getInt(ARG_STATISTICS_PROVIDER_TYPE)!!
        statisticsProvider = StatisticsProvider.getParcelable(arguments, ARG_STATISTICS_PROVIDER, type)!!

        // Record analytics only when the view is opened the first time, not restore from state
        if (savedInstanceState == null) {
            Analytics.trackViewStatisticsList()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (statisticsProvider.units.size == 1) {
            fragmentTabs.visibility = View.GONE
            navigationActivity?.supportActionBar?.elevation = resources.getDimension(R.dimen.base_elevation)
        } else {
            fragmentTabs.visibility = View.VISIBLE
            navigationActivity?.supportActionBar?.elevation = 0F
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        navigationActivity?.setToolbarTitle(resources.getString(R.string.statistics), statisticsProvider.name)
    }

    // MARK: TabbedFragment

    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return StatisticsUnitPagerAdapter(childFragmentManager, tabCount, statisticsProvider.units)
    }

    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        statisticsProvider.units.forEach { unit ->
            tabLayout.addTab(tabLayout.newTab().setText(unit.name))
        }
    }

    override fun handleTabSwitch(newTab: Int) {
        // TODO: set the tabs to the same scroll point
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return null
    }

    override fun onFabClick() {
        // Intentionally left blank
    }

    // MARK: StatisticsUnitPagerAdapter

    class StatisticsUnitPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val statisticsUnits: List<StatisticsUnit>
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        override fun buildFragment(position: Int): Fragment? {
            return StatisticsUnitDetailsFragment.newInstance(statisticsUnits[position])
        }
    }
}
