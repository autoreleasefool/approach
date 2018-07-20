package ca.josephroque.bowlingcompanion.leagues

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

        /** Identifier for the argument that represents the id of the [Bowler] whose leagues are displayed. */
        private const val ARG_BOWLER_ID = "${TAG}_bowler_id"

        /** Identifier for the argument indicating if this fragment should list leagues or events. */
        private const val ARG_SHOW = "${TAG}_show"

        /** Identifier for the single select mode. */
        private const val ARG_SINGLE_SELECT_MODE = "${TAG}_single_select"

        /**
         * Creates a new instance.
         *
         * @param bowler bowler to load leagues/events of
         * @param show what type of leagues to show in the list
         * @param singleSelectMode disables swiping and long press of the list
         * @return the new instance
         */
        fun newInstance(bowler: Bowler, show: Show, singleSelectMode: Boolean = false): LeagueListFragment {
            val fragment = LeagueListFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_BOWLER, bowler)
                putInt(ARG_SHOW, show.ordinal)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }

        /**
         * Creates a new instance.
         *
         * @param bowlerId id of bowler to load leagues/events of
         * @param show what type of leagues to show in the list
         * @param singleSelectMode disables swiping and long press of the list
         * @return the new instance
         */
        fun newInstance(bowlerId: Long, show: Show, singleSelectMode: Boolean = false): LeagueListFragment {
            val fragment = LeagueListFragment()
            fragment.arguments = Bundle().apply {
                putLong(ARG_BOWLER_ID, bowlerId)
                putInt(ARG_SHOW, show.ordinal)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }

        /**
         * What type of leagues to show in the list.
         */
        enum class Show {
            Events,
            Leagues,
            Both;

            companion object {
                private val map = Show.values().associateBy(Show::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    /** The bowler whose leagues are to be displayed. */
    private var bowler: Bowler? = null

    /** The id of the bowler whose leagues are to be displayed. Only used if `bowler` is unavailable. */
    private var bowlerId: Long? = null

    /** Indicates if this fragment should list leagues or events. */
    private var show: Show = Show.Both

    /** When true, the features of the fragment are limited to only offer selecting a single league. */
    private var singleSelectMode: Boolean = false

    /** @Override */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            bowler = it.getParcelable(ARG_BOWLER)
            bowlerId = it.getLong(ARG_BOWLER_ID)
            show = Show.fromInt(it.getInt(ARG_SHOW))!!
            singleSelectMode = it.getBoolean(ARG_SINGLE_SELECT_MODE)
        }

        setHasOptionsMenu(!singleSelectMode)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (singleSelectMode) {
            val parent = parentFragment as? OnListFragmentInteractionListener ?: return
            listener = parent
        }
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_leagues, menu)
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
        adapter.swipeable = !singleSelectMode
        adapter.longPressable = !singleSelectMode
        adapter.buildImageResource = { item, _ ->
            if (item.isEvent) {
                Pair(R.drawable.ic_event, Color.BLACK)
            } else {
                Pair(R.drawable.ic_league, Color.BLACK)
            }
        }
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<League>> {
        return async(CommonPool) {
            this@LeagueListFragment.context?.let { context ->
                val bowlerId = bowlerId
                if (bowler == null && bowlerId != null) {
                    bowler = Bowler.fetch(context, bowlerId).await()
                }

                bowler?.let {
                    when (show) {
                        Show.Events -> return@async it.fetchEvents(context).await()
                        Show.Leagues -> return@async it.fetchLeagues(context).await()
                        Show.Both -> return@async it.fetchLeaguesAndEvents(context).await()
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
        super.onItemLongClick(item)
    }
}
