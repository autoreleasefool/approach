package ca.josephroque.bowlingcompanion.teams.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.GameControllerFragment
import ca.josephroque.bowlingcompanion.games.SeriesProvider
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMemberDialog
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMembersListFragment
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.view_team_member_header.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing the details of a single team and its members.
 */
class TeamDetailsFragment : BaseFragment(),
        IFloatingActionButtonHandler,
        ListFragment.OnListFragmentInteractionListener,
        TeamMemberDialog.OnTeamMemberDialogInteractionListener,
        TeamMembersListFragment.OnTeamMembersListFragmentInteractionListener {

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
            fragment.arguments = Bundle().apply { putParcelable(ARG_TEAM, team) }
            return fragment
        }
    }

    /** The team whose details are to be displayed. */
    private var team: Team? = null

    /** Indicate if all team members are ready for bowling to begin. */
    private var allTeamMembersReady: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                fabProvider?.invalidateFab()
            }
        }

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = arguments?.getParcelable(ARG_TEAM)

        val view = inflater.inflate(R.layout.fragment_team_details, container, false)
        setupHeader(view)

        val team = team
        if (savedInstanceState == null && team != null) {
            val fragment = TeamMembersListFragment.newInstance(team)
            childFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragment)
                commit()
            }
        }

        return view
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        fabProvider?.invalidateFab()
    }

    /**
     * Set up the header of the view.
     *
     * @param rootView the root view
     */
    private fun setupHeader(rootView: View) {
        rootView.tv_header_title.setText(R.string.team_members)
        rootView.tv_header_caption.setText(R.string.team_members_select_league)
    }

    /** @Override */
    override fun getFabImage(): Int? = if (allTeamMembersReady) R.drawable.ic_ball else null

    /** @Override */
    override fun onFabClick() {
        safeLet(context, team) { context, team ->
            launch(Android) {
                val error = attemptToBowl().await()
                if (error != null) {
                    error.show(context)
                    return@launch
                }

                val fragment = GameControllerFragment.newInstance(SeriesProvider.TeamSeries(team))
                fragmentNavigation?.pushFragment(fragment)
            }
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is TeamMember) {
            val fragment = TeamMemberDialog.newInstance(item)
            fragmentNavigation?.pushDialogFragment(fragment)
        }
    }

    /** @Override */
    override fun onFinishTeamMember(teamMember: TeamMember) {
        childFragmentManager.fragments
                .filter { it != null && it.isVisible }
                .forEach {
                    val list = it as? TeamMembersListFragment ?: return
                    list.refreshList(teamMember)
                    team = team?.replaceTeamMember(teamMember)
                }
    }

    /** @Override */
    override fun onTeamMembersReadyChanged(ready: Boolean) {
        allTeamMembersReady = ready
    }

    /** @Override */
    override fun onTeamMembersReordered(order: List<Long>) {
        team?.let { team = Team(it.id, it.name, it.members, order) }
    }

    /**
     * Attempt to begin a new round of bowling.
     *
     * @return [BCError] if any errors occur, or null if the [Team] is ready to bowl.
     */
    private fun attemptToBowl(): Deferred<BCError?> {
        return async(CommonPool) {
            val context = this@TeamDetailsFragment.context ?: return@async BCError()
            val team = this@TeamDetailsFragment.team ?: return@async BCError()

            if (allTeamMembersReady) {
                allTeamMembersReady = false
                val teamMemberSeries: MutableMap<TeamMember, Series> = HashMap()

                val database = Saviour.instance.getWritableDatabase(context).await()
                var transactionBegun = false

                // Create series in the database for each team member, if one does not exist,
                // or retrieve the existing series if it does.
                try {
                    team.members.forEach {
                        val league = it.league!!

                        when {
                            league.isEvent -> {
                                val eventSeries = league.fetchSeries(context).await()
                                assert(eventSeries.size == 1)
                                teamMemberSeries[it] = eventSeries.first()
                            }
                            it.series == null -> {
                                if (!transactionBegun) {
                                    transactionBegun = true
                                    database.beginTransaction()
                                }

                                val (newSeries, seriesError) = league.createNewSeries(
                                        context = context,
                                        inTransaction = true
                                ).await()

                                if (newSeries != null) {
                                    teamMemberSeries[it] = newSeries
                                } else if (seriesError != null) {
                                    return@async seriesError
                                }
                            }
                            else -> {
                                teamMemberSeries[it] = it.series
                            }
                        }
                    }

                    if (transactionBegun) {
                        database.setTransactionSuccessful()
                    }
                } finally {
                    if (transactionBegun) {
                        database.endTransaction()
                    }
                }

                // Replace immutable [TeamMember] instances with updated series
                val membersWithSeries = team.members.map {
                    return@map TeamMember(
                            teamId = it.teamId,
                            bowlerName = it.bowlerName,
                            bowlerId = it.bowlerId,
                            league = it.league,
                            series = teamMemberSeries[it]
                    )
                }

                // Replace team with updated members
                this@TeamDetailsFragment.team = Team(
                        id = team.id,
                        name = team.name,
                        members = membersWithSeries,
                        initialOrder = team.order
                )
            }

            return@async null
        }
    }
}
