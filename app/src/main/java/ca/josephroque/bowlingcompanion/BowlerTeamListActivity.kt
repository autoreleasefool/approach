package ca.josephroque.bowlingcompanion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.FloatingActionButton.OnVisibilityChangedListener
import android.view.View
import android.view.Menu
import android.view.MenuItem
import ca.josephroque.bowlingcompanion.utils.Android
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity(),
        BowlerFragment.OnBowlerFragmentInteractionListener,
        TeamFragment.OnTeamFragmentInteractionListener {

    companion object {
        /** Logging identifier. */
        private val TAG = "MainActivity"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureTabLayout()
        configureFab()
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

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

    fun hideFab(which: Int) {
        when (which) {
            PRIMARY -> fab_primary.hide()
            SECONDARY -> fab_secondary.hide()
        }
    }

    fun showFab(which: Int) {
        when (which) {
            PRIMARY -> fab_primary.show()
            SECONDARY -> fab_secondary.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_settings).isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_settings -> {
//                openSettings()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun promptNewBowler() {
        launch(Android) {
            Bowler.createNewAndSave(this@MainActivity, name).await()

        }
    }

    override fun onTeamSelected(team: Team) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBowlerSelected(bowler: Bowler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal class TabPagerAdapter(fm: FragmentManager, private var tabCount: Int): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return BowlerFragment.newInstance()
                1 -> return TeamFragment.newInstance()
                else -> return null
            }
        }

        override fun getCount(): Int {
            return tabCount
        }
    }
}
