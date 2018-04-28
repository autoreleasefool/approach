package ca.josephroque.bowlingcompanion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.LeagueEventListActivity.LeaguesEventsPagerAdapter.Companion.EVENT_FRAGMENT
import ca.josephroque.bowlingcompanion.LeagueEventListActivity.LeaguesEventsPagerAdapter.Companion.LEAGUE_FRAGMENT
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.leagues.LeagueDialog
import ca.josephroque.bowlingcompanion.leagues.LeagueListFragment
import kotlinx.android.synthetic.main.activity_league_event_list.*
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display bowler details.
 */
class LeagueEventListActivity : BaseActivity(),
        LeagueListFragment.OnLeagueListFragmentInteractionListener,
        LeagueDialog.OnLeagueDialogInteractionListener
{

    companion object {
        /** Logging identifier. */
        private const val TAG = "LeagueEventListActivity"

        /** Intent argument for passing a [Bowler] to this activity. */
        const val INTENT_BOWLER = "${TAG}_bowler"
    }

    /** The user's selected bowler. */
    private var bowler: Bowler? = null

    /** Active tab. */
    private val currentTab: Int
        get() = pager_leagues_events.currentItem

    /** Handle visibility changes in the fab. */
    val fabVisibilityChangedListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.show()
        }
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_event_list)

        bowler = intent.getParcelableExtra(INTENT_BOWLER)

        configureToolbar()
        configureTabLayout()
        configureFab()
    }

    /** @Override */
    override fun onResume() {
        super.onResume()

        bowler?.let {
            supportActionBar?.title = it.name
        }
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar_bowler_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure tab layout for rendering.
     */
    private fun configureTabLayout() {
        val bowler = bowler ?: return

        tabs_leagues_events.addTab(tabs_leagues_events.newTab().setText(R.string.leagues))
        tabs_leagues_events.addTab(tabs_leagues_events.newTab().setText(R.string.events))
        pager_leagues_events.scrollingEnabled = false

        val adapter = LeagueEventListActivity.LeaguesEventsPagerAdapter(supportFragmentManager, tabs_leagues_events.tabCount, bowler)
        pager_leagues_events.adapter = adapter

        pager_leagues_events.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_leagues_events))
        tabs_leagues_events.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager_leagues_events.currentItem = tab.position

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
        fab.setImageResource(R.drawable.ic_add_white_24dp)

        fab.setOnClickListener {
            when (currentTab) {
                LEAGUE_FRAGMENT -> promptAddOrEditLeague(false)
                EVENT_FRAGMENT -> promptAddOrEditLeague(true)
            }
        }
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_activity_leagues_events, menu)
        return true
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
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
    }

    /** @Override */
    override fun onLeagueSelected(league: League, toEdit: Boolean) {
        if (toEdit) {
            promptAddOrEditLeague(league.isEvent, league)
        } else {
            val intent = Intent(baseContext, SeriesListActivity::class.java).apply {
                putExtra(SeriesListActivity.INTENT_LEAGUE, league)
            }
            startActivity(intent)
        }
    }

    /** @Override */
    override fun onFinishLeague(league: League) {
        val adapter = pager_leagues_events.adapter as? LeaguesEventsPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(EVENT_FRAGMENT) as? LeagueListFragment
        } else {
            adapter?.getFragment(LEAGUE_FRAGMENT) as? LeagueListFragment
        }
        leagueFragment?.refreshList(league)
    }

    /** @Override */
    override fun onDeleteLeague(league: League) {
        val adapter = pager_leagues_events.adapter as? LeaguesEventsPagerAdapter
        val leagueFragment = if (league.isEvent) {
            adapter?.getFragment(LEAGUE_FRAGMENT) as? LeagueListFragment
        } else {
            adapter?.getFragment(EVENT_FRAGMENT) as? LeagueListFragment
        }
        leagueFragment?.onItemDelete(league)
    }

    /**
     * Manages pages for each tab.
     */
    internal class LeaguesEventsPagerAdapter(
            fm: FragmentManager,
            private var tabCount: Int,
            private var bowler: Bowler
    ): FragmentPagerAdapter(fm) {

        companion object {
            /** Index for [LeagueListFragment]. */
            const val LEAGUE_FRAGMENT = 0
            /** Index for [LeagueListFragment] showing events. */
            const val EVENT_FRAGMENT = 1
        }

        /** Weak references to the fragments in the pager. */
        private val fragmentReferenceMap: MutableMap<Int, WeakReference<Fragment>> = HashMap()

        /** @Override. */
        override fun getItem(position: Int): Fragment? {
            val fragment: Fragment = when (position) {
                LEAGUE_FRAGMENT -> LeagueListFragment.newInstance(bowler, false)
                EVENT_FRAGMENT -> LeagueListFragment.newInstance(bowler, true)
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
