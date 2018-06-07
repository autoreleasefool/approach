package ca.josephroque.bowlingcompanion.teams.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMembersListFragment
import kotlinx.android.synthetic.main.view_team_member_header.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing the details of a single team and its members.
 */
class TeamDetailsFragment : BaseFragment(),
        ListFragment.OnListFragmentInteractionListener {

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

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = savedInstanceState?.getParcelable(ARG_TEAM) ?: arguments?.getParcelable(ARG_TEAM)
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_team_details, container, false)

        val team = team
        if (savedInstanceState == null && team != null) {
            // TODO: is there a way to embed this directly in XML?
            val fragment = TeamMembersListFragment.newInstance(team)
            childFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragment)
                commit()
            }
        }

        setupHeader(view)

        return view
    }

    /**
     * Set up the header of the view.
     */
    private fun setupHeader(rootView: View) {
        rootView.tv_header_title.setText(R.string.team_members)
        rootView.tv_header_caption.setText(R.string.team_members_select_league)
    }

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is TeamMember) {
            TODO("not implemented")
        }
    }
}
