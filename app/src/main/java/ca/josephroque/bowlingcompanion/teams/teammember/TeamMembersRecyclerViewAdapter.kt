package ca.josephroque.bowlingcompanion.teams.teammember

import android.support.v4.content.ContextCompat
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
        listener: BaseRecyclerViewAdapter.OnAdapterInteractionListener<TeamMember>
) : BaseRecyclerViewAdapter<TeamMember>(items, listener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "TeamMembersRecyclerViewAdapter"
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

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<TeamMember>.ViewHolder, position: Int) {
        holder.bind(items[position], position)
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
            ivIcon.setImageResource(R.drawable.ic_person)
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

            if (position % 2 == 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
            }

            itemView.setOnClickListener(this@TeamMembersRecyclerViewAdapter)
        }
    }
}
