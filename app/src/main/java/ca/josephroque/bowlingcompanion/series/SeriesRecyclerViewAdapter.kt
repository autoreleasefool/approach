package ca.josephroque.bowlingcompanion.series

import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
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

        private val chipGroupScores: ChipGroup? = view.findViewById(R.id.cg_scores)

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

            chipGroupScores?.removeAllViews()
            if (shouldShowMatchPlayResult) {
                for (i in 0 until item.scores.size) {
                    val viewId = View.generateViewId()
                    val score = item.scores[i]
                    val matchPlayResult = MatchPlayResult.fromInt(item.matchPlay[i].toInt())!!

                    val chipIconResource: Int?
                    val chipIconTintResource: Int?
                    when (matchPlayResult) {
                        MatchPlayResult.WON -> {
                            chipIconResource = R.drawable.ic_match_play_won_chip
                            chipIconTintResource = if (shouldHighlightMatchPlayResult) R.color.matchPlayWin else R.color.primaryBlackText
                        }
                        MatchPlayResult.LOST -> {
                            chipIconResource = R.drawable.ic_match_play_lost_chip
                            chipIconTintResource = if (shouldHighlightMatchPlayResult) R.color.matchPlayLoss else R.color.primaryBlackText
                        }
                        MatchPlayResult.TIED -> {
                            chipIconResource = R.drawable.ic_match_play_tied_chip
                            chipIconTintResource = if (shouldHighlightMatchPlayResult) R.color.matchPlayTie else R.color.primaryBlackText
                        }
                        MatchPlayResult.NONE -> {
                            chipIconResource = null
                            chipIconTintResource = null
                        }
                    }
                    val chipTextColorResource = if (shouldHighlightGame(score)) R.color.gameHighlight else R.color.primaryBlackText

                    val chip = Chip(context).apply {
                        id = viewId
                        isFocusable = false
                        isClickable = false
                        text = score.toString()
                        setTextColor(ContextCompat.getColor(context, chipTextColorResource))
                        chipIconResource?.let { setChipIconResource(it) }
                        chipIconTintResource?.let { setChipIconTintResource(it) }
                        setChipBackgroundColorResource(R.color.colorListContrast)
                    }

                    chipGroupScores?.addView(chip)
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
