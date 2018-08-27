package ca.josephroque.bowlingcompanion.teams.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Teams.
 */
class TeamListFragment : ListFragment<Team, TeamRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamListFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): TeamListFragment {
            return TeamListFragment()
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
        inflater.inflate(R.menu.fragment_teams, menu)
    }

    /** @Override */
    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by -> {
                showSortByDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Team>> {
        context?.let {
            return Team.fetchAll(it)
        }

        return async(CommonPool) {
            mutableListOf<Team>()
        }
    }

    /** @Override */
    override fun buildAdapter(): TeamRecyclerViewAdapter {
        val adapter = TeamRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.longPressable = true
        return adapter
    }

    /**
     * Prompt user to sort the list of teams in another order. Caches the chosen order.
     */
    @SuppressLint("ApplySharedPref")
    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.team_sort_options) { _, which: Int ->
                        val order = Bowler.Companion.Sort.fromInt(which)
                        order?.let { sort ->
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.TEAM_SORT_ORDER, sort.ordinal)
                                    .commit()
                            refreshList()
                        }
                    }
                    .show()
        }
    }
}
