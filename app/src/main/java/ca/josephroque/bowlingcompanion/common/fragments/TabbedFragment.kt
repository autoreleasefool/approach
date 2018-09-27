package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
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
        IFloatingActionButtonHandler,
        IRefreshable {

    companion object {
        @Suppress("unused")
        private const val TAG = "TabbedFragment"
    }

    protected var currentTab: Int
        get() = fragmentPager?.currentItem ?: 0
        set(value) { fragmentPager?.currentItem = value }

    private var delegate: TabbedFragmentDelegate? = null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_tabs, container, false)
        configureTabLayout(rootView)
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? TabbedFragmentDelegate ?: throw RuntimeException("${context!!} must implement TabbedFragmentDelegate")
        delegate = context
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    // MARK: TabbedFragment

    fun resetTabLayout() {
        configureTabLayout(view!!)
    }

    fun refreshTabs(ignored: Set<Int> = HashSet()) {
        val adapter = fragmentPager.adapter as? FragmentPagerAdapter
        adapter?.let {
            for (i in 0 until it.count) {
                if (ignored.contains(i)) {
                    continue
                }

                val fragment = findFragmentByPosition(i) as? IRefreshable
                fragment?.refresh()
            }
        }
    }

    abstract fun addTabs(tabLayout: TabLayout)

    abstract fun buildPagerAdapter(tabCount: Int): FragmentPagerAdapter

    abstract fun handleTabSwitch(newTab: Int)

    fun findFragmentByPosition(position: Int): BaseFragment? {
        val fragmentPagerAdapter = fragmentPager.adapter as? FragmentPagerAdapter ?: return null
        val tag = "android:switcher:${fragmentPager.id}:${fragmentPagerAdapter.getItemId(position)}"
        return childFragmentManager.findFragmentByTag(tag) as? BaseFragment
}

    // MARK: Private functions

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

    // MARK: IRefreshable

    override fun refresh() {
        refreshTabs()
    }

    // MARK: BaseFragment

    override fun popChildFragment(): Boolean {
        val fragment = findFragmentByPosition(currentTab)
        return fragment?.popChildFragment() ?: super.popChildFragment()
    }

    // MARK: TabbedFragmentDelegate

    interface TabbedFragmentDelegate {
        fun onTabSwitched()
    }
}
