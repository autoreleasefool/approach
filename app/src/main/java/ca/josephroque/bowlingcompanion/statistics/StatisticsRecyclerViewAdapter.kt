package ca.josephroque.bowlingcompanion.statistics

import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.statistics.provider.Statistic

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a list of [Statistic]s and makes calls to the specified listener
 * upon interactions.
 */
class StatisticsRecyclerViewAdapter(
        values: List<Statistic>,
        listener: BaseRecyclerViewAdapter.OnAdapterInteractionListener<Statistic>?
) : BaseRecyclerViewAdapter<Statistic>(values, listener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatisticsRVAdapter"
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

}
