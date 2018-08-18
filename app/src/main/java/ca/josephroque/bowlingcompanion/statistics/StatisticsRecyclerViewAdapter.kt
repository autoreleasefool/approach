package ca.josephroque.bowlingcompanion.statistics

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a list of [StatisticListItem]s and makes calls to the specified listener
 * upon interactions.
 */
class StatisticsRecyclerViewAdapter(
        values: List<StatisticListItem>,
        listener: BaseRecyclerViewAdapter.OnAdapterInteractionListener<StatisticListItem>?
) : BaseRecyclerViewAdapter<StatisticListItem>(values, listener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatisticsRVAdapter"

        /**
         * Types of items to display in the list.
         */
        private enum class ViewType {
            Header,
            Item;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    /** @override */
    override fun getItemViewType(position: Int): Int {
        return when (getItemAt(position)) {
            is StatisticsCategory -> ViewType.Header.ordinal
            is Statistic -> ViewType.Item.ordinal
            else -> throw IllegalArgumentException("StatisticListItems can only be Statistic or StatisticsCategory.")
        }
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (ViewType.fromInt(viewType)) {
            ViewType.Header -> HeaderViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_statistic_category, parent, false))
            ViewType.Item -> ItemViewHolder(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_statistic, parent, false))
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid.")
        }
    }

    /** @Override */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItemAt(position), position)
    }

    /**
     * Build and render a statistics category header.
     */
    inner class HeaderViewHolder(view: View) : BaseRecyclerViewAdapter<StatisticListItem>.ViewHolder(view) {
        /** Render title of the category. */
        private val tvTitle: TextView? = view.findViewById(R.id.tv_title)

        /** @Override */
        override fun bind(item: StatisticListItem, position: Int) {
            val context = itemView.context
            val header = item as StatisticsCategory

            tvTitle?.text = header.getTitle(context.resources)
        }
    }

    /**
     * Build and render a statistic.
     */
    inner class ItemViewHolder(view: View) : BaseRecyclerViewAdapter<StatisticListItem>.ViewHolder(view) {
        /** Render title of the statistic. */
        private val tvTitle: TextView? = view.findViewById(R.id.tv_title)
        /** Render value of the statistic. */
        private val tvValue: TextView? = view.findViewById(R.id.tv_value)

        /** @Override */
        override fun bind(item: StatisticListItem, position: Int) {
            val context = itemView.context
            val statistic = item as Statistic

            tvTitle?.text = statistic.getTitle(context.resources)
            tvValue?.text = statistic.displayValue

            itemView.setOnClickListener(this@StatisticsRecyclerViewAdapter)
        }
    }
}
