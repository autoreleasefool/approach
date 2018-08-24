package ca.josephroque.bowlingcompanion.statistics

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
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 */
class StatisticsUnitTabbedFragment : TabbedFragment() {
    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatUnitTabbedFragment"

        /** Argument identifier for passing an array of [StatisticsProvider] type. */
        private const val ARG_STATISTICS_PROVIDER_TYPE = "${TAG}_type"

        /** Argument identifier for passing an array of [StatisticsProvider] to this fragment. */
        private const val ARG_STATISTICS_PROVIDER = "${TAG}_stats"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(statisticsProvider: StatisticsProvider): StatisticsUnitTabbedFragment {
            val newInstance = StatisticsUnitTabbedFragment()
            newInstance.arguments = Bundle().apply {
                putInt(ARG_STATISTICS_PROVIDER_TYPE, statisticsProvider.describeContents())
                putParcelable(ARG_STATISTICS_PROVIDER, statisticsProvider)
            }
            return newInstance
        }
    }

    /** The [StatisticsProvider] whose units' statistics are being displayed. */
    private lateinit var statisticsProvider: StatisticsProvider

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type = arguments?.getInt(ARG_STATISTICS_PROVIDER_TYPE)!!
        statisticsProvider = StatisticsProvider.getParcelable(arguments, ARG_STATISTICS_PROVIDER, type)!!
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun updateToolbarTitle() {
        navigationActivity?.setToolbarTitle(resources.getString(R.string.statistics), statisticsProvider.name)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return StatisticsUnitPagerAdapter(childFragmentManager, tabCount, statisticsProvider.units)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        statisticsProvider.units.forEach { unit ->
            tabLayout.addTab(tabLayout.newTab().setText(unit.name))
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {
        // TODO: handle unit tab switch
    }

    /** @Override */
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

    // MARK: IFloatingActionButtonHandler

    /** @Override */
    override fun getFabImage(): Int? {
        return null
    }

    /** @Override */
    override fun onFabClick() {
        // Intentionally left blank
    }

    // MARK: StatisticsUnitPagerAdapter

    /**
     * Pager adapter for statistic units.
     */
    class StatisticsUnitPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val statisticsUnits: List<StatisticsUnit>
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            return StatisticsListFragment.newInstance(statisticsUnits[position])
        }
    }
}
