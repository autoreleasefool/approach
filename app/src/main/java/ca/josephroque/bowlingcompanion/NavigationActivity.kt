package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.annotation.IdRes
import android.util.Log
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : BaseActivity() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "NavigationActivity"

        /** Cache the default tab to load on open. */
        private const val DEFAULT_TAB = R.id.action_record
    }

    /** @Override. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        setupBottomNavigation()
        onTabSelected(DEFAULT_TAB)
    }

    /**
     * Add listeners to bottom view navigation.
     */
    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            onTabSelected(it.itemId)
            return@setOnNavigationItemSelectedListener true
        }
    }

    /**
     * Handle tab changes.
     *
     * @param tab the new tab to display
     */
    private fun onTabSelected(@IdRes tab: Int) {
        val fragmentName: String
        when (tab) {
            R.id.action_record -> fragmentName = BowlerListFragment::class.java.name
            R.id.action_statistics -> {
                fragmentName = BowlerListFragment::class.java.name
                Log.d(TAG, "Statistics")
            }
            R.id.action_equipment -> {
                fragmentName = BowlerListFragment::class.java.name
                Log.d(TAG, "Equipment")
            }
            else -> throw RuntimeException("Tab has not been registered in NavigationActivity.")
        }

        showFragment(fragmentName)
    }

    /**
     * Create a new instance of the specified fragment and show it.
     *
     * @param fragmentName class name of the fragment to show
     */
    private fun showFragment(fragmentName: String) {
        val fragment = BaseFragment.newInstance(fragmentName)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragmentName)
                .commit()
    }
}
