package ca.josephroque.bowlingcompanion.common

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener
import ca.josephroque.bowlingcompanion.teams.Team


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [INameAverage] and makes a call to the
 * specified [OnNameAverageInteractionListener].
 */
class NameAverageRecyclerViewAdapter(
        private var values: List<INameAverage>,
        private var listener: OnNameAverageInteractionListener?
): RecyclerView.Adapter<NameAverageRecyclerViewAdapter.ViewHolder>() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "NameAverageRecyclerViewAdapter"

        /**
         * Apply a default [DividerItemDecoration] to the given [RecyclerView].
         *
         * @param recyclerView [RecyclerView] to add decorator to
         * @param context to build [DividerItemDecoration]
         */
        fun applyDefaultDivider(recyclerView: RecyclerView, context: Context) {
            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            recyclerView.addItemDecoration(itemDecorator)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        listener = null
    }

    /** @Override */
    override fun getItemCount(): Int {
        return values.size
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_name_average, parent, false)
        return ViewHolder(view)
    }

    /** @Override */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = values[position]
        holder.tvName.text = values[position].name
        holder.tvAverage.text = values[position].getRoundedAverage(1)

        if (holder.item is Bowler) {
            holder.ivIcon.setImageResource(R.drawable.ic_person_black_24dp)
        } else if (holder.item is Team) {
            holder.ivIcon.setImageResource(R.drawable.ic_people_black_24dp)
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.view.setOnClickListener {
            listener?.onNAItemClick(holder.item!!)
        }
    }

    /**
     * Update elements in the [RecyclerView].
     *
     * @param items new list of items to display
     */
    fun setElements(items: List<INameAverage>) {
        values = items
        notifyDataSetChanged()
    }

    /**
     * View Holder.
     */
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        /** Render name of the INameAverage item. */
        val tvName: TextView = view.findViewById(R.id.tv_name)

        /** Render average of the INameAverage item. */
        val tvAverage: TextView = view.findViewById(R.id.tv_average)

        /** Render type indicator of the INameAverage item. */
        val ivIcon: ImageView = view.findViewById(R.id.iv_name_average)

        /** INameAverage item. */
        var item: INameAverage? = null
    }

    /**
     * Handles interactions with items in the list.
     */
    interface OnNameAverageInteractionListener {

        /**
         * Indicates user interaction with the item.
         *
         * @param item interacted item
         */
        fun onNAItemClick(item: INameAverage)

        /**
         * Indicates long click user interaction with the item.
         *
         * @param item interacted item
         */
        fun onNAItemLongClick(item: INameAverage)

        /**
         * Indicates user desire to delete the item.
         *
         * @param item deleted item
         */
        fun onNAItemDelete(item: INameAverage)
    }
}
