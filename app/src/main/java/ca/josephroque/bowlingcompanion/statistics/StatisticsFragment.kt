package ca.josephroque.bowlingcompanion.statistics

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display the user's statistics.
 */
class StatisticsFragment : TabbedFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatisticsFragment"

        /** Argument identifier for passing a [StatisticsProvider] type. */
        private const val ARG_STATISTICS_PROVIDER_TYPE = "${TAG}_type"

        /** Argument identifier for passing a [StatisticsProvider] to this fragment. */
        private const val ARG_STATISTICS_PROVIDER = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param statisticsProvider the series to edit games for
         * @return the new instance
         */
        fun newInstance(statisticsProvider: StatisticsProvider): StatisticsFragment {
            val fragment = StatisticsFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_STATISTICS_PROVIDER_TYPE, statisticsProvider.describeContents())
                putParcelable(ARG_STATISTICS_PROVIDER, statisticsProvider)
            }
            return fragment
        }
    }

    /** The stats being displayed. */
    private var statisticsProvider: StatisticsProvider? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val statisticsType = arguments?.getInt(ARG_STATISTICS_PROVIDER_TYPE) ?: 0
        statisticsProvider = StatisticsProvider.getParcelable(arguments, ARG_STATISTICS_PROVIDER, statisticsType)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return StatisticsListPagerAdapter(childFragmentManager, tabCount, statisticsProvider?.units )
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        statisticsProvider?.units?.let {
            it.forEach { unit ->
                tabLayout.addTab(tabLayout.newTab().setText(unit.name))
            }
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {

    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        val unitList = statisticsProvider?.units ?: return
        if (unitList.size == 1) {
            fragmentTabs.visibility = View.GONE
            (activity as? AppCompatActivity)?.supportActionBar?.elevation = resources.getDimension(R.dimen.base_elevation)
        } else {
            fragmentTabs.visibility = View.VISIBLE
            (activity as? AppCompatActivity)?.supportActionBar?.elevation = 0F
        }
    }

    // MARK: IFloatingActionButtonHandler

    /** @Override */
    override fun getFabImage(): Int? {
        return null
    }

    /** @Override */
    override fun onFabClick() {

    }

    /**
     * Pager adapter for games.
     */
    class StatisticsListPagerAdapter(
            fragmentManager: FragmentManager,
            tabCount: Int,
            private val statisticsUnits: List<StatisticsUnit>?
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            statisticsUnits?.let {
                return StatisticsListFragment.newInstance(statisticsUnits[position])
            }

            return null
        }
    }

}
