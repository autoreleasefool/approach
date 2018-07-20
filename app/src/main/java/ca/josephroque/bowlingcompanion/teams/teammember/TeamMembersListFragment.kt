package ca.josephroque.bowlingcompanion.teams.teammember

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.teams.Team
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to display a list of team members.
 */
class TeamMembersListFragment : ListFragment<TeamMember, TeamMembersRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMembersListFragment"

        /** Identifier for the argument that represents the [Team] whose details are displayed. */
        private const val ARG_TEAM = "${TAG}_team"

        /**
         * Creates a new instance.
         *
         * @param team team to load details of
         * @return the new instance
         */
        fun newInstance(team: Team): TeamMembersListFragment {
            val fragment = TeamMembersListFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_TEAM, team) }
            return fragment
        }
    }

    /** The team whose details are to be displayed. */
    private var team: Team? = null

    /** Interaction teamMemberListener. */
    private var teamMemberListener: OnTeamMembersListFragmentInteractionListener? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = savedInstanceState?.getParcelable(ARG_TEAM) ?: arguments?.getParcelable(ARG_TEAM)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? OnTeamMembersListFragmentInteractionListener ?: throw RuntimeException("${parentFragment!!} must implement OnTeamMembersListFragmentInteractionListener")
        teamMemberListener = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        teamMemberListener = null
    }

    /** @Override */
    override fun buildAdapter(): TeamMembersRecyclerViewAdapter {
        val teamMembers: List<TeamMember> = team?.members ?: emptyList()
        return TeamMembersRecyclerViewAdapter(teamMembers, this)
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<TeamMember>> {
        return async(CommonPool) {
            team?.let {
                return@async it.members.toMutableList()
            }
            emptyList<TeamMember>().toMutableList()
        }
    }

    /** @Override */
    override fun listWasRefreshed() {
        teamMemberListener?.onTeamMembersReadyChanged(allTeamMembersReady())
    }

    /**
     * Check if all team members are ready to begin bowling.
     *
     * @return true if all members have a league associated, false otherwise
     */
    private fun allTeamMembersReady(): Boolean {
        return (adapter?.items?.filter { it.league != null }?.size ?: -1) == (adapter?.items?.size ?: -2)
    }

    /**
     * Handle interactions with the list.
     */
    interface OnTeamMembersListFragmentInteractionListener {

        /**
         * Called when the team members that are ready change.
         *
         * @param ready true when all team members are ready to bowl, false otherwise.
         */
        fun onTeamMembersReadyChanged(ready: Boolean)
    }
}
