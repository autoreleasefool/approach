package ca.josephroque.bowlingcompanion.leagues

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of leagues.
 */
class LeagueListFragment : ListFragment<League, NameAverageRecyclerViewAdapter<League>>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "LeagueListFragment"

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
        fun newInstance(bowler: Bowler, showEvents: Boolean): LeagueListFragment {
            val fragment = LeagueListFragment()
            val args = Bundle()
            args.putParcelable(ARG_BOWLER, bowler)
            args.putBoolean(ARG_SHOW_EVENTS, showEvents)
            fragment.arguments = args
            return fragment
        }
    }

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
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** @Override */
    override fun buildAdapter(): NameAverageRecyclerViewAdapter<League> {
        val adapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.buildImageResource = { item, _ ->
            if (item.isEvent) {
                Pair(R.drawable.ic_event_white_24dp, Color.BLACK)
            } else {
                Pair(R.drawable.ic_league_white_24dp, Color.BLACK)
            }
        }
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<League>> {
        return async(CommonPool) {
            this@LeagueListFragment.context?.let { context ->
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

    /**
     * Disable deleting the Practice league
     *
     * @param item the league to be deleted
     */
    override fun onItemDelete(item: League) {
        if (item.name == League.PRACTICE_LEAGUE_NAME) {
            context?.let {
                BCError(
                        R.string.error_deleting_league,
                        R.string.error_cannot_delete_practice_league,
                        BCError.Severity.Warning
                ).show(it)
            }
            return
        }

        super.onItemDelete(item)
    }

    /**
     * Disable long pressing the league item.
     *
     * @param item the league that was long clicked
     */
    override fun onItemLongClick(item: League) {
        if (item.name == League.PRACTICE_LEAGUE_NAME) return
        super.onItemClick(item)
    }
}
