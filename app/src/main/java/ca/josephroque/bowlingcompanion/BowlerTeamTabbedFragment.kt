package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerDialog
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.details.TeamDetailsFragment
import ca.josephroque.bowlingcompanion.teams.list.TeamDialog
import ca.josephroque.bowlingcompanion.teams.list.TeamListFragment
import ca.josephroque.bowlingcompanion.transfer.BaseTransferDialogFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment with tabs to switch between a [BowlerListFragment] and [TeamListFragment]
 */
class BowlerTeamTabbedFragment : TabbedFragment(),
        ListFragment.ListFragmentDelegate,
        BowlerDialog.BowlerDialogDelegate,
        TeamDialog.TeamDialogDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "BowlerTeamTabFragment"

        enum class Tab {
            Bowlers, Teams;

            companion object {
                private val map = Tab.values().associateBy(Tab::ordinal)
                fun fromInt(type: Int) = map[type]
            }

            val title: Int
                get() = when (this) {
                    Bowlers -> R.string.bowlers
                    Teams -> R.string.teams
                }
        }

        fun newInstance(): BowlerTeamTabbedFragment {
            return BowlerTeamTabbedFragment()
        }
    }

    private val teamFragment: TeamListFragment?
        get() = findFragmentByPosition(Tab.Teams.ordinal) as? TeamListFragment

    private val bowlerFragment: BowlerListFragment?
        get() = findFragmentByPosition(Tab.Bowlers.ordinal) as? BowlerListFragment

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_bowlers_teams, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_transfer -> {
                showTransferFragment()
                Analytics.trackViewTransferMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        navigationActivity?.setToolbarTitle(resources.getString(R.string.app_name))
    }

    // MARK: TabbedFragment

    override fun buildPagerAdapter(tabCount: Int): FragmentPagerAdapter {
        return BowlerTeamPagerAdapter(childFragmentManager, tabCount)
    }

    override fun addTabs(tabLayout: TabLayout) {
        for (tab in Tab.values()) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.title))
        }
    }

    override fun handleTabSwitch(newTab: Int) {}

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return when (Tab.fromInt(currentTab)) {
            Tab.Bowlers -> R.drawable.ic_person_add
            Tab.Teams -> R.drawable.ic_group_add
            else -> throw RuntimeException("$currentTab is not a valid tab for BowlerTeamTabbedFragment")
        }
    }

    override fun onFabClick() {
        when (Tab.fromInt(currentTab)) {
            Tab.Bowlers -> promptAddOrEditBowler()
            Tab.Teams -> promptAddOrEditTeam()
            else -> throw RuntimeException("$currentTab is not a valid tab for BowlerTeamTabbedFragment")
        }
    }

    // MARK: ListFragmentDelegate

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
                    showTeamDetails(item)
                }
            }
            else -> throw RuntimeException("BowlerTeamTabbedFragment can only handle Bowler or Team and item is $item")
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        when (item) {
            is Bowler -> {
                teamFragment?.refresh()
                Analytics.trackDeleteBowler()
            }
            is Team -> {
                bowlerFragment?.refresh()
                Analytics.trackDeleteTeam()
            }
            else -> throw RuntimeException("BowlerTeamTabbedFragment can only handle Bowler or Team and item is $item")
        }
    }

    // MARK: Private functions

    private fun showTransferFragment() {
        val newFragment = BaseTransferDialogFragment.newInstance()
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    private fun promptAddOrEditBowler(bowler: Bowler? = null) {
        val newFragment = BowlerDialog.newInstance(bowler)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    private fun promptAddOrEditTeam(team: Team? = null) {
        val newFragment = TeamDialog.newInstance(team)
        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    private fun showLeaguesAndEvents(bowler: Bowler) {
        val newFragment = LeagueEventTabbedFragment.newInstance(bowler)
        fragmentNavigation?.pushFragment(newFragment)

        Analytics.trackSelectBowler()
    }

    private fun showTeamDetails(team: Team) {
        val newFragment = TeamDetailsFragment.newInstance(team)
        fragmentNavigation?.pushFragment(newFragment)

        Analytics.trackSelectTeam()
    }

    // MARK: BowlerDialogDelegate

    override fun onFinishBowler(bowler: Bowler) {
        val bowlerFragment = bowlerFragment
        bowlerFragment?.refreshList(bowler)
        teamFragment?.refreshList()
    }

    override fun onDeleteBowler(bowler: Bowler) {
        bowlerFragment?.onItemDelete(bowler)
        teamFragment?.refreshList()
    }

    // MARK: TeamDialogDelegate

    override fun onFinishTeam(team: Team) {
        teamFragment?.refreshList(team)
    }

    override fun onDeleteTeam(team: Team) {
        teamFragment?.onItemDelete(team)
    }

    // MARK: BowlerTeamPagerAdapter

    class BowlerTeamPagerAdapter(
        fragmentManager: FragmentManager,
        private val tabCount: Int
    ) : FragmentPagerAdapter(fragmentManager) {
        override fun getCount() = tabCount

        override fun getItem(position: Int): Fragment? {
            return when (Tab.fromInt(position)) {
                Tab.Bowlers -> BowlerListFragment.newInstance()
                Tab.Teams -> TeamListFragment.newInstance()
                else -> null
            }
        }
    }
}
