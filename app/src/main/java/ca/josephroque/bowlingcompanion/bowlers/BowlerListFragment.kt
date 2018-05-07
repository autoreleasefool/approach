package ca.josephroque.bowlingcompanion.bowlers

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.*
import ca.josephroque.bowlingcompanion.BowlerTeamTabbedFragment
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
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
        /** Logging identifier. */
        private const val TAG = "BowlerListFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): BowlerListFragment {
            return BowlerListFragment()
        }
    }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_bowlers, menu)
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by -> {
                showSortByDialog()
                true
            }
            else -> {
                false
            }
        }
    }

    /** @Override */
    override fun buildAdapter(): NameAverageRecyclerViewAdapter<Bowler> {
        val adapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.buildImageResource = { _, _ -> Pair(R.drawable.ic_person_white_24dp, Color.BLACK) }
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Bowler>> {
        context?.let {
            return Bowler.fetchAll(it)
        }

        return async(CommonPool) {
            emptyList<Bowler>().toMutableList()
        }
    }

    /**
     * Prompt user to sort the list of bowlers in another order. Caches the chosen order.
     */
    @SuppressLint("ApplySharedPref")
    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.bowler_sort_options, { _, which: Int ->
                        val order = Bowler.Companion.Sort.fromInt(which)
                        order?.let {
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.BOWLER_SORT_ORDER, it.ordinal)
                                    .commit()

                            val ignoredSet: MutableSet<Int> = HashSet()
                            ignoredSet.add(BowlerTeamTabbedFragment.Companion.Tab.Bowlers.ordinal)
                            (parentFragment as? BowlerTeamTabbedFragment)?.refreshTabs(ignoredSet)
                        }
                    })
                    .show()
        }
    }
}
