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
import ca.josephroque.bowlingcompanion.database.DatabaseManager
import ca.josephroque.bowlingcompanion.games.GameControllerFragment
import ca.josephroque.bowlingcompanion.games.SeriesProvider
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMemberDialog
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMembersListFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.view_screen_header.view.*
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
        ListFragment.ListFragmentDelegate,
        TeamMemberDialog.TeamMemberDialogDelegate,
        TeamMembersListFragment.TeamMemberListFragmentDelegate,
        IStatisticsContext {

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamDetailsFragment"

        private const val ARG_TEAM = "${TAG}_team"

        fun newInstance(team: Team): TeamDetailsFragment {
            val fragment = TeamDetailsFragment()
            fragment.arguments = Bundle().apply { putParcelable(ARG_TEAM, team) }
            return fragment
        }
    }

    override val statisticsProviders: List<StatisticsProvider> by lazy {
        val team = team
        return@lazy if (team != null) {
            arrayListOf(StatisticsProvider.TeamStatistics(team))
        } else {
            emptyList<StatisticsProvider>()
        }
    }

    private var team: Team? = null

    private var allTeamMembersReady: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                launch(Android) {
                    fabProvider?.invalidateFab()
                }
            }
        }

    // MARK: Lifecycle functions

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

    override fun onStart() {
        super.onStart()
        fabProvider?.invalidateFab()
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        team?.let { navigationActivity?.setToolbarTitle(it.name) }
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? = if (allTeamMembersReady) R.drawable.ic_ball else null

    override fun onFabClick() {
        safeLet(context, team) { context, team ->
            val needsNewPracticeSeries = team.members.any { it.league?.isPractice == true && it.series == null }
            if (needsNewPracticeSeries) {
                League.showPracticeGamesPicker(context, ::launchAttemptToBowl)
            } else {
                launchAttemptToBowl()
            }
        }
    }

    // MARK: ListFragmentDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is TeamMember) {
            val fragment = TeamMemberDialog.newInstance(item)
            fragmentNavigation?.pushDialogFragment(fragment)
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        // Intentionally left blank
    }

    // MARK: TeamMemberDialogDelegate

    override fun onFinishTeamMember(teamMember: TeamMember) {
        childFragmentManager.fragments
                .filter { it != null && it.isVisible }
                .forEach {
                    val list = it as? TeamMembersListFragment ?: return
                    list.refreshList(teamMember)
                    team = team?.replaceTeamMember(teamMember)
                }
    }

    // MARK: TeamMemberListFragmentDelegate

    override fun onTeamMembersReadyChanged(ready: Boolean) {
        allTeamMembersReady = ready
    }

    override fun onTeamMembersReordered(order: List<Long>) {
        team?.let { team = Team(it.id, it.name, it.members, order) }

        Analytics.trackReorderTeamMembers()
    }

    // MARK: Private functions

    private fun setupHeader(rootView: View) {
        rootView.tv_header_title.setText(R.string.team_members)
        rootView.tv_header_caption.setText(R.string.team_members_select_league)
    }

    private fun launchAttemptToBowl(practiceNumberOfGames: Int = 1) {
        val context = context ?: return
        launch(Android) {
            val error = attemptToBowl(practiceNumberOfGames).await()
            if (error != null) {
                error.show(context)
                return@launch
            }

            val team = this@TeamDetailsFragment.team
            if (team != null) {
                val fragment = GameControllerFragment.newInstance(SeriesProvider.TeamSeries(team))
                fragmentNavigation?.pushFragment(fragment)
            } else {
                BCError().show(context)
            }
        }
    }

    private fun attemptToBowl(practiceNumberOfGames: Int): Deferred<BCError?> {
        return async(CommonPool) {
            val context = this@TeamDetailsFragment.context ?: return@async BCError()
            val team = this@TeamDetailsFragment.team ?: return@async BCError()

            if (allTeamMembersReady) {
                allTeamMembersReady = false
                val teamMemberSeries: MutableMap<TeamMember, Series> = HashMap()

                // Create series in the database for each team member, if one does not exist,
                // or retrieve the existing series if it does.
                val database = DatabaseManager.getWritableDatabase(context).await()
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
                                if (!database.inTransaction()) {
                                    database.beginTransaction()
                                }

                                val (newSeries, seriesError) = league.createNewSeries(
                                        context = context,
                                        openDatabase = database,
                                        numberOfPracticeGamesOverride = practiceNumberOfGames
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

                    if (database.inTransaction()) {
                        database.setTransactionSuccessful()
                    }
                } finally {
                    if (database.inTransaction()) {
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
