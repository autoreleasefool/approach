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
): BaseRecyclerViewAdapter<Team>(values, listener) {

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<Team>.ViewHolder {
        return when (ViewType.fromInt(viewType)) {
            ViewType.Active -> { ViewHolderActive(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_team, parent, false))
            }
            ViewType.Deleted -> { ViewHolderDeleted(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_deleted, parent, false))
            } else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    /** @Override */
    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<Team>.ViewHolder, position: Int) {
        holder.bind(values[position], position)
    }

    inner class ViewHolderActive(view: View) : BaseRecyclerViewAdapter<Team>.ViewHolder(view) {
        /** Render name of the item. */
        private val tvName: TextView? = view.findViewById(R.id.tv_name)
        /** Render members of the team. */
        private val flowMembers: FlowLayout? = view.findViewById(R.id.flow_members)

        override fun bind(item: Team, position: Int) {
            val context = itemView.context
            tvName?.text = item.name

            if (position % 2 == 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
            }

            flowMembers?.removeAllViews()
            item.members.forEach({
                val memberView = Chip(context)
                memberView.isFocusable = false
                memberView.isClickable = false
                memberView.chipText = it.first
                memberView.changeBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                memberView.textColor = ContextCompat.getColor(context, R.color.primaryWhiteText)
                flowMembers?.addView(memberView)
            })
            val chipMargin = context.resources.getDimension(R.dimen.chip_margin).toInt()
            flowMembers?.childSpacing = chipMargin

            itemView.setOnClickListener(this@TeamRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@TeamRecyclerViewAdapter)
        }
    }

    /**
     * Build and render a deleted item in the list.
     */
    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<Team>.ViewHolder(view) {
        /** Render name of the deleted item. */
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        override fun bind(item: Team, position: Int) {
            val context = itemView.context

            tvDeleted?.text = String.format(
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
            itemView.setOnClickListener(deletedItemListener)
            itemView.setOnLongClickListener(null)
            tvUndo?.setOnClickListener(deletedItemListener)
        }
    }
}
