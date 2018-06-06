package ca.josephroque.bowlingcompanion.teams.teammember

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

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = savedInstanceState?.getParcelable(ARG_TEAM) ?: arguments?.getParcelable(ARG_TEAM)
        return super.onCreateView(inflater, container, savedInstanceState)
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
}
