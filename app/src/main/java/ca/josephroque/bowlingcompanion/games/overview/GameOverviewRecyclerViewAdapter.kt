package ca.josephroque.bowlingcompanion.games.overview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.views.ScoreSheet

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [Game] and makes a call to the specified delegate
 * upon interactions.
 */
class GameOverviewRecyclerViewAdapter(
    values: List<Game>,
    delegate: BaseRecyclerViewAdapter.AdapterDelegate<Game>?
) : BaseRecyclerViewAdapter<Game>(values, delegate) {

    private var scrollOffsets: MutableMap<Int, Pair<Int, Int>> = HashMap()

    companion object {
        @Suppress("unused")
        private const val TAG = "GameOverviewRVAdapter"
    }

    // MARK: BaseRecyclerViewAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<Game>.ViewHolder {
        return ViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_game_overview, parent, false))
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<Game>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position), position)
    }

    // MARK: ViewHolder

    inner class ViewHolder(view: View) : BaseRecyclerViewAdapter<Game>.ViewHolder(view) {
        private val tvGameNumber: TextView? = view.findViewById(R.id.tv_game_number)
        private val scoreSheet: ScoreSheet? = view.findViewById(R.id.score_sheet)
        private val checkBox: CheckBox? = view.findViewById(R.id.checkbox_share)

        override fun bind(item: Game, position: Int) {
            val context = itemView.context

            tvGameNumber?.text = context.resources.getString(R.string.game_number).format(item.ordinal)

            if (multiSelect) {
                checkBox?.visibility = View.VISIBLE
                checkBox?.isChecked = selectedItems.contains(item)
            } else {
                checkBox?.visibility = View.GONE
            }

            val scoreText = item.getScoreTextForFrames()
            val ballText = item.getBallTextForFrames()
            scoreSheet?.let {
                it.frameNumbersEnabled = false
                it.finalScore = item.score
                it.updateFrames(-1, -1, scoreText, ballText)
                item.frames.forEachIndexed { frameIdx, frame ->
                    frame.ballFouled.forEachIndexed { ballIdx, foul ->
                        it.setFoulEnabled(frameIdx, ballIdx, foul)
                    }
                }

                // Remember scroll position
                val (x, y) = scrollOffsets[position] ?: Pair(0, 0)
                it.scrollTo(x, y)

                it.delegate = object : ScoreSheet.SheetScrollListener {
                    override fun didScroll(x: Int, y: Int) {
                        scrollOffsets[position] = Pair(x, y)
                    }
                }
            }

            itemView.setOnClickListener(this@GameOverviewRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@GameOverviewRecyclerViewAdapter)
        }
    }
}
