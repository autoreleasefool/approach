package ca.josephroque.bowlingcompanion.series

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.games.MatchPlayResult
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
): BaseRecyclerViewAdapter<Series, SeriesRecyclerViewAdapter.ViewHolder>(values, listener) {

    companion object {
        /** Logging identifier. */
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
            values[position].isDeleted -> ViewType.Deleted.ordinal
            !values[position].isDeleted && seriesView == Series.Companion.View.Condensed -> ViewType.Condensed.ordinal
            !values[position].isDeleted && seriesView == Series.Companion.View.Expanded -> ViewType.Expanded.ordinal
            else -> throw IllegalArgumentException("Position `$position` is invalid")
        }
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (ViewType.fromInt(viewType)) {
            ViewType.Condensed -> LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_series_condensed, parent, false)
            ViewType.Expanded -> LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_series_expanded, parent, false)
            ViewType.Deleted -> LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_deleted, parent, false)
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }

        return ViewHolder(view)
    }

    /** @Override */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = ViewType.fromInt(getItemViewType(position))
        when (viewType) {
            ViewType.Condensed -> bindCondensedViewHolder(holder, position)
            ViewType.Expanded -> bindExpandedViewHolder(holder, position)
            ViewType.Deleted -> bindDeletedViewHolder(holder, position)
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    /**
     * Sets up views to display a [Series] in a condensed view.
     *
     * @param holder the views to display items in
     * @param position the item to display
     */
    private fun bindCondensedViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val series = values[position]
        holder.series = series

        val seriesTotal = series.scores.sum()

        holder.tvDate?.text = series.prettyDate
        holder.tvTotal?.text = seriesTotal.toString()

        if (shouldHighlightSeries && seriesTotal >= seriesHighlightMin) {
            holder.tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
        } else {
            holder.tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
    }

    /**
     * Sets up views to display a [Series] in an expanded view.
     *
     * @param holder the views to display items in
     * @param position the item to display
     */
    private fun bindExpandedViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val series = values[position]
        holder.series = series

        val seriesTotal = series.scores.sum()

        holder.tvDate?.text = series.prettyDate
        holder.tvTotal?.text = seriesTotal.toString()

        if (shouldHighlightSeries && seriesHighlightMin > 0 && seriesTotal >= seriesHighlightMin) {
            holder.tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.seriesHighlight))
        } else {
            holder.tvTotal?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
        }

        holder.flowScores?.removeAllViews()
        for (i in 0 until series.scores.size) {
            val scoreView = SeriesScoreView(context)
            val matchPlayResult = MatchPlayResult.fromInt(series.matchPlay[i].toInt())!!
            scoreView.isFocusable = false
            scoreView.isClickable = false
            scoreView.score = series.scores[i]
            scoreView.matchPlay = matchPlayResult

            if (shouldHighlightScores && gameHighlightMin > 0 && series.scores[i] >= gameHighlightMin) {
                scoreView.scoreText?.setTextColor(ContextCompat.getColor(context, R.color.gameHighlight))
            } else {
                scoreView.scoreText?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            when (matchPlayResult) {
                MatchPlayResult.NONE -> scoreView.matchPlayText?.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
                MatchPlayResult.WON -> scoreView.matchPlayText?.setTextColor(ContextCompat.getColor(context, R.color.matchPlayWin))
                MatchPlayResult.LOST -> scoreView.matchPlayText?.setTextColor(ContextCompat.getColor(context, R.color.matchPlayLoss))
                MatchPlayResult.TIED -> scoreView.matchPlayText?.setTextColor(ContextCompat.getColor(context, R.color.matchPlayTie))
            }

            holder.flowScores?.addView(scoreView)
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
    }

    /**
     * Sets up views to display a deleted [Series].
     *
     * @param holder the views to display items in
     * @param position the item to display
     */
    private fun bindDeletedViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.series = values[position]

        holder.tvDeleted?.text = String.format(
                context.resources.getString(R.string.query_delete_item),
                values[position].prettyDate
        )

        val deletedItemListener = View.OnClickListener {
            if (it.id == R.id.tv_undo) {
                listener?.onItemSwipe(values[position])
            } else {
                listener?.onItemDelete(values[position])
            }
        }
        holder.itemView.setOnClickListener(deletedItemListener)
        holder.itemView.setOnLongClickListener(null)
        holder.tvUndo?.setOnClickListener(deletedItemListener)
    }


    /**
     * View holder.
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        /** Render date of the series. */
        val tvDate: TextView? = view.findViewById(R.id.tv_date)
        /** Render total of the series. */
        val tvTotal: TextView? = view.findViewById(R.id.tv_total)

        /** Render set of scores in the series. */
        val flowScores: FlowLayout? = view.findViewById(R.id.flow_scores)

        /** Render name of the deleted item. */
        val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        /** Series item. */
        var series: Series? = null
    }
}
