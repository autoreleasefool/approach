package ca.josephroque.bowlingcompanion.teams

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Teams.
 */
class TeamFragment : Fragment(), TeamRecyclerViewAdapter.OnTeamInteractionListener {

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
    /** Adapter to manage rendering the list of team. */
    private var teamAdapter: TeamRecyclerViewAdapter? = null
    /** List of teams available. */
    private var teams: MutableList<Team> = ArrayList()

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_common_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            teamAdapter = TeamRecyclerViewAdapter(emptyList(), this)
            teamAdapter?.swipingEnabled = true

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = teamAdapter
            view.setHasFixedSize(true)
            TeamRecyclerViewAdapter.applyDefaultDivider(view, context)
        }

        setHasOptionsMenu(true)
        return view
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
    override fun onResume() {
        super.onResume()
        refreshTeamList()
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

    /**
     * Reload the list of teams and update list.
     *
     * @param team if the team exists in the list only that entry will be updated
     */
    fun refreshTeamList(team: Team? = null) {
        val context = context?: return
        launch(Android) {
            val index = team?.indexInList(this@TeamFragment.teams) ?: -1
            if (index == -1) {
                val teams = Team.fetchAll(context).await()
                this@TeamFragment.teams = teams
                teamAdapter?.setElements(this@TeamFragment.teams)
            } else {
                teamAdapter?.notifyItemChanged(index)
            }
        }
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
                            refreshTeamList()
                        }
                    })
                    .show()
        }
    }

    /** @Override */
    override fun onTeamClick(team: Team) {
        listener?.onTeamSelected(team, false)
    }

    /** @Override */
    override fun onTeamDelete(team: Team) {
        val context = context ?: return
        val index = team.indexInList(teams)
        if (index != -1) {
            teams.removeAt(index)
            teamAdapter?.notifyItemRemoved(index)
            team.delete(context)
        }
    }

    /** @Override */
    override fun onTeamLongClick(team: Team) {
        listener?.onTeamSelected(team, true)
    }

    /** @Override */
    override fun onTeamSwipe(team: Team) {
        val index = team.indexInList(teams)
        if (index != -1) {
            team.isDeleted = !team.isDeleted
            teamAdapter?.notifyItemChanged(index)
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
