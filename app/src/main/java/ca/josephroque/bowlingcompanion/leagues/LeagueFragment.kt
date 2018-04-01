package ca.josephroque.bowlingcompanion.leagues

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of leagues.
 */
class LeagueFragment : ListFragment<League, NameAverageRecyclerViewAdapter<League>.ViewHolder, NameAverageRecyclerViewAdapter<League>>() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "LeagueFragment"

        /** Identifier for the argument that represents the [Bowler] whose leagues are displayed. */
        private const val ARG_BOWLER = "${TAG}_bowler"

        /** Identifier for the argument indicating if this fragment should list leagues or events. */
        private const val ARG_SHOW_EVENTS = "${TAG}_events"

        /**
         * Creates a new instance.
         *
         * @param bowler bowler to load leagues/events of
         * @param showEvents true to show bowler's events, false to show leagues
         * @return the new instance
         */
        fun newInstance(bowler: Bowler, showEvents: Boolean): LeagueFragment {
            val fragment = LeagueFragment()
            val args = Bundle()
            args.putParcelable(ARG_BOWLER, bowler)
            args.putBoolean(ARG_SHOW_EVENTS, showEvents)
            fragment.arguments = args
            return fragment
        }
    }

    /** Interaction handler. */
    private var listener: OnLeagueFragmentInteractionListener? = null

    /** The bowler whose leagues are to be displayed. */
    private var bowler: Bowler? = null

    /** Indicates if this fragment should list leagues or events. */
    private var showEvents: Boolean = false

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bowler = savedInstanceState?.getParcelable(ARG_BOWLER) ?: arguments?.getParcelable(ARG_BOWLER)
        showEvents = arguments?.getBoolean(ARG_SHOW_EVENTS) ?: false
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
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

    /** @Override */
    override fun buildAdapter(): NameAverageRecyclerViewAdapter<League> {
        val adapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<League>> {
        return async(CommonPool) {
            this@LeagueFragment.context?.let { context ->
                bowler?.let {
                    if (showEvents) {
                        return@async it.fetchEvents(context).await()
                    } else {
                        return@async it.fetchLeagues(context).await()
                    }
                }
            }

            emptyList<League>().toMutableList()
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
                            refreshList()
                        }
                    })
                    .show()
        }
    }

    /** @Override */
    override fun onItemClick(item: League) {
        listener?.onLeagueSelected(item, false)
    }

    /** @Override */
    override fun onItemDelete(item: League) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    /** @Override */
    override fun onItemSwipe(item: League) {
        val index = item.indexInList(items)
        if (index != -1) {
            item.isDeleted = !item.isDeleted
            adapter?.notifyItemChanged(index)
        }
    }

    /** @Override */
    override fun onItemLongClick(item: League) {
        listener?.onLeagueSelected(item, true)
    }

    /**
     * Handles interactions with the list of leagues.
     */
    interface OnLeagueFragmentInteractionListener {

        /**
         * Indicates a league has been selected and further details should be shown to the user.
         *
         * @param league the league that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [League] or select
         */
        fun onLeagueSelected(league: League, toEdit: Boolean)
    }
}
