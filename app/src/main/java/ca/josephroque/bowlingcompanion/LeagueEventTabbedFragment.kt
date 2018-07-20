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
import kotlinx.android.synthetic.main.fragment_common_tabs.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [LeagueListFragment] showing leagues, and a
 * [LeagueListFragment] showing events.
 */
class LeagueEventTabbedFragment : TabbedFragment(),
        ListFragment.OnListFragmentInteractionListener,
        LeagueDialog.OnLeagueDialogInteractionListener {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "LeagueEventTabFragment"

        /** Argument identifier for passing a [Bowler] to this fragment. */
        private const val ARG_BOWLER = "${TAG}_bowler"

        /** Tabs available in the fragment. */
        enum class Tab {
            Leagues, Events;

            companion object {
                private val map = Tab.values().associateBy(Tab::ordinal)
                fun fromInt(type: Int) = map[type]
            }

            /**
             * Get the title for the tab.
             */
            fun getTitle(): Int {
                return when (this) {
                    Leagues -> R.string.leagues
                    Events -> R.string.events
                }
            }
        }

        /**
         * Create a new instance.
         *
         * @param bowler the bowler whose leagues and events will be shown.
         * @return the new instance
         */
        fun newInstance(bowler: Bowler): LeagueEventTabbedFragment {
            val fragment = LeagueEventTabbedFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_BOWLER, bowler) }
            return fragment
        }
    }

    /** The user's selected bowler. */
    private var bowler: Bowler? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bowler = savedInstanceState?.getParcelable(ARG_BOWLER) ?: arguments?.getParcelable(ARG_BOWLER)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return LeagueEventPagerAdapter(childFragmentManager, tabCount, bowler)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        for (tab in Tab.values()) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.getTitle()))
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {}

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_add
    }

    /** @Override */
    override fun onFabClick() {
        when (Tab.fromInt(currentTab)) {
            Tab.Leagues -> promptAddOrEditLeague(false)
            Tab.Events -> promptAddOrEditLeague(true)
            else -> throw RuntimeException("$currentTab is not a valid tab for LeagueEventTabbedFragment")
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is League) {
            if (longPress) {
                promptAddOrEditLeague(item.isEvent, item)
            } else {
                showSeries(item)
            }
        } else {
            throw RuntimeException("LeagueEventTabbedFragment can only handle League and item is $item")
        }
    }

    /** @Override */
    override fun onFinishLeague(league: League) {
        val adapter = tabbed_fragment_pager.adapter as? LeagueEventPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(Tab.Events.ordinal) as? LeagueListFragment
        } else {
            adapter?.getFragment(Tab.Leagues.ordinal) as? LeagueListFragment
        }
        leagueFragment?.refreshList(league)
    }

    /** @Override */
    override fun onDeleteLeague(league: League) {
        val adapter = tabbed_fragment_pager.adapter as? LeagueEventPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(Tab.Events.ordinal) as? LeagueListFragment
        } else {
            adapter?.getFragment(Tab.Leagues.ordinal) as? LeagueListFragment
        }
        leagueFragment?.onItemDelete(league)
    }

    /**
     * Display a prompt to add or edit a league.
     *
     * @param isEvent true to add an event, false to add a league
     * @param league the league to edit, or null if a new league should be added
     */
    private fun promptAddOrEditLeague(isEvent: Boolean, league: League? = null) {
        val bowler = bowler ?: return
        val newFragment = LeagueDialog.newInstance(bowler, league, isEvent)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    /**
     * Show list of series for the league.
     *
     * @param league league to show series for
     */
    private fun showSeries(league: League) {
        val newFragment = SeriesListFragment.newInstance(league)
        fragmentNavigation?.pushFragment(newFragment)
    }

    /**
     * Pager adapter for league and event fragments.
     */
    class LeagueEventPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val bowler: Bowler?
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
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
