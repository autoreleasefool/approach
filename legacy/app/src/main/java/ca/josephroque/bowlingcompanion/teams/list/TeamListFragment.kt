package ca.josephroque.bowlingcompanion.teams.list

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
        @Suppress("unused")
        private const val TAG = "TeamListFragment"

        fun newInstance(): TeamListFragment {
            return TeamListFragment()
        }
    }

    override val emptyViewImage = R.drawable.empty_view_teams
    override val emptyViewText = R.string.empty_view_teams

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_teams, menu)
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

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: ListFragment

    override fun fetchItems(): Deferred<MutableList<Team>> {
        context?.let {
            return Team.fetchAll(it)
        }

        return async(CommonPool) {
            mutableListOf<Team>()
        }
    }

    override fun buildAdapter(): TeamRecyclerViewAdapter {
        val adapter = TeamRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.longPressable = true
        return adapter
    }

    // MARK: Private functions

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
                                    .apply()
                            refreshList()
                        }
                    }
                    .show()
        }
    }
}
