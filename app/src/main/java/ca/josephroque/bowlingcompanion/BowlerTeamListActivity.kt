package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerFragment
import ca.josephroque.bowlingcompanion.bowlers.NewBowlerDialog
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamFragment
import ca.josephroque.bowlingcompanion.utils.Android
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch


class BowlerTeamListActivity : AppCompatActivity(),
        BowlerFragment.OnBowlerFragmentInteractionListener,
        TeamFragment.OnTeamFragmentInteractionListener,
        NewBowlerDialog.OnNewBowlerInteractionListener {

    companion object {
        /** Logging identifier. */
        private val TAG = "BowlerTeamListActivity"
        /** References primary Fab. */
        val PRIMARY = 0
        /** References secondary Fab. */
        val SECONDARY = 1
    }

    /** Active tab. */
    private val currentTab: Int
        get() = pager_main.currentItem

    /** Handle visibility changes in the fab. */
    val fabVisibilityChangedListener = object : OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            super.onHidden(fab)

            when (currentTab) {
                0 -> fab_primary.setImageResource(R.drawable.ic_person_add_black_24dp)
                1 -> fab_primary.setImageResource(R.drawable.ic_group_add_black_24dp)
            }

            fab_primary.show()
        }
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureTabLayout()
        configureFab()
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure tab layout for rendering.
     */
    private fun configureTabLayout() {
        tabs_main.addTab(tabs_main.newTab().setText(R.string.bowlers))
        tabs_main.addTab(tabs_main.newTab().setText(R.string.teams))

        val adapter = TabPagerAdapter(supportFragmentManager, tabs_main.tabCount)
        pager_main.adapter = adapter

        pager_main.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_main))
        tabs_main.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager_main.currentItem = tab.position

                if (fab_primary.visibility == View.VISIBLE) {
                    fab_primary.hide(fabVisibilityChangedListener)
                } else {
                    fabVisibilityChangedListener.onHidden(fab_primary)
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
        fab_primary.setImageResource(R.drawable.ic_person_add_black_24dp)
        fab_secondary.visibility = View.GONE

        fab_primary.setOnClickListener {
            when (currentTab) {
                0 -> promptNewBowler()
                1 -> TODO("not implemented")
            }
        }
    }

    /**
     * Hide the specified floating action button.
     *
     * @param Int the fab to hide
     */
    fun hideFab(which: Int) {
        when (which) {
            PRIMARY -> fab_primary.hide()
            SECONDARY -> fab_secondary.hide()
        }
    }

    /**
     * Show the specified floating action button.
     *
     * @param Int the fab to show
     */
    fun showFab(which: Int) {
        when (which) {
            PRIMARY -> fab_primary.show()
            SECONDARY -> fab_secondary.show()
        }
    }

    /** @Override */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /** @Override */
    override fun onBackPressed() {
        super.onBackPressed()
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** @Override */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_settings)?.isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_settings -> {
//                openSettings()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Display a prompt to add a new bowler.
     */
    fun promptNewBowler() {
        val newFragment = NewBowlerDialog()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
    }

    /**
     * Callback to create a new [Bowler].
     *
     * @param name name of the new [Bowler]
     */
    override fun onCreateBowler(name: String) {
        launch(Android) {
            Bowler.createNewAndSave(this@BowlerTeamListActivity, name).await()
            // TODO: reload the bowler fragment
        }
    }

    /** @Override. */
    override fun onTeamSelected(team: Team) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override. */
    override fun onBowlerSelected(bowler: Bowler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Manages pages for each tab.
     */
    internal class TabPagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {

        /** @Override. */
        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return BowlerFragment.newInstance()
                1 -> return TeamFragment.newInstance()
                else -> return null
            }
        }

        /** @Override. */
        override fun getCount(): Int {
            return tabCount
        }
    }
}
