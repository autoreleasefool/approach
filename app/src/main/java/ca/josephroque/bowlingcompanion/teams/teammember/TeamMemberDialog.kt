package ca.josephroque.bowlingcompanion.teams.teammember

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.FabController
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.leagues.LeagueListFragment
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.series.SeriesListFragment
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_team_member.view.*
import kotlinx.android.synthetic.main.view_team_member_header.view.*
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to select league and series for a team member.
 */
class TeamMemberDialog : BaseDialogFragment(),
        ListFragment.OnListFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMemberDialog"

        /** Identifier for the [TeamMember] to be edited. */
        private const val ARG_TEAM_MEMBER = "${TAG}_team_member"

        /** Identifier for the [League] selected for the [TeamMember]. */
        private const val ARG_SELECTED_LEAGUE = "${TAG}_selected_league"

        /**
         * Create a new instance of the dialog.
         *
         * @param teamMember [TeamMember] to select league for
         * @return the new instance
         */
        fun newInstance(teamMember: TeamMember): TeamMemberDialog {
            val dialog = TeamMemberDialog()
            dialog.arguments = Bundle().apply { putParcelable(ARG_TEAM_MEMBER, teamMember) }
            return dialog
        }
    }

    /** Team member to select league for. */
    private var teamMember: TeamMember? = null

    /** Interaction handler. */
    private var listener: OnTeamMemberDialogInteractionListener? = null

    /** The league selected by the user for the team member. */
    private var selectedLeague: League? = null

    /** The series selected by the user for the team member. */
    private var selectedSeries: Series? = null

    /** Controller for floating action button. */
    private lateinit var fabController: FabController

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        teamMember = arguments?.getParcelable(ARG_TEAM_MEMBER)
        selectedLeague = savedInstanceState?.getParcelable(ARG_SELECTED_LEAGUE)

        val rootView = inflater.inflate(R.layout.dialog_team_member, container, false)
        setupToolbar(rootView)
        setupFab(rootView)
        prepareActions(rootView, selectedLeague == null)
        setupChildFragment(savedInstanceState)
        childFragmentManager.addOnBackStackChangedListener(this)
        return rootView
    }

    /**
     * Set up title, style, and listeners for toolbar.
     *
     * @param rootView the root view
     */
    private fun setupToolbar(rootView: View) {
        rootView.toolbar_team_member.apply {
            setNavigationOnClickListener {
                if (selectedLeague == null) {
                    dismiss()
                } else {
                    selectedLeague = null
                    childFragmentManager.popBackStack()
                }
            }
        }
    }

    /**
     * Set up the floating action button controller.
     *
     * @param rootView the root view
     */
    private fun setupFab(rootView: View) {
        fabController = FabController(rootView.fab, View.OnClickListener {
            if (selectedLeague != null) {
                saveTeamMember()
            }
        })
    }

    /**
     * Set up the header and actions of the view.
     *
     * @param rootView the root view
     * @param forLeague true if the actions are for the league list, false if they are for the
     *                  series list
     */
    private fun prepareActions(rootView: View, forLeague: Boolean) {
        if (forLeague) {
            rootView.tv_header_title.setText(R.string.league)
            rootView.tv_header_caption.setText(R.string.team_members_leagues_select_a_league)
            fabController.image = null

            rootView.toolbar_team_member.apply {
                setNavigationIcon(R.drawable.ic_dismiss)
                title = teamMember?.bowlerName
            }
        } else {
            rootView.tv_header_title.setText(R.string.series)
            rootView.tv_header_caption.setText(R.string.team_members_series_select_a_series)
            fabController.image = R.drawable.ic_add

            rootView.toolbar_team_member.apply {
                setNavigationIcon(R.drawable.ic_arrow_back)
                title = selectedLeague?.name
            }
        }
    }

    /**
     * Create the child fragment if it has not been created yet.
     *
     * @param savedInstanceState the saved instance state
     */
    private fun setupChildFragment(savedInstanceState: Bundle?) {
        teamMember?.let {
            if (savedInstanceState == null) {
                val fragment = LeagueListFragment.newInstance(
                        bowlerId = it.bowlerId,
                        show = LeagueListFragment.Companion.Show.Both,
                        singleSelectMode = true
                )

                childFragmentManager.beginTransaction().apply {
                    add(R.id.fragment_container, fragment)
                    commit()
                }
            }
        }
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? OnTeamMemberDialogInteractionListener
                ?: throw RuntimeException("${parentFragment!!} must implement OnTeamMemberDialogInteractionListener")
        listener = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        view?.let { prepareActions(it, selectedLeague == null) }
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_SELECTED_LEAGUE, selectedLeague)
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /**
     * Clean up dialog before calling super.
     */
    override fun dismiss() {
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    /**
     * Save the current team member. Show errors if there are any.
     */
    private fun saveTeamMember() {
        launch(Android) {
            safeLet(teamMember, selectedLeague) { teamMember, league ->
                val newTeamMember = TeamMember(
                        teamId = teamMember.teamId,
                        bowlerName = teamMember.bowlerName,
                        bowlerId = teamMember.bowlerId,
                        leagueName = league.name,
                        leagueId = league.id,
                        seriesName = selectedSeries?.prettyDate,
                        seriesId = selectedSeries?.id ?: -1
                )
                listener?.onFinishTeamMember(newTeamMember)
                dismiss()
            }
        }
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is League) {
            selectedLeague = item
            if (item.isEvent) {
                saveTeamMember()
            } else {
                val fragment = SeriesListFragment.newInstance(item, true)
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, fragment)
                    addToBackStack(resources.getString(R.string.leagues))
                    commit()
                }
            }
        } else if (item is Series) {
            selectedSeries = item
            saveTeamMember()
        }
    }

    /** @Override */
    override fun onBackStackChanged() {
        view?.let { prepareActions(it, childFragmentManager.backStackEntryCount == 0) }
    }

    /** Handles interactions with the dialog. */
    interface OnTeamMemberDialogInteractionListener {

        /**
         * Indicates when the user has finished editing the [TeamMember].
         *
         * @param teamMember the finished [TeamMember]
         */
        fun onFinishTeamMember(teamMember: TeamMember)
    }
}