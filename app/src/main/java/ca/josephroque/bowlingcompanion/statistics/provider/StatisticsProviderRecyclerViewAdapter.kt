package ca.josephroque.bowlingcompanion.statistics.provider

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
 * [RecyclerView.Adapter] that can display a [StatisticsProvider] and makes a call to the
 * specified delegate.
 */
class StatisticsProviderRecyclerViewAdapter(
    items: List<StatisticsProvider>,
    delegate: AdapterDelegate<StatisticsProvider>?
) : BaseRecyclerViewAdapter<StatisticsProvider>(items, delegate) {

    companion object {
        @Suppress("unused")
        private const val TAG = "SPRecyclerViewAdapter"

        private enum class ViewType {
            Team,
            Bowler,
            League,
            Series,
            Game;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    // MARK: BaseRecyclerViewAdapter

    override fun getItemViewType(position: Int): Int {
        val item = getItemAt(position)
        val type: ViewType = when (item) {
            is StatisticsProvider.TeamStatistics -> ViewType.Team
            is StatisticsProvider.BowlerStatistics -> ViewType.Bowler
            is StatisticsProvider.LeagueStatistics -> ViewType.League
            is StatisticsProvider.SeriesStatistics -> ViewType.Series
            is StatisticsProvider.GameStatistics -> ViewType.Game
        }

        return type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<StatisticsProvider>.ViewHolder {
        return ViewHolderName(
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_provider, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<StatisticsProvider>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position), position)
    }

    // MARK: ViewHolderName

    inner class ViewHolderName(view: View) : BaseRecyclerViewAdapter<StatisticsProvider>.ViewHolder(view) {
        private val tvName: TextView? = view.findViewById(R.id.tv_name)
        private val tvType: TextView? = view.findViewById(R.id.tv_type)

        override fun bind(item: StatisticsProvider, position: Int) {
            tvName?.text = item.name
            tvType?.setText(item.typeName)
            itemView.setOnClickListener(this@StatisticsProviderRecyclerViewAdapter)
        }
    }
}
