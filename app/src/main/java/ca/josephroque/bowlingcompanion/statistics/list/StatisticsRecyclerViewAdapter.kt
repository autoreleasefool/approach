package ca.josephroque.bowlingcompanion.statistics.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a list of [StatisticListItem]s and makes calls to the specified delegate
 * upon interactions.
 */
class StatisticsRecyclerViewAdapter(
    values: List<StatisticListItem>,
    delegate: BaseRecyclerViewAdapter.AdapterDelegate<StatisticListItem>?
) : BaseRecyclerViewAdapter<StatisticListItem>(values, delegate) {

    companion object {
        @Suppress("unused")
        private const val TAG = "StatisticsRVAdapter"

        private enum class ViewType {
            Header,
            Item;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    // MARK: BaseRecyclerViewAdapter

    override fun getItemViewType(position: Int): Int {
        return when (getItemAt(position)) {
            is StatisticsCategory -> ViewType.Header.ordinal
            is Statistic -> ViewType.Item.ordinal
            else -> throw IllegalArgumentException("StatisticListItems can only be Statistic or StatisticsCategory.")
        }
    }

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItemAt(position), position)
    }

    // MARK: HeaderViewHolder

    inner class HeaderViewHolder(view: View) : BaseRecyclerViewAdapter<StatisticListItem>.ViewHolder(view) {
        private val tvTitle: TextView? = view.findViewById(R.id.tv_title)

        override fun bind(item: StatisticListItem, position: Int) {
            val context = itemView.context
            val header = item as StatisticsCategory

            tvTitle?.text = header.getTitle(context.resources)
        }
    }

    // MARK: ItemViewHolder

    inner class ItemViewHolder(view: View) : BaseRecyclerViewAdapter<StatisticListItem>.ViewHolder(view) {
        private val tvTitle: TextView? = view.findViewById(R.id.tv_title)
        private val tvValue: TextView? = view.findViewById(R.id.tv_value)
        private val tvSubtitle: TextView? = view.findViewById(R.id.tv_subtitle)

        override fun bind(item: StatisticListItem, position: Int) {
            val context = itemView.context
            val statistic = item as Statistic

            tvTitle?.text = statistic.getTitle(context.resources)
            tvValue?.text = statistic.displayValue

            val subtitle = statistic.getSubtitle()
            if (subtitle != null) {
                tvSubtitle?.text = subtitle
                tvSubtitle?.visibility = View.VISIBLE
            } else {
                tvSubtitle?.visibility = View.GONE
            }

            itemView.setOnClickListener(this@StatisticsRecyclerViewAdapter)
        }
    }
}
