package ca.josephroque.bowlingcompanion.leagues

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
import ca.josephroque.bowlingcompanion.utils.Analytics
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
        @Suppress("unused")
        private const val TAG = "LeagueListFragment"

        private const val ARG_BOWLER = "${TAG}_bowler"
        private const val ARG_BOWLER_ID = "${TAG}_bowler_id"
        private const val ARG_SHOW = "${TAG}_show"
        private const val ARG_SINGLE_SELECT_MODE = "${TAG}_single_select"

        fun newInstance(bowler: Bowler, show: Show, singleSelectMode: Boolean = false): LeagueListFragment {
            val fragment = LeagueListFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_BOWLER, bowler)
                putInt(ARG_SHOW, show.ordinal)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }

        fun newInstance(bowlerId: Long, show: Show, singleSelectMode: Boolean = false): LeagueListFragment {
            val fragment = LeagueListFragment()
            fragment.arguments = Bundle().apply {
                putLong(ARG_BOWLER_ID, bowlerId)
                putInt(ARG_SHOW, show.ordinal)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }

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

    private var bowler: Bowler? = null
    private var bowlerId: Long? = null
    private var show: Show = Show.Both
    private var singleSelectMode: Boolean = false

    override val emptyViewImage: Int
        get() = if (show == Show.Events) R.drawable.empty_view_events else R.drawable.empty_view_leagues
    override val emptyViewText: Int
        get() = if (show == Show.Events) R.string.empty_view_events else R.string.empty_view_leagues

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            bowler = it.getParcelable(ARG_BOWLER)
            bowlerId = it.getLong(ARG_BOWLER_ID)
            show = Show.fromInt(it.getInt(ARG_SHOW))!!
            singleSelectMode = it.getBoolean(ARG_SINGLE_SELECT_MODE)
        }

        setHasOptionsMenu(!singleSelectMode)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        delegate = if (!singleSelectMode) {
            parentFragment as? ListFragmentDelegate ?: return
        } else {
            null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_leagues, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by -> {
                showSortByDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: ListFragment

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

            mutableListOf<League>()
        }
    }

    // MARK: Private functions

    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.league_sort_options) { _, which: Int ->
                        val order = League.Companion.Sort.fromInt(which)
                        order?.let { sort ->
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.LEAGUE_SORT_ORDER, sort.ordinal)
                                    .apply()
                            refreshList()

                            Analytics.trackSortedLeagues(order)
                        }
                    }
                    .show()
        }
    }

    // MARK: AdapterDelegate

    override fun onItemDelete(item: League) {
        // Disable deleting the practice league
        if (item.isPractice) {
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

    override fun onItemLongClick(item: League) {
        // Disable long pressing the practice league
        if (item.isPractice) return
        super.onItemLongClick(item)
    }
}
