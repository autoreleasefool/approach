package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.leagues.League
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of series.
 */
class SeriesFragment : ListFragment<Series, SeriesRecyclerViewAdapter.ViewHolder, SeriesRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "SeriesFragment"

        /** Identifier for the argument that represents the [League] whose series are displayed. */
        private const val ARG_LEAGUE = "${TAG}_league"

        fun newInstance(league: League): SeriesFragment {
            val fragment = SeriesFragment()
            val args = Bundle()
            args.putParcelable(ARG_LEAGUE, league)
            fragment.arguments = args
            return fragment
        }
    }

    /** Interaction handler. */
    private var listener: OnSeriesFragmentInteractionListener? = null

    /** The league whose series are to be displayed. */
    private var league: League? = null

    private var showCondensedView: Boolean = false
        set(value) {
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
                        .edit()
                        .putBoolean(Series.SHOW_CONDENSED_VIEW, value)
                        .apply()
            }
            field = value
        }

    /** @Override. */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        league = savedInstanceState?.getParcelable(ARG_LEAGUE) ?: arguments?.getParcelable(ARG_LEAGUE)
        context?.let {
            showCondensedView = PreferenceManager.getDefaultSharedPreferences(it)
                    .getBoolean(Series.SHOW_CONDENSED_VIEW, false)
        }

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    /** @Override. */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnSeriesFragmentInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnSeriesFragmentInteractionListener ")
        listener = context
    }

    /** @Override. */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override. */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_series, menu)
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_stats -> {
                TODO("league stats not implemented")
            }
            else -> false
        }
    }

    /** @Override */
    override fun buildAdapter(): SeriesRecyclerViewAdapter {
        val adapter = SeriesRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.showCondensedView = showCondensedView
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Series>> {
        return async(CommonPool) {
            this@SeriesFragment.context?.let { context ->
                league?.let {
                    return@async it.fetchSeries(context).await()
                }
            }

            emptyList<Series>().toMutableList()
        }
    }

    /** @Override */
    override fun onItemClick(item: Series) {
        listener?.onSeriesSelected(item, false)
    }

    override fun onItemDelete(item: Series) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    /** @Override */
    override fun onItemSwipe(item: Series) {
        val index = item.indexInList(items)
        if (index != -1) {
            item.isDeleted = !item.isDeleted
            adapter?.notifyItemChanged(index)
        }
    }

    /** @Override */
    override fun onItemLongClick(item: Series) {
        listener?.onSeriesSelected(item, true)
    }

    /**
     * Handles interactions with the list of leagues.
     */
    interface OnSeriesFragmentInteractionListener {

        /**
         * Indicates a series has been selected and further details should be shown to the user.
         *
         * @param league the series that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [Series] or select
         */
        fun onSeriesSelected(series: Series, toEdit: Boolean)
    }

}