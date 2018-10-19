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
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
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
        ListFragment.ListFragmentDelegate,
        IFloatingActionButtonHandler {

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

    private var isSharing: Boolean = false
        set(value) {
            field = value
            adapter?.multiSelect = isSharing

            if (isSharing) {
                adapter?.setSelectedElementsWithIds(HashSet(games.map { it.id }))
                headerTitle = R.string.sharing_instructions_title
                headerSubtitle = R.string.sharing_instructions_body
            } else {
                headerTitle = null
                headerSubtitle = null
            }
            fabProvider?.invalidateFab()
        }

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let { games = it.getParcelableArrayList(ARG_GAMES)!! }
        setHasOptionsMenu(true)
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

        menu.findItem(R.id.action_share).isVisible = !isSharing
        menu.findItem(R.id.action_stop_sharing).isVisible = isSharing
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                isSharing = true
                activity?.invalidateOptionsMenu()
                true
            }
            R.id.action_stop_sharing -> {
                isSharing = false
                activity?.invalidateOptionsMenu()
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
        adapter.longPressable = true
        return adapter
    }

    override fun fetchItems(): Deferred<MutableList<Game>> {
        return async(CommonPool) {
            return@async this@GameOverviewFragment.games.toMutableList()
        }
    }

    // MARK: AdapterDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is Game) {
            if (longPress) {
                promptShareGames(listOf(item))
            }
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        // Intentionally left blank
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return if (isSharing) R.drawable.ic_share else null
    }

    override fun onFabClick() {
        if (isSharing) {
            adapter?.let {
                promptShareGames(it.selectedItems.toList())
            }
        }
    }

    // Private functions

    private fun promptShareGames(games: List<Game>) {

    }
}
