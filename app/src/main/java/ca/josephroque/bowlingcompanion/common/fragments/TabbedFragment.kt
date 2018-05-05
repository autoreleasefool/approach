package ca.josephroque.bowlingcompanion.common.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.IRefreshable
import kotlinx.android.synthetic.main.fragment_common_tabs.*
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 */
abstract class TabbedFragment : BaseFragment() {

    companion object {
        /** Logging identifier. */
        private val TAG = TabbedFragment::class.java.simpleName
    }

    /** Active tab. */
    protected val currentTab: Int
        get() = tabbed_fragment_pager.currentItem

    /** Handle visibility changes in the fab. */
    val fabVisibilityChangeListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.let {
                it.setColorFilter(Color.BLACK)
                val image = getFabImage(currentTab) ?: return
                it.setImageResource(image)
                it.show()
            }
        }
    }

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_tabs, container, false)
        configureToolbar()
        configureTabLayout()
        configureFab()
        return rootView
    }

    /**
     * Get the image to display on the Fab for a certain tab.
     *
     * @param currentTab the tab to be displayed
     * @return an image resource id, or null to hide the fab
     * */
    abstract fun getFabImage(currentTab: Int): Int?

    /**
     * Add tabs to the tab layout.
     *
     * @param tabLayout to create and add tabs
     */
    abstract fun addTabs(tabLayout: TabLayout)

    /**
     * Create an instance of [BaseFragmentPagerAdapter] to manage the fragment instances for each tab.
     *
     * @param tabCount number of tabs
     */
    abstract fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter

    /**
     * Invoked when the floating action button is selected, so subclasses may handle.
     */
    abstract fun onFabSelected()

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        // TODO: configure toolbar for tabbed fragment
    }

    /**
     * Configure tab layout for rendering.
     */
    private fun configureTabLayout() {
        addTabs(tabbed_fragment_tabs)
        tabbed_fragment_pager.scrollingEnabled = false

        val adapter = buildPagerAdapter(tabbed_fragment_tabs.tabCount)
        tabbed_fragment_pager.adapter = adapter

        tabbed_fragment_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabbed_fragment_tabs))
        tabbed_fragment_tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabbed_fragment_pager.currentItem = tab.position

                if (tabbed_fragment_fab.visibility == View.VISIBLE) {
                    tabbed_fragment_fab.hide(fabVisibilityChangeListener)
                } else {
                    fabVisibilityChangeListener.onHidden(tabbed_fragment_fab)
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
        val image = getFabImage(currentTab) ?: return
        tabbed_fragment_fab.setColorFilter(Color.BLACK)
        tabbed_fragment_fab.setImageResource(image)
        tabbed_fragment_fab.setOnClickListener {
            onFabSelected()
        }
    }


    /**
     * Refresh lists in all tabs.
     *
     * @param ignored fragment tags to ignore
     */
    fun refreshTabs(ignored: Set<String> = HashSet()) {
        // TODO: figure out how to use ignored
        val adapter = tabbed_fragment_pager.adapter as? BaseFragmentPagerAdapter
        adapter?.let {
            for (i in 0 until it.count) {
//                if (ignored.contains(i)) {
//                    continue
//                }

                val fragment = it.getFragment(i) as? IRefreshable
                fragment?.refresh()
            }
        }
    }

    abstract class BaseFragmentPagerAdapter(
            fragmentManager: FragmentManager,
            private val tabCount: Int): FragmentPagerAdapter(fragmentManager) {

        /** Weak references to the fragments in the pager. */
        private val fragmentReferenceMap: MutableMap<Int, WeakReference<Fragment>> = HashMap(tabCount)

        /** @Override */
        final override fun getItem(position: Int): Fragment? {
            val fragment = buildFragment(position) ?: return null
            fragmentReferenceMap[position] = WeakReference(fragment)
            return fragment
        }

        /** @Override. */
        final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
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

        /**
         * Build the fragment for the position in the pager.
         *
         * @param position position of fragment
         * @return the fragment, or null if the position is invalid
         */
        abstract fun buildFragment(position: Int): Fragment?
    }
}


