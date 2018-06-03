package ca.josephroque.bowlingcompanion

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerDialog
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.list.TeamDialog
import ca.josephroque.bowlingcompanion.teams.list.TeamListFragment
import kotlinx.android.synthetic.main.fragment_common_tabs.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [BowlerListFragment] and [TeamListFragment]
 */
class BowlerTeamTabbedFragment : TabbedFragment(),
        ListFragment.OnListFragmentInteractionListener,
        BowlerDialog.OnBowlerDialogInteractionListener,
        TeamDialog.OnTeamDialogInteractionListener
{

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "BowlerTeamTabFragment"

        /** Tabs available in the fragment. */
        enum class Tab {
            Bowlers, Teams;

            companion object {
                private val map = Tab.values().associateBy(Tab::ordinal)
                fun fromInt(type: Int) = map[type]
            }

            /**
             * Get the title for the tab.
             */
            fun getTitle(): Int {
                return when (this) {
                    Bowlers -> R.string.bowlers
                    Teams -> R.string.teams
                }
            }
        }

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): BowlerTeamTabbedFragment {
            return BowlerTeamTabbedFragment()
        }
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return BowlerTeamPagerAdapter(childFragmentManager, tabCount)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        for (tab in Tab.values()) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.getTitle()))
        }
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return when (Tab.fromInt(currentTab)) {
            Tab.Bowlers -> R.drawable.ic_person_add
            Tab.Teams -> R.drawable.ic_group_add
            else -> throw RuntimeException("$currentTab is not a valid tab for BowlerTeamTabbedFragment")
        }
    }

    /** @Override */
    override fun onFabClick() {
        when (Tab.fromInt(currentTab)) {
            Tab.Bowlers -> promptAddOrEditBowler()
            Tab.Teams -> promptAddOrEditTeam()
            else -> throw RuntimeException("$currentTab is not a valid tab for BowlerTeamTabbedFragment")
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        when (item) {
            is Bowler -> {
                if (longPress) {
                    promptAddOrEditBowler(item)
                } else {
                    showLeaguesAndEvents(item)
                }
            }
            is Team -> {
                if (longPress) {
                    promptAddOrEditTeam(item)
                } else {
                    TODO("Select team")
                }
            }
            else -> throw RuntimeException("BowlerTeamTabbedFragment can only handle Bowler or Team and item is $item")
        }
    }

    /**
     * Display a prompt to add or edit a bowler.
     *
     * @param bowler the bowler to edit, or null if a new bowler should be added
     */
    private fun promptAddOrEditBowler(bowler: Bowler? = null) {
        val newFragment = BowlerDialog.newInstance(bowler)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    /**
     * Display a prompt to add or edit a team.
     *
     * @param team the team to edit, or null if a new team should be added
     */
    private fun promptAddOrEditTeam(team: Team? = null) {
        val newFragment = TeamDialog.newInstance(team)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    /**
     * Push fragment to show leagues and events of a [Bowler]
     *
     * @param bowler the bowler whose leagues and events will be shown
     */
    private fun showLeaguesAndEvents(bowler: Bowler) {
        val newFragment = LeagueEventTabbedFragment.newInstance(bowler)
        fragmentNavigation?.pushFragment(newFragment)
    }

    /** @Override */
    override fun onFinishBowler(bowler: Bowler) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val bowlerFragment = adapter?.getFragment(Tab.Bowlers.ordinal) as? BowlerListFragment
        bowlerFragment?.refreshList(bowler)

        val teamFragment = adapter?.getFragment(Tab.Teams.ordinal) as? TeamListFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onDeleteBowler(bowler: Bowler) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val bowlerFragment = adapter?.getFragment(Tab.Bowlers.ordinal) as? BowlerListFragment
        bowlerFragment?.onItemDelete(bowler)

        val teamFragment = adapter?.getFragment(Tab.Teams.ordinal) as? TeamListFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onFinishTeam(team: Team) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val teamFragment = adapter?.getFragment(Tab.Teams.ordinal) as? TeamListFragment
        teamFragment?.refreshList(team)
    }

    /** @Override */
    override fun onDeleteTeam(team: Team) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val teamFragment = adapter?.getFragment(Tab.Teams.ordinal) as? TeamListFragment
        teamFragment?.onItemDelete(team)
    }

    /**
     * Pager adapter for bowler and team fragments.
     */
    class BowlerTeamPagerAdapter(
            fragmentManager: FragmentManager,
            tabCount: Int
    ): BaseFragmentPagerAdapter(fragmentManager, tabCount) {
        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            return when (Tab.fromInt(position)) {
                Tab.Bowlers -> BowlerListFragment.newInstance()
                Tab.Teams -> TeamListFragment.newInstance()
                else -> null
            }
        }
    }
}
