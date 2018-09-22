package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.leagues.LeagueDialog
import ca.josephroque.bowlingcompanion.leagues.LeagueListFragment
import ca.josephroque.bowlingcompanion.series.SeriesListFragment
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [LeagueListFragment] showing leagues, and a
 * [LeagueListFragment] showing events.
 */
class LeagueEventTabbedFragment : TabbedFragment(),
        ListFragment.ListFragmentDelegate,
        LeagueDialog.LeagueDialogDelegate,
        IStatisticsContext {

    companion object {
        @Suppress("unused")
        private const val TAG = "LeagueEventTabFragment"

        private const val ARG_BOWLER = "${TAG}_bowler"

        enum class Tab {
            Leagues, Events;

            companion object {
                private val map = Tab.values().associateBy(Tab::ordinal)
                fun fromInt(type: Int) = map[type]
            }

            fun getTitle(): Int {
                return when (this) {
                    Leagues -> R.string.leagues
                    Events -> R.string.events
                }
            }
        }

        fun newInstance(bowler: Bowler): LeagueEventTabbedFragment {
            val fragment = LeagueEventTabbedFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_BOWLER, bowler) }
            return fragment
        }
    }

    override val statisticsProviders: List<StatisticsProvider> by lazy {
        val bowler = bowler
        return@lazy if (bowler != null) {
            arrayListOf<StatisticsProvider>(StatisticsProvider.BowlerStatistics(bowler))
        } else {
            emptyList<StatisticsProvider>()
        }
    }

    private var bowler: Bowler? = null

    // MARK: LeagueEventTabbedFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bowler = arguments?.getParcelable(ARG_BOWLER)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        bowler?.let { navigationActivity?.setToolbarTitle(it.name) }
    }

    // MARK: TabbedFragment

    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return LeagueEventPagerAdapter(childFragmentManager, tabCount, bowler)
    }

    override fun addTabs(tabLayout: TabLayout) {
        for (tab in Tab.values()) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.getTitle()))
        }
    }

    override fun handleTabSwitch(newTab: Int) {}

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return R.drawable.ic_add
    }

    override fun onFabClick() {
        when (Tab.fromInt(currentTab)) {
            Tab.Leagues -> promptAddOrEditLeague(false)
            Tab.Events -> promptAddOrEditLeague(true)
            else -> throw RuntimeException("$currentTab is not a valid tab for LeagueEventTabbedFragment")
        }
    }

    // MARK: ListFragmentDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is League) {
            if (longPress) {
                promptAddOrEditLeague(item.isEvent, item)
            } else {
                showSeries(item)
                Analytics.trackSelectLeague(item.isPractice, item.isEvent)
            }
        } else {
            throw RuntimeException("LeagueEventTabbedFragment can only handle League and item is $item")
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        // Intentionally left blank
    }

    // MARK: LeagueDialogDelegate

    override fun onFinishLeague(league: League) {
        val adapter = fragmentPager.adapter as? LeagueEventPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(Tab.Events.ordinal) as? LeagueListFragment
        } else {
            adapter?.getFragment(Tab.Leagues.ordinal) as? LeagueListFragment
        }
        leagueFragment?.refreshList(league)
    }

    override fun onDeleteLeague(league: League) {
        val adapter = fragmentPager.adapter as? LeagueEventPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(Tab.Events.ordinal) as? LeagueListFragment
        } else {
            adapter?.getFragment(Tab.Leagues.ordinal) as? LeagueListFragment
        }
        leagueFragment?.onItemDelete(league)
    }

    // MARK: Private functions

    private fun promptAddOrEditLeague(isEvent: Boolean, league: League? = null) {
        val bowler = bowler ?: return
        val newFragment = LeagueDialog.newInstance(bowler, league, isEvent)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    private fun showSeries(league: League) {
        val newFragment = SeriesListFragment.newInstance(league)
        fragmentNavigation?.pushFragment(newFragment)
    }

    // MARK: LeagueEventPagerAdapter

    class LeagueEventPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val bowler: Bowler?
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {
        override fun buildFragment(position: Int): Fragment? {
            bowler?.let {
                return when (Tab.fromInt(position)) {
                    Tab.Leagues -> LeagueListFragment.newInstance(bowler, LeagueListFragment.Companion.Show.Leagues)
                    Tab.Events -> LeagueListFragment.newInstance(bowler, LeagueListFragment.Companion.Show.Events)
                    else -> null
                }
            }

            return null
        }
    }
}
