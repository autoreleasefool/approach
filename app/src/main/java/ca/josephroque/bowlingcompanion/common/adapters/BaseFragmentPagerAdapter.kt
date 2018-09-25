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

    private val fragmentReferenceMap: MutableMap<Int, WeakReference<Fragment>> = HashMap(tabCount)

    // MARK: FragmentPagerAdapter

    final override fun getItem(position: Int): Fragment? {
        val fragment = buildFragment(position) ?: return null
        fragmentReferenceMap[position] = WeakReference(fragment)
        return fragment
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragmentReferenceMap.remove(position)
    }

    override fun getCount(): Int {
        return tabCount
    }

    // MARK: BaseFragmentPagerAdapter

    fun getFragment(position: Int): Fragment? {
        return fragmentReferenceMap[position]?.get()
    }

    abstract fun buildFragment(position: Int): Fragment?
}
