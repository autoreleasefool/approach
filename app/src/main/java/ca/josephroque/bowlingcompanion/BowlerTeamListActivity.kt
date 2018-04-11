package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerFragment
import ca.josephroque.bowlingcompanion.bowlers.BowlerDialog
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamFragment
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Email
import kotlinx.android.synthetic.main.activity_bowler_team_list.*
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference
import ca.josephroque.bowlingcompanion.settings.SettingsActivity
import android.content.Intent
import android.graphics.Color
import ca.josephroque.bowlingcompanion.BowlerTeamListActivity.BowlersTeamsPagerAdapter.Companion.BOWLER_FRAGMENT
import ca.josephroque.bowlingcompanion.BowlerTeamListActivity.BowlersTeamsPagerAdapter.Companion.TEAM_FRAGMENT
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.teams.TeamDialog

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display bowler and team fragments.
 */
class BowlerTeamListActivity : BaseActivity(),
        BowlerFragment.OnBowlerFragmentInteractionListener,
        TeamFragment.OnTeamFragmentInteractionListener,
        BowlerDialog.OnBowlerDialogInteractionListener,
        TeamDialog.OnTeamDialogInteractionListener {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BowlerTeamListActivity"
    }

    /** Active tab. */
    private val currentTab: Int
        get() = pager_bowlers_teams.currentItem

    /** Handle visibility changes in the fab. */
    val fabVisibilityChangedListener = object : OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.let {
                it.setColorFilter(Color.BLACK)
                when (currentTab) {
                    BOWLER_FRAGMENT -> it.setImageResource(R.drawable.ic_person_add_white_24dp)
                    TEAM_FRAGMENT -> it.setImageResource(R.drawable.ic_group_add_white_24dp)
                }

                it.show()
            }
        }
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bowler_team_list)

        configureToolbar()
        configureTabLayout()
        configureFab()
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar_bowlers_teams)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure tab layout for rendering.
     */
    private fun configureTabLayout() {
        tabs_bowlers_teams.addTab(tabs_bowlers_teams.newTab().setText(R.string.bowlers))
        tabs_bowlers_teams.addTab(tabs_bowlers_teams.newTab().setText(R.string.teams))
        pager_bowlers_teams.scrollingEnabled = false

        val adapter = BowlersTeamsPagerAdapter(supportFragmentManager, tabs_bowlers_teams.tabCount)
        pager_bowlers_teams.adapter = adapter

        pager_bowlers_teams.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_bowlers_teams))
        tabs_bowlers_teams.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager_bowlers_teams.currentItem = tab.position

                if (fab.visibility == View.VISIBLE) {
                    fab.hide(fabVisibilityChangedListener)
                } else {
                    fabVisibilityChangedListener.onHidden(fab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Configure floating action buttons for rendering.
     */
    private fun configureFab() {
        fab?.setColorFilter(Color.BLACK)
        fab.setImageResource(R.drawable.ic_person_add_white_24dp)

        fab.setOnClickListener {
            when (currentTab) {
                BOWLER_FRAGMENT -> promptAddOrEditBowler()
                TEAM_FRAGMENT -> promptAddOrEditTeam()
            }
        }
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_activity_bowlers_teams, menu)
        return true
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_transfer -> {
                TODO("initiate transfer")
            }
            R.id.action_settings -> {
                openSettings()
                true
            }
            R.id.action_feedback -> {
                Email.sendEmail(
                        this,
                        resources.getString(R.string.feedback_email_recipient),
                        String.format(resources.getString(R.string.feedback_email_subject), BuildConfig.VERSION_CODE),
                        null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Refresh lists in all tabs.
     */
    fun refreshTabs() {
        val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
        adapter?.let {
            for (i in 0 until it.count) {
                val fragment = it.getFragment(i) as? ListFragment<*,*,*>
                fragment?.refreshList()
            }
        }
    }

    /**
     * Display a prompt to add or edit a bowler.
     *
     * @param bowler the bowler to edit, or null if a new bowler should be added
     */
    private fun promptAddOrEditBowler(bowler: Bowler? = null) {
        val newFragment = BowlerDialog.newInstance(bowler)
        supportFragmentManager.beginTransaction()
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
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
    }

    /** @Override */
    override fun onFinishBowler(bowler: Bowler) {
        val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
        val bowlerFragment = adapter?.getFragment(BOWLER_FRAGMENT) as? BowlerFragment
        bowlerFragment?.refreshList(bowler)

        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onDeleteBowler(bowler: Bowler) {
        val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
        val bowlerFragment = adapter?.getFragment(BOWLER_FRAGMENT) as? BowlerFragment
        bowlerFragment?.onItemDelete(bowler)

        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamFragment
        teamFragment?.refreshList()
    }

    /** @Override */
    override fun onFinishTeam(team: Team) {
        val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamFragment
        teamFragment?.refreshList(team)
    }

    /** @Override */
    override fun onDeleteTeam(team: Team) {
        val adapter = pager_bowlers_teams.adapter as? BowlersTeamsPagerAdapter
        val teamFragment = adapter?.getFragment(TEAM_FRAGMENT) as? TeamFragment
        teamFragment?.onItemDelete(team)
    }

    /** @Override. */
    override fun onTeamSelected(team: Team, toEdit: Boolean) {
        if (toEdit) {
            promptAddOrEditTeam(team)
        } else {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    /** @Override. */
    override fun onBowlerSelected(bowler: Bowler, toEdit: Boolean) {
        if (toEdit) {
            promptAddOrEditBowler(bowler)
        } else {
            val intent = Intent(baseContext, BowlerDetailsActivity::class.java).apply {
                putExtra(BowlerDetailsActivity.INTENT_BOWLER, bowler)
            }
            startActivity(intent)
        }
    }

    /**
     * Manages pages for each tab.
     */
    internal class BowlersTeamsPagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {

        companion object {
            /** Index for [BowlerFragment]. */
            const val BOWLER_FRAGMENT = 0
            /** Index for [TeamFragment]. */
            const val TEAM_FRAGMENT = 1
        }

        /** Weak references to the fragments in the pager. */
        private val fragmentReferenceMap: MutableMap<Int, WeakReference<Fragment>> = HashMap()

        /** @Override. */
        override fun getItem(position: Int): Fragment? {
            val fragment: Fragment = when (position) {
                BOWLER_FRAGMENT -> BowlerFragment.newInstance()
                TEAM_FRAGMENT -> TeamFragment.newInstance()
                else -> return null
            }

            fragmentReferenceMap[position] = WeakReference(fragment)
            return fragment
        }

        /** @Override. */
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            fragmentReferenceMap.remove(position)
        }

        /** @Override. */
        override fun getCount(): Int {
            return tabCount
        }

        /**
         * Get a reference to a fragment in the pager.
         *
         * @param position the fragment to get
         * @return the fragment at [position]
         */
        fun getFragment(position: Int): Fragment? {
            return fragmentReferenceMap[position]?.get()
        }
    }
}
