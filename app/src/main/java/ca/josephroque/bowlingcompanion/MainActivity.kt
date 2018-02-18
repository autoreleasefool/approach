package ca.josephroque.bowlingcompanion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.bowlers.BowlerFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        BowlerFragment.OnBowlerFragmentInteractionListener,
        TeamFragment.OnTeamFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureTabLayout()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
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
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
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
