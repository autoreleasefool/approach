package ca.josephroque.bowlingcompanion.teams

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import com.nex3z.flowlayout.FlowLayout
import com.robertlevonyan.views.chip.Chip

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [Team] and makes a call to the listener.
 */
class TeamRecyclerViewAdapter(
        values: List<Team>,
        listener: BaseRecyclerViewAdapter.OnAdapterInteractionListener<Team>?
): BaseRecyclerViewAdapter<Team, TeamRecyclerViewAdapter.ViewHolder>(values, listener) {

    companion object {
        /** Logging identifier. */
        private const val TAG = "TeamRecyclerViewAdapter"

        /** Views can be active and accessible, or deleted. */
        private enum class ViewType {
            Active,
            Deleted;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    /** @Override */
    override fun getItemViewType(position: Int): Int {
        return if (values[position].isDeleted) {
            ViewType.Deleted.ordinal
        } else {
            ViewType.Active.ordinal
        }
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (ViewType.fromInt(viewType)) {
            ViewType.Active -> {
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_team, parent, false)
            }
            ViewType.Deleted -> {
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_deleted, parent, false)
            } else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }

        return ViewHolder(view)
    }

    /** @Override */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = ViewType.fromInt(getItemViewType(position))
        when(viewType) {
            ViewType.Active -> bindActiveViewHolder(holder, position)
            ViewType.Deleted -> bindDeletedViewHolder(holder, position)
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    /**
     * Set up views to display an active [Team] item.
     *
     * @param holder the views to display item in
     * @param position the item to display
     */
    private fun bindActiveViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val team = values[position]
        holder.item = team
        holder.tvName?.text = team.name

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.flowMembers?.removeAllViews()
        team.members.forEach({
            val memberView = Chip(context)
            memberView.isFocusable = false
            memberView.isClickable = false
            memberView.chipText = it.first
            memberView.changeBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            memberView.textColor = ContextCompat.getColor(context, R.color.primaryWhiteText)
            holder.flowMembers?.addView(memberView)
        })
        val chipMargin = context.resources.getDimension(R.dimen.chip_margin).toInt()
        holder.flowMembers?.childSpacing = chipMargin

        holder.view.setOnClickListener(this)
        holder.view.setOnLongClickListener(this)
    }

    /**
     * Set up views to display a deleted [Team] item.
     *
     * @param holder the views to display item in
     * @param position the item to display
     */
    private fun bindDeletedViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = values[position]

        holder.tvDeleted?.text = String.format(
                context.resources.getString(R.string.query_delete_item),
                values[position].name
        )

        val deletedItemListener = View.OnClickListener {
            if (it.id == R.id.tv_undo) {
                listener?.onItemSwipe(values[position])
            } else {
                listener?.onItemDelete(values[position])
            }
        }

        holder.view.setOnClickListener(deletedItemListener)
        holder.tvUndo?.setOnClickListener(deletedItemListener)
    }

    /**
     * View Holder.
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        /** Render name of the item. */
        val tvName: TextView? = view.findViewById(R.id.tv_name)
        /** Render members of the team. */
        val flowMembers: FlowLayout? = view.findViewById(R.id.flow_members)

        /** Render name of the deleted item. */
        val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        /** Team item. */
        var item: Team? = null
    }
}
