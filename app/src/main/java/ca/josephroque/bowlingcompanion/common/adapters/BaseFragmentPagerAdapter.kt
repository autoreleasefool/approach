package ca.josephroque.bowlingcompanion.common.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Base class for managing fragment paging.
 */
abstract class BaseFragmentPagerAdapter(
        fragmentManager: FragmentManager,
        private val tabCount: Int
) : FragmentPagerAdapter(fragmentManager) {

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
