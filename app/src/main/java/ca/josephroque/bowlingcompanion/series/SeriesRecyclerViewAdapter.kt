package ca.josephroque.bowlingcompanion.series

import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.settings.Settings
import com.nex3z.flowlayout.FlowLayout

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [Series] and makes a call to the specified delegate
 * upon interactions.
 */
class SeriesRecyclerViewAdapter(
    values: List<Series>,
    delegate: BaseRecyclerViewAdapter.AdapterDelegate<Series>?
) : BaseRecyclerViewAdapter<Series>(values, delegate) {

    companion object {
        @Suppress("unused")
        private const val TAG = "SeriesRVAdapter"

        private enum class ViewType {
            Condensed,
            Expanded,
            Deleted;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    var seriesView: Series.Companion.View = Series.Companion.View.Expanded
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var seriesHighlightMin: Int = 0
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var gameHighlightMin: Int = 0
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var shouldHighlightSeries: Boolean = true
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var shouldHighlightScores: Boolean = true
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    // MARK: BaseRecyclerViewAdapter

    override fun getItemViewType(position: Int): Int {
        return when {
            getItemAt(position).isDeleted -> ViewType.Deleted.ordinal
            !getItemAt(position).isDeleted && seriesView == Series.Companion.View.Condensed -> ViewType.Condensed.ordinal
            !getItemAt(position).isDeleted && seriesView == Series.Companion.View.Expanded -> ViewType.Expanded.ordinal
            else -> throw IllegalArgumentException("Position `$position` is invalid")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<Series>.ViewHolder {
        return when (ViewType.fromInt(viewType)) {
            ViewType.Condensed -> ViewHolderCondensed(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_series_condensed, parent, false))
            ViewType.Expanded -> ViewHolderExpanded(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_series_expanded, parent, false))
            ViewType.Deleted -> ViewHolderDeleted(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_deleted, parent, false))
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<Series>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position))
    }

    // MARK: SeriesRecyclerViewAdapter

    private fun shouldHighlightSeries(seriesTotal: Int, numberOfGames: Int): Boolean {
        return when {
            seriesHighlightMin > 0 -> shouldHighlightSeries && seriesTotal > seriesHighlightMin
            seriesHighlightMin == -1 -> shouldHighlightSeries && seriesTotal > League.DEFAULT_SERIES_HIGHLIGHT[numberOfGames - 1]
            else -> false
        }
    }

    private fun shouldHighlightGame(score: Int): Boolean {
        return when {
            gameHighlightMin > 0 -> shouldHighlightScores && score > gameHighlightMin
            gameHighlightMin == -1 -> shouldHighlightScores && score > League.DEFAULT_GAME_HIGHLIGHT
            else -> false
        }
    }

    // MARK: ViewHolderCondensed

    inner class ViewHolderCondensed(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        private val tvDate: TextView? = view.findViewById(R.id.tv_date)
        private val tvTotal: TextView? = view.findViewById(R.id.tv_total)

        override fun bind(item: Series) {
            val context = itemView.context
            val seriesTotal = item.scores.sum()

            tvDate?.text = item.prettyDate
            tvTotal?.text = seriesTotal.toString()

            if (shouldHighlightSeries(seriesTotal, item.numberOfGames)) {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
            } else {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            itemView.setOnClickListener(this@SeriesRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@SeriesRecyclerViewAdapter)
        }
    }

    // MARK: ViewHolderExpanded

    inner class ViewHolderExpanded(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        private val tvDate: TextView? = view.findViewById(R.id.tv_date)
        private val tvTotal: TextView? = view.findViewById(R.id.tv_total)

        private val flowScores: FlowLayout? = view.findViewById(R.id.flow_scores)

        override fun bind(item: Series) {
            val context = itemView.context
            val seriesTotal = item.scores.sum()

            tvDate?.text = item.prettyDate
            tvTotal?.text = seriesTotal.toString()

            if (shouldHighlightSeries(seriesTotal, item.numberOfGames)) {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
            } else {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val shouldShowMatchPlayResult = Settings.BooleanSetting.ShowMatchResults.getValue(preferences)
            val shouldHighlightMatchPlayResult = Settings.BooleanSetting.HighlightMatchResults.getValue(preferences)

            flowScores?.removeAllViews()
            if (shouldShowMatchPlayResult) {
                for (i in 0 until item.scores.size) {
                    val id = View.generateViewId()
                    val scoreView = SeriesScoreView(context)
                    val matchPlayResult = MatchPlayResult.fromInt(item.matchPlay[i].toInt())!!
                    scoreView.id = id
                    scoreView.isFocusable = false
                    scoreView.isClickable = false
                    scoreView.score = item.scores[i]
                    scoreView.matchPlay = matchPlayResult

                    scoreView.scoreTextColor = if (shouldHighlightGame(item.scores[i])) {
                        ContextCompat.getColor(context, R.color.gameHighlight)
                    } else {
                        ContextCompat.getColor(context, R.color.primaryBlackText)
                    }

                    when {
                        !shouldHighlightMatchPlayResult || matchPlayResult == MatchPlayResult.NONE -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.primaryBlackText)
                        matchPlayResult == MatchPlayResult.WON -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayWin)
                        matchPlayResult == MatchPlayResult.LOST -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayLoss)
                        matchPlayResult == MatchPlayResult.TIED -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayTie)
                    }

                    flowScores?.addView(scoreView)
                }
            }

            itemView.setOnClickListener(this@SeriesRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@SeriesRecyclerViewAdapter)
        }
    }

    // MARK: ViewHolderDeleted

    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        override fun bind(item: Series) {
            val context = itemView.context

            tvDeleted?.text = String.format(
                    context.resources.getString(R.string.query_delete_item),
                    getItemAt(adapterPosition).prettyDate
            )

            val deletedItemListener = View.OnClickListener {
                if (it.id == R.id.tv_undo) {
                    delegate?.onItemSwipe(getItemAt(adapterPosition))
                } else {
                    delegate?.onItemDelete(getItemAt(adapterPosition))
                }
            }
            itemView.setOnClickListener(deletedItemListener)
            itemView.setOnLongClickListener(null)
            tvUndo?.setOnClickListener(deletedItemListener)
        }
    }
}
