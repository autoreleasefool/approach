package ca.josephroque.bowlingcompanion.teams.teammember

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.teams.Team
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.Collections

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to display a list of team members.
 */
class TeamMembersListFragment :
    ListFragment<TeamMember, TeamMembersRecyclerViewAdapter>(),
    TeamMembersRecyclerViewAdapter.TeamMemberMoveDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamMembersListFragment"

        private const val ARG_TEAM = "${TAG}_team"

        fun newInstance(team: Team): TeamMembersListFragment {
            val fragment = TeamMembersListFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_TEAM, team) }
            return fragment
        }
    }

    private var team: Team? = null
    private var teamMemberDelegate: TeamMemberListFragmentDelegate? = null

    private val allTeamMembersReady: Boolean
        get() = (adapter?.items?.filter { it.league != null }?.size ?: -1) == (adapter?.items?.size ?: -2)

    override val emptyViewImage = R.drawable.empty_view_teams
    override val emptyViewText = R.string.empty_view_team_members

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = arguments?.getParcelable(ARG_TEAM)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? TeamMemberListFragmentDelegate ?: throw RuntimeException("${parentFragment!!} must implement TeamMemberListFragmentDelegate")
        teamMemberDelegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        teamMemberDelegate = null
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: ListFragment

    override fun buildAdapter(): TeamMembersRecyclerViewAdapter {
        val teamMembers: List<TeamMember> = team?.members ?: emptyList()
        val teamMembersOrder: List<Long> = team?.order ?: emptyList()
        return TeamMembersRecyclerViewAdapter(teamMembers, teamMembersOrder, this, this)
    }

    override fun fetchItems(): Deferred<MutableList<TeamMember>> {
        return async(CommonPool) {
            team?.let {
                return@async it.membersInOrder.toMutableList()
            }
            mutableListOf<TeamMember>()
        }
    }

    override fun listWasRefreshed() {
        teamMemberDelegate?.onTeamMembersReadyChanged(allTeamMembersReady)
    }

    // MARK: TeamMemberMoveDelegate

    override fun onTeamMemberMoved(from: Int, to: Int) {
        val team = team ?: return
        val teamMemberOrder = team.order.toMutableList()
        Collections.swap(teamMemberOrder, from, to)

        // Update the team with the new order
        this@TeamMembersListFragment.team = Team(
                id = team.id,
                name = team.name,
                members = team.members,
                initialOrder = teamMemberOrder)
        arguments?.putParcelable(ARG_TEAM, this@TeamMembersListFragment.team)

        // Update adapter and delegate
        adapter?.itemsOrder = teamMemberOrder
        adapter?.notifyItemMoved(from, to)
        teamMemberDelegate?.onTeamMembersReordered(teamMemberOrder)
    }

    // MARK: TeamMemberListFragmentDelegate

    interface TeamMemberListFragmentDelegate {
        fun onTeamMembersReadyChanged(ready: Boolean)
        fun onTeamMembersReordered(order: List<Long>)
    }
}
