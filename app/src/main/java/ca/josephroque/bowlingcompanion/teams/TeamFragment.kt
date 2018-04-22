package ca.josephroque.bowlingcompanion.teams

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Teams.
 */
class TeamFragment : ListFragment<Team, TeamRecyclerViewAdapter.ViewHolder, TeamRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "TeamFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): TeamFragment {
            return TeamFragment()
        }
    }

    /** Handle team interaction events. */
    private var listener: OnTeamFragmentInteractionListener? = null

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
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnTeamFragmentInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnTeamFragmentInteractionListener")
        listener = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_teams, menu)
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
    override fun fetchItems(): Deferred<MutableList<Team>> {
        context?.let {
            return Team.fetchAll(it)
        }

        return async(CommonPool) {
            emptyList<Team>().toMutableList()
        }
    }

    override fun buildAdapter(): TeamRecyclerViewAdapter {
        val adapter = TeamRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
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
                    .setItems(R.array.team_sort_options, { _, which: Int ->
                        val order = Bowler.Companion.Sort.fromInt(which)
                        order?.let {
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.TEAM_SORT_ORDER, it.ordinal)
                                    .commit()
                            refreshList()
                        }
                    })
                    .show()
        }
    }

    /** @Override */
    override fun onItemClick(item: Team) {
        listener?.onTeamSelected(item, false)
    }

    /** @Override */
    override fun onItemDelete(item: Team) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    /** @Override */
    override fun onItemLongClick(item: Team) {
        listener?.onTeamSelected(item, true)
    }

    /** @Override */
    override fun onItemSwipe(item: Team) {
        val index = item.indexInList(items)
        if (index != -1) {
            item.isDeleted = !item.isDeleted
            adapter?.notifyItemChanged(index)
        }
    }

    /**
     * Handles interactions with the Team list.
     */
    interface OnTeamFragmentInteractionListener {

        /**
         * Indicates a team has been selected and further details should be shown to the user.
         *
         * @param team the team that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [Team] or select
         */
        fun onTeamSelected(team: Team, toEdit: Boolean)
    }
}
