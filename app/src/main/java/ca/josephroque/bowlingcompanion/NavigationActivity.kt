package ca.josephroque.bowlingcompanion

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import ca.josephroque.bowlingcompanion.bowlers.BowlerListFragment
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import com.ncapdevi.fragnav.FragNavController
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : BaseActivity(),
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        BaseFragment.FragmentNavigation {

    companion object {
        /** Logging identifier. */
        private val TAG = NavigationActivity::class.java.simpleName

        enum class BottomTab {
            Record, Statistics, Equipment;

            companion object {
                private val map = BottomTab.values().associateBy(BottomTab::ordinal)
                fun fromInt(type: Int) = map[type]
                fun fromId(@IdRes id: Int): BottomTab {
                    return when (id) {
                        R.id.action_record -> Record
                        R.id.action_statistics -> Statistics
                        R.id.action_equipment -> Equipment
                        else -> throw RuntimeException("$id is not valid BottomTab id")
                    }
                }
            }
        }
    }

    private var fragNavController: FragNavController? = null

    /** @Override. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        setupToolbar()
        setupBottomNavigation()
        setupFragNavController(savedInstanceState)
    }

    /** @Override */
    override fun onBackPressed() {
        if (fragNavController?.popFragment()?.not() == true) {
            super.onBackPressed()
        }
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragNavController?.onSaveInstanceState(outState!!)
    }

    /** @Override */
    override fun pushFragment(fragment: BaseFragment) {
        fragNavController?.pushFragment(fragment)
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Add listeners to bottom view navigation.
     */
    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            fragNavController?.switchTab(BottomTab.fromId(it.itemId).ordinal)
            return@setOnNavigationItemSelectedListener true
        }

        bottom_navigation.setOnNavigationItemReselectedListener {
            fragNavController?.clearStack()
        }
    }

    /**
     * Build the [FragNavController] for bottom tab navigation.
     *
     * @param savedInstanceState the activity saved instance state
     */
    private fun setupFragNavController(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.fragment_container)
        builder.rootFragmentListener(this, BottomTab.values().size)
    }

    /** @Override */
    override fun getRootFragment(index: Int): Fragment {
        val tab = BottomTab.fromInt(index)
        val fragmentName: String
        fragmentName = when (tab) {
            BottomTab.Record -> BowlerListFragment::class.java.name
            BottomTab.Equipment -> BowlerListFragment::class.java.name // TODO: enable equipment tab
            BottomTab.Statistics -> BowlerListFragment::class.java.name // TODO: enable statistics tab
            else -> throw RuntimeException("$index is not a valid tab index")
        }

        return BaseFragment.newInstance(fragmentName)
    }

    /** @Override */
    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController?.isRootFragment?.not() ?: false)
    }

    /** @Override */
    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController?.isRootFragment?.not() ?: false)
    }
}
