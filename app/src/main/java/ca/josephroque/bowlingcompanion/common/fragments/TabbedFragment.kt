package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.interfaces.IRefreshable
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager
import kotlinx.android.synthetic.main.fragment_common_tabs.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Base implementation for a fragment with tabs.
 */
abstract class TabbedFragment : BaseFragment(),
        IFloatingActionButtonHandler {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TabbedFragment"
    }

    /** Active tab. */
    protected var currentTab: Int
        get() = fragmentPager?.currentItem ?: 0
        set(value) { fragmentPager?.currentItem = value }

    /** Delegate for [TabbedFragment] events. */
    private var delegate: TabbedFragmentDelegate? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_tabs, container, false)
        configureTabLayout(rootView)
        return rootView
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? TabbedFragmentDelegate ?: throw RuntimeException("${context!!} must implement TabbedFragmentDelegate")
        delegate = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

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
     * For subclasses to respond to tab changes.
     *
     * @param newTab the new tab
     */
    abstract fun handleTabSwitch(newTab: Int)

    /**
     * Configure tab layout for rendering.
     *
     * @param rootView the root view of the fragment
     */
    private fun configureTabLayout(rootView: View) {
        rootView.tabbed_fragment_tabs.removeAllTabs()
        addTabs(rootView.tabbed_fragment_tabs)
        rootView.tabbed_fragment_pager.scrollingEnabled = false

        val adapter = buildPagerAdapter(rootView.tabbed_fragment_tabs.tabCount)
        rootView.tabbed_fragment_pager.adapter = adapter

        rootView.tabbed_fragment_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(rootView.tabbed_fragment_tabs))
        rootView.tabbed_fragment_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                rootView.tabbed_fragment_pager.currentItem = tab.position
                handleTabSwitch(tab.position)
                delegate?.onTabSwitched()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Reset the tab layout.
     */
    fun resetTabLayout() {
        configureTabLayout(view!!)
    }

    /**
     * Refresh lists in all tabs.
     *
     * @param ignored fragment tags to ignore
     */
    fun refreshTabs(ignored: Set<Int> = HashSet()) {
        // TODO: figure out how to use ignored
        val adapter = fragmentPager.adapter as? BaseFragmentPagerAdapter
        adapter?.let {
            for (i in 0 until it.count) {
                if (ignored.contains(i)) {
                    continue
                }

                val fragment = it.getFragment(i) as? IRefreshable
                fragment?.refresh()
            }
        }
    }

    /**
     * Delegate for [TabbedFragment] events.
     */
    interface TabbedFragmentDelegate {

        /**
         * Invoked when the current tab changes.
         */
        fun onTabSwitched()
    }
}
