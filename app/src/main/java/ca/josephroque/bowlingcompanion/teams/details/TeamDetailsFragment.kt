package ca.josephroque.bowlingcompanion.teams.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.TeamMember
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing the details of a single team and its members.
 */
class TeamDetailsFragment : ListFragment<TeamMember, TeamDetailsRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamDetailsFragment"

        /** Identifier for the argument that represents the [Team] whose details are displayed. */
        private const val ARG_TEAM = "${TAG}_team"

        /**
         * Creates a new instance.
         *
         * @param team team to load details of
         * @return the new instance
         */
        fun newInstance(team: Team): TeamDetailsFragment {
            val fragment = TeamDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_TEAM, team)
            fragment.arguments = args
            fragment.canIgnoreListener = true
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
    override fun buildAdapter(): TeamDetailsRecyclerViewAdapter {
        val teamMembers: List<TeamMember> = team?.members ?: emptyList()
        return TeamDetailsRecyclerViewAdapter(teamMembers, this)
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
    override fun onItemClick(item: TeamMember) {
        super.onItemClick(item)

        TODO("not implemented")
    }
}