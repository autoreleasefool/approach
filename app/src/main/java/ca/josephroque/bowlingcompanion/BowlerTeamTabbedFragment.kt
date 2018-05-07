package ca.josephroque.bowlingcompanion

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.teams.TeamListFragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [BowlerListFragment] and [TeamListFragment]
 */
class BowlerTeamTabbedFragment : TabbedFragment() {

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
        TODO("not implemented")
//        when (currentTab) {
//            BOWLER_FRAGMENT -> promptAddOrEditBowler()
//            TEAM_FRAGMENT -> promptAddOrEditTeam()
//        }
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