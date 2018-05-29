package ca.josephroque.bowlingcompanion.series

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.leagues.League
import com.nex3z.flowlayout.FlowLayout

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [Series] and makes a call to the specified listener
 * upon interactions.
 */
class SeriesRecyclerViewAdapter(
        values: List<Series>,
        listener: BaseRecyclerViewAdapter.OnAdapterInteractionListener<Series>?
) : BaseRecyclerViewAdapter<Series>(values, listener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "SeriesRVAdapter"

        /**
         * Possible types of series views to be displayed.
         */
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

    /** If true, a condensed view of series is shown. */
    var seriesView: Series.Companion.View = Series.Companion.View.Expanded
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    /** Minimum color to highlight a series. */
    var seriesHighlightMin: Int = 0
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    /** Minimum color to highlight a game. */
    var gameHighlightMin: Int = 0
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    /** Indicates if series should be highlighted. */
    var shouldHighlightSeries: Boolean = true
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    /** Indicates if scores should be highlighted. */
    var shouldHighlightScores: Boolean = true
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    /** @Override */
    override fun getItemViewType(position: Int): Int {
        return when {
            items[position].isDeleted -> ViewType.Deleted.ordinal
            !items[position].isDeleted && seriesView == Series.Companion.View.Condensed -> ViewType.Condensed.ordinal
            !items[position].isDeleted && seriesView == Series.Companion.View.Expanded -> ViewType.Expanded.ordinal
            else -> throw IllegalArgumentException("Position `$position` is invalid")
        }
    }

    /** @Override */
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

    /** @Override */
    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<Series>.ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    /**
     * Check if a series should be highlighted.
     *
     * @param seriesTotal the series total
     * @param numberOfGames number of games in the series
     * @return true if the series total is high enough to highlight, and highlighting is active,
     *         false otherwise
     */
    private fun shouldHighlightSeries(seriesTotal: Int, numberOfGames: Int): Boolean {
        return when {
            seriesHighlightMin > 0 -> shouldHighlightSeries && seriesTotal > seriesHighlightMin
            seriesHighlightMin == -1 -> shouldHighlightSeries && seriesTotal > League.DEFAULT_SERIES_HIGHLIGHT[numberOfGames - 1]
            else -> false
        }
    }

    /**
     * Check if a game should be highlighted.
     *
     * @param score the score of the game
     * @return true if the score is high enough to highlight, and highlighting is active false otherwise
     */
    private fun shouldHighlightGame(score: Int): Boolean {
        return when {
            gameHighlightMin > 0 -> shouldHighlightScores && score > gameHighlightMin
            gameHighlightMin == -1 -> shouldHighlightScores && score > League.DEFAULT_GAME_HIGHLIGHT
            else -> false
        }
    }

    /**
     * Build and render a condensed series.
     */
    inner class ViewHolderCondensed(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        /** Render date of the series. */
        private val tvDate: TextView? = view.findViewById(R.id.tv_date)
        /** Render total of the series. */
        private val tvTotal: TextView? = view.findViewById(R.id.tv_total)

        /** @Override */
        override fun bind(item: Series, position: Int) {
            val context = itemView.context
            val seriesTotal = item.scores.sum()

            tvDate?.text = item.prettyDate
            tvTotal?.text = seriesTotal.toString()

            if (shouldHighlightSeries(seriesTotal, item.numberOfGames)) {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
            } else {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            if (position % 2 == 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
            }

            itemView.setOnClickListener(this@SeriesRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@SeriesRecyclerViewAdapter)
        }
    }

    /**
     * Build and render an expanded series.
     */
    inner class ViewHolderExpanded(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        /** Render date of the series. */
        private val tvDate: TextView? = view.findViewById(R.id.tv_date)
        /** Render total of the series. */
        private val tvTotal: TextView? = view.findViewById(R.id.tv_total)

        /** Render set of scores in the series. */
        private val flowScores: FlowLayout? = view.findViewById(R.id.flow_scores)

        /** @Override */
        override fun bind(item: Series, position: Int) {
            val context = itemView.context
            val seriesTotal = item.scores.sum()

            tvDate?.text = item.prettyDate
            tvTotal?.text = seriesTotal.toString()

            if (shouldHighlightSeries(seriesTotal, item.numberOfGames)) {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
            } else {
                tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            flowScores?.removeAllViews()
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

                when (matchPlayResult) {
                    MatchPlayResult.NONE -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.primaryBlackText)
                    MatchPlayResult.WON -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayWin)
                    MatchPlayResult.LOST -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayLoss)
                    MatchPlayResult.TIED -> scoreView.matchPlayTextColor = ContextCompat.getColor(context, R.color.matchPlayTie)
                }

                flowScores?.addView(scoreView)
            }

            if (position % 2 == 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
            }

            itemView.setOnClickListener(this@SeriesRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@SeriesRecyclerViewAdapter)
        }
    }

    /**
     * Build and render a deleted item in the list.
     */
    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<Series>.ViewHolder(view) {
        /** Render name of the deleted item. */
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        /** @Override */
        override fun bind(item: Series, position: Int) {
            val context = itemView.context

            tvDeleted?.text = String.format(
                    context.resources.getString(R.string.query_delete_item),
                    items[position].prettyDate
            )

            val deletedItemListener = View.OnClickListener {
                if (it.id == R.id.tv_undo) {
                    listener?.onItemSwipe(items[position])
                } else {
                    listener?.onItemDelete(items[position])
                }
            }
            itemView.setOnClickListener(deletedItemListener)
            itemView.setOnLongClickListener(null)
            tvUndo?.setOnClickListener(deletedItemListener)
        }
    }
}
