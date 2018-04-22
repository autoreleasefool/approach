package ca.josephroque.bowlingcompanion.bowlers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.*
import ca.josephroque.bowlingcompanion.BowlerTeamListActivity
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.utils.Preferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Bowlers.
 */
class BowlerFragment : ListFragment<Bowler, NameAverageRecyclerViewAdapter<Bowler>.ViewHolder, NameAverageRecyclerViewAdapter<Bowler>>() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BowlerFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): BowlerFragment {
            return BowlerFragment()
        }
    }

    /** Interaction handler. */
    private var listener: OnBowlerFragmentInteractionListener? = null

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnBowlerFragmentInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnBowlerFragmentInteractionListener")
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
        inflater.inflate(R.menu.menu_fragment_bowlers, menu)
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
    override fun buildAdapter(): NameAverageRecyclerViewAdapter<Bowler> {
        val adapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.buildImageResource = { _, _ -> Pair(R.drawable.ic_person_white_24dp, Color.BLACK) }
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Bowler>> {
        context?.let {
            return Bowler.fetchAll(it)
        }

        return async(CommonPool) {
            emptyList<Bowler>().toMutableList()
        }
    }

    /**
     * Prompt user to sort the list of bowlers in another order. Caches the chosen order.
     */
    @SuppressLint("ApplySharedPref")
    private fun showSortByDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(it.resources.getString(R.string.sort_items))
                    .setItems(R.array.bowler_sort_options, { _, which: Int ->
                        val order = Bowler.Companion.Sort.fromInt(which)
                        order?.let {
                            PreferenceManager.getDefaultSharedPreferences(context)
                                    .edit()
                                    .putInt(Preferences.BOWLER_SORT_ORDER, it.ordinal)
                                    .commit()

                            val ignoredSet: MutableSet<Int> = HashSet()
                            ignoredSet.add(BowlerTeamListActivity.BOWLER_FRAGMENT)
                            (activity as? BowlerTeamListActivity)?.refreshTabs(ignoredSet)
                        }
                    })
                    .show()
        }
    }

    /** @Override */
    override fun onItemClick(item: Bowler) {
        listener?.onBowlerSelected(item, false)
    }

    /** @Override */
    override fun onItemDelete(item: Bowler) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)

            async(CommonPool) {
                item.delete(context).await()

                val ignoredSet: MutableSet<Int> = HashSet()
                ignoredSet.add(BowlerTeamListActivity.BOWLER_FRAGMENT)
                (activity as? BowlerTeamListActivity)?.refreshTabs(ignoredSet)
            }
        }
    }

    /** @Override */
    override fun onItemSwipe(item: Bowler) {
        val index = item.indexInList(items)
        if (index != -1) {
            item.isDeleted = !item.isDeleted
            adapter?.notifyItemChanged(index)
        }
    }

    /** @Override */
    override fun onItemLongClick(item: Bowler) {
        listener?.onBowlerSelected(item, true)
    }

    /**
     * Handles interactions with the Bowler list.
     */
    interface OnBowlerFragmentInteractionListener {

        /**
         * Indicates a bowler has been selected and further details should be shown to the user.
         *
         * @param bowler the bowler that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [Bowler] or select
         */
        fun onBowlerSelected(bowler: Bowler, toEdit: Boolean)
    }
}
