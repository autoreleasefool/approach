package ca.josephroque.bowlingcompanion.leagues

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
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.launch


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of leagues.
 */
class LeagueFragment : Fragment(),
        NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener<League> {

    companion object {
        /** Logging identifier. */
        private const val TAG = "LeagueFragment"

        /** Identifier for the argument that represents the [Bowler] whose leagues are displayed. */
        private const val ARG_BOWLER = "${TAG}_bowler"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(bowler: Bowler): LeagueFragment {
            val fragment = LeagueFragment()
            val args = Bundle()
            args.putParcelable(ARG_BOWLER, bowler)
            fragment.arguments = args
            return fragment
        }
    }

    /** Interaction handler. */
    private var listener: OnLeagueFragmentInteractionListener? = null

    /** The bowler whose leagues are to be displayed. */
    private var bowler: Bowler? = null

    /** Adapter to manage rendering the list of leagues. */
    private var leagueAdapter: NameAverageRecyclerViewAdapter<League>? = null

    /** Bowlers to display. */
    private var leagues: MutableList<League> = ArrayList()

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_league_list, container, false)
        bowler = savedInstanceState?.getParcelable(ARG_BOWLER) ?: arguments?.getParcelable(ARG_BOWLER)

        if (view is RecyclerView) {
            val context = view.getContext()
            leagueAdapter = NameAverageRecyclerViewAdapter(emptyList(), this)
            leagueAdapter?.swipeable = true

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = leagueAdapter
            view.setHasFixedSize(true)
            NameAverageRecyclerViewAdapter.applyDefaultDivider(view, context)
        }

        setHasOptionsMenu(true)
        return view
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnLeagueFragmentInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnLeagueFragmentInteractionListener")
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
        refreshLeagueList()
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_leagues, menu)
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
     * Reload the list of leagues and update list.
     *
     * @param league if the league exists in the list only that entry will be updated
     */
    fun refreshLeagueList(league: League? = null) {
        val context = context?: return
        launch(Android) {
            bowler?.let {
                val index = league?.indexInList(this@LeagueFragment.leagues) ?: -1
                if (index == -1) {
                    val leagues = it.fetchLeagues(context).await()
                    this@LeagueFragment.leagues = leagues
                    leagueAdapter?.setElements(this@LeagueFragment.leagues)
                } else {
                    leagueAdapter?.notifyItemChanged(index)
                }
            }
        }
    }

    /**
     * Prompt user to sort the list of leagues in another order. Caches the chosen order.
     */
    @SuppressLint("ApplySharedPref")
    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.league_sort_options, { _, which: Int ->
                        val order = League.Companion.Sort.fromInt(which)
                        order?.let {
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.LEAGUE_SORT_ORDER, it.ordinal)
                                    .commit()
                            refreshLeagueList()
                        }
                    })
                    .show()
        }
    }

    /**
     * Handles interaction with the selected league.
     *
     * @param item the league the user is interacting with
     */
    override fun onNAItemClick(item: League) {
        listener?.onLeagueSelected(item, false)
    }

    /**
     * Deletes the selected league.
     *
     * @param item the league the user wishes to delete
     */
    override fun onNAItemDelete(item: League) {
        val context = context ?: return
        val index = item.indexInList(leagues)
        if (index != -1) {
            leagues.removeAt(index)
            leagueAdapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    /**
     * Sets the league to be deleted or active again (toggles from current state).
     *
     * @param item the league to delete or activate
     */
    override fun onNAItemSwipe(item: League) {
        val index = item.indexInList(leagues)
        if (index != -1) {
            item.isDeleted = !item.isDeleted
            leagueAdapter?.notifyItemChanged(index)
        }
    }

    /**
     * Shows option to edit the selected league.
     *
     * @param item the league the user wishes to edit
     */
    override fun onNAItemLongClick(item: League) {
        listener?.onLeagueSelected(item, true)
    }

    /**
     * Handles interactions with the list of leagues.
     */
    interface OnLeagueFragmentInteractionListener {

        /**
         * Indicates a league has been selected and further details should be shown to the user.
         *
         * @param bowler the league that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [League] or select
         */
        fun onLeagueSelected(league: League, toEdit: Boolean)
    }
}
