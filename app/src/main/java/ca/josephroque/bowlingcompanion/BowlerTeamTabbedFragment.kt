package ca.josephroque.bowlingcompanion

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerDialog
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamDialog
import ca.josephroque.bowlingcompanion.teams.TeamListFragment
import kotlinx.android.synthetic.main.fragment_common_tabs.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [BowlerListFragment] and [TeamListFragment]
 */
class BowlerTeamTabbedFragment : TabbedFragment(),
        ListFragment.OnListFragmentInteractionListener,
        BowlerDialog.OnBowlerDialogInteractionListener,
        TeamDialog.OnTeamDialogInteractionListener {

    companion object {
        /** Logging identifier */
        private val TAG = "BowlerTeamTabbedFragment"

        /** Index for [BowlerListFragment] tab. */
        const val BOWLER_FRAGMENT = 0

        /** Index for [TeamListFragment] tab. */
        const val TEAM_FRAGMENT = 1
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return BowlerTeamPagerAdapter(childFragmentManager, tabCount)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bowlers))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.teams))
    }

    /** @Override */
    override fun getFabImage(currentTab: Int): Int? {
        return when (currentTab) {
            BOWLER_FRAGMENT -> R.drawable.ic_person_add_white_24dp
            TEAM_FRAGMENT -> R.drawable.ic_group_add_white_24dp
            else -> throw RuntimeException("$currentTab is not a valid tab for BowlerTeamTabbedFragment")
        }
    }

    /** @Override */
    override fun onFabSelected() {
        when (currentTab) {
            BOWLER_FRAGMENT -> promptAddOrEditBowler()
            TEAM_FRAGMENT -> promptAddOrEditTeam()
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        when (item) {
            is Bowler -> {
                if (longPress) {
                    promptAddOrEditBowler(item)
                } else {
                    TODO("Select bowler")
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
        childFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
    }

    /**
     * Display a prompt to add or edit a team.
     *
     * @param team the team to edit, or null if a new team should be added
     */
    private fun promptAddOrEditTeam(team: Team? = null) {
        val newFragment = TeamDialog.newInstance(team)
        childFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
    }

    /** @Override */
    override fun onFinishBowler(bowler: Bowler) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val bowlerFragment = adapter?.getFragment(BOWLER_FRAGMENT) as? BowlerListFragment
        bowlerFragment?.refreshList(bowler)

        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamListFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onDeleteBowler(bowler: Bowler) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val bowlerFragment = adapter?.getFragment(BOWLER_FRAGMENT) as? BowlerListFragment
        bowlerFragment?.onItemDelete(bowler)

        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamListFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onFinishTeam(team: Team) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamListFragment
        teamFragment?.refreshList(team)
    }

    /** @Override */
    override fun onDeleteTeam(team: Team) {
        val adapter = tabbed_fragment_pager.adapter as? BowlerTeamPagerAdapter
        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamListFragment
        teamFragment?.onItemDelete(team)
    }

    /**
     * Pager adapter for bowler and team fragments.
     */
    class BowlerTeamPagerAdapter(
            fragmentManager: FragmentManager,
            tabCount: Int): BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            return when (position) {
                BOWLER_FRAGMENT -> BowlerListFragment.newInstance()
                TEAM_FRAGMENT -> TeamListFragment.newInstance()
                else -> return null
            }
        }
    }

}