package ca.josephroque.bowlingcompanion.games.overview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.games.Game
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to display an overview of a list of games.
 */
class GameOverviewFragment : ListFragment<Game, GameOverviewRecyclerViewAdapter>(),
        ListFragment.ListFragmentDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameOverviewFragment"

        private const val ARG_GAMES = "${TAG}_games"

        fun newInstance(games: List<Game>): GameOverviewFragment {
            val fragment = GameOverviewFragment()
            fragment.arguments = Bundle().apply {
                putParcelableArrayList(ARG_GAMES, ArrayList(games))
            }
            return fragment
        }
    }

    private lateinit var games: List<Game>

    override val emptyViewImage = R.drawable.empty_view_leagues
    override val emptyViewText = R.string.empty_view_game_overview

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let { games = it.getParcelableArrayList(ARG_GAMES)!! }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        canIgnoreDelegate = true
        delegate = this
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_game_overview, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                promptShareSeries()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        context?.let { navigationActivity?.setToolbarTitle(it.resources.getString(R.string.overview)) }
    }

    // MARK: ListFragment

    override fun buildAdapter(): GameOverviewRecyclerViewAdapter {
        val adapter = GameOverviewRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = false
        adapter.multiSelect = true
        adapter.longPressable = true
        return adapter
    }

    override fun fetchItems(): Deferred<MutableList<Game>> {
        return async(CommonPool) {
            return@async this@GameOverviewFragment.games.toMutableList()
        }
    }

    override fun listWasRefreshed() {
        // Select all games when the list is refreshed
        adapter?.setSelectedElementsWithIds(HashSet(games.map { it.id }))
    }

    // MARK: AdapterDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is Game) {
            if (longPress) {
                promptShareGame(item)
            }
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        // Intentionally left blank
    }

    // Private functions

    private fun promptShareGame(game: Game) {
        TODO("not implemented")
    }

    private fun promptShareSeries() {
        TODO("not implemented")
        // TODO: analytics
    }
}
