package ca.josephroque.bowlingcompanion.teams.teammember

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.teams.Team

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [BaseRecyclerViewAdapter] that can display the members of a [Team].
 */
class TeamMembersRecyclerViewAdapter(
    items: List<TeamMember>,
    var itemsOrder: List<Long>,
    delegate: BaseRecyclerViewAdapter.AdapterDelegate<TeamMember>,
    private var moveDelegate: TeamMemberMoveDelegate?
) : BaseRecyclerViewAdapter<TeamMember>(items, delegate) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMembersRecyclerViewAdapter"
    }

    /** @Override */
    override fun getItemAt(position: Int): TeamMember {
        return items.first { it.id == itemsOrder[position] }
    }

    /** @Override */
    override fun getPositionOfItem(item: TeamMember): Int {
        return itemsOrder.indexOf(item.id)
    }

    /** @Override */
    override fun getItemViewType(position: Int): Int {
        return 0
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_team_member, parent, false)
        )
    }

    /** @Override */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        moveDelegate = null
    }

    /** @Override */
    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<TeamMember>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position), position)
    }

    /** @Override */
    override fun buildItemTouchHelper(): ItemTouchHelper.Callback {
        return DragCallback()
    }

    inner class ViewHolder(view: View) : BaseRecyclerViewAdapter<TeamMember>.ViewHolder(view) {
        /** Render name of the team member. */
        private val tvBowlerName: TextView = view.findViewById(R.id.tv_team_member_name)
        /** Render league selected for the member to bowl. */
        private val tvLeagueName: TextView = view.findViewById(R.id.tv_team_member_league)
        /** Render series selected for the member to bowl. */
        private val tvSeriesName: TextView = view.findViewById(R.id.tv_team_member_series)
        /** Render type indicator for the member. */
        private val ivIcon: ImageView = view.findViewById(R.id.iv_team_member_icon)

        override fun bind(item: TeamMember, position: Int) {
            val context = itemView.context
            ivIcon.setImageResource(R.drawable.ic_menu)
            ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primaryBlackIcon))

            tvBowlerName.text = item.bowlerName

            if (item.league != null) {
                tvLeagueName.text = item.league.name
                tvLeagueName.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            } else {
                tvLeagueName.setText(R.string.no_league_selected)
                tvLeagueName.setTextColor(ContextCompat.getColor(context, R.color.dangerRed))

                tvSeriesName.setText(R.string.no_series_selected)
                tvSeriesName.setTextColor(ContextCompat.getColor(context, R.color.dangerRed))
            }

            if (item.series != null) {
                tvSeriesName.text = item.series.prettyDate
                tvSeriesName.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            } else if (item.league != null) {
                tvSeriesName.setText(R.string.create_new_series)
                tvSeriesName.setTextColor(ContextCompat.getColor(context, R.color.primaryBlackText))
            }

            itemView.setOnClickListener(this@TeamMembersRecyclerViewAdapter)
        }
    }

    /**
     * Allow dragging of the Team Members for re-ordering.
     */
    inner class DragCallback : ItemTouchHelper.Callback() {

        /** @Override */
        override fun isLongPressDragEnabled() = true

        /** @Override */
        override fun isItemViewSwipeEnabled() = false

        /** @Override */
        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        /** @Override */
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            moveDelegate?.onTeamMemberMoved(viewHolder!!.adapterPosition, target!!.adapterPosition)
            return true
        }

        /** @Override */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {}
    }

    /**
     * Callback to move team members.
     */
    interface TeamMemberMoveDelegate {

        /**
         * Move a team member.
         *
         * @param from old position
         * @param to new position
         */
        fun onTeamMemberMoved(from: Int, to: Int)
    }
}
