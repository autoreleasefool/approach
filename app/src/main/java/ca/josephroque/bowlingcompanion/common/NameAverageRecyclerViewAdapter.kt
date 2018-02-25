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
class NameAverageRecyclerViewAdapter(private var mValues: List<INameAverage>, private val mListener: OnNameAverageInteractionListener?): RecyclerView.Adapter<NameAverageRecyclerViewAdapter.ViewHolder>() {

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

    /** @Override */
    override fun getItemCount(): Int {
        return mValues.size
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_name_average, parent, false)
        return ViewHolder(view)
    }

    /** @Override */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.mItem = mValues[position]
        holder.mNameView.text = mValues[position].name
        holder.mAverageView.text = mValues[position].getRoundedAverage(1)

        if (holder.mItem is Bowler) {
            holder.mIconView.setImageResource(R.drawable.ic_person_black_24dp)
        } else if (holder.mItem is Team) {
            holder.mIconView.setImageResource(R.drawable.ic_people_black_24dp)
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.mView.setOnClickListener {
            mListener?.onNAItemClick(holder.mItem!!)
        }
    }

    /**
     * Update elements in the [RecyclerView].
     *
     * @param items new list of items to display
     */
    fun setElements(items: List<INameAverage>) {
        mValues = items
        notifyDataSetChanged()
    }

    /**
     * View Holder.
     */
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        /** Render name of the INameAverage item. */
        val mNameView: TextView
        /** Render average of the INameAverage item. */
        val mAverageView: TextView
        /** Render type indicator of the INameAverage item. */
        val mIconView: ImageView
        /** INameAverage item. */
        var mItem: INameAverage? = null

        init {
            mNameView = mView.findViewById(R.id.tv_name)
            mAverageView = mView.findViewById(R.id.tv_average)
            mIconView = mView.findViewById(R.id.iv_name_average)
        }
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
