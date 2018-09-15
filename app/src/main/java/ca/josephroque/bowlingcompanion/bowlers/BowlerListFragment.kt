package ca.josephroque.bowlingcompanion.bowlers

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.BowlerTeamTabbedFragment
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Bowlers.
 */
class BowlerListFragment : ListFragment<Bowler, NameAverageRecyclerViewAdapter<Bowler>>() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BowlerListFragment"

        fun newInstance(): BowlerListFragment {
            return BowlerListFragment()
        }
    }

    // MARK Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_bowlers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by -> {
                showSortByDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MARK: ListFragment

    override fun buildAdapter(): NameAverageRecyclerViewAdapter<Bowler> {
        val adapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.longPressable = true
        adapter.buildImageResource = { _, _ -> Pair(R.drawable.ic_person, Color.BLACK) }
        return adapter
    }

    override fun fetchItems(): Deferred<MutableList<Bowler>> {
        context?.let {
            return Bowler.fetchAll(it)
        }

        return async(CommonPool) {
            mutableListOf<Bowler>()
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: Private functions

    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.bowler_sort_options) { _, which: Int ->
                        val order = Bowler.Companion.Sort.fromInt(which)
                        order?.let { sort ->
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.BOWLER_SORT_ORDER, sort.ordinal)
                                    .apply()

                            val ignoredSet: MutableSet<Int> = HashSet()
                            ignoredSet.add(BowlerTeamTabbedFragment.Companion.Tab.Bowlers.ordinal)
                            (parentFragment as? BowlerTeamTabbedFragment)?.refreshTabs(ignoredSet)

                            Analytics.trackSortedBowlers(order)
                        }
                    }
                    .show()
        }
    }
}
