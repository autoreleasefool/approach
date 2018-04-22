package ca.josephroque.bowlingcompanion.common.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.INameAverage

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [INameAverage] and makes a call to the
 * specified listener.
 */
class NameAverageRecyclerViewAdapter<T : INameAverage>(
        values: List<T>,
        listener: OnAdapterInteractionListener<T>?
): BaseRecyclerViewAdapter<T, NameAverageRecyclerViewAdapter<T>.ViewHolder>(values, listener) {

    companion object {
        /** Logging identifier. */
        private const val TAG = "NARecyclerViewAdapter"

        /** Views can be active and accessible, or deleted. */
        private enum class ViewType {
            Active,
            Selectable,
            Deleted;

            companion object {
                private val map = ViewType.values().associateBy(ViewType::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    /**
     * Optional function to build image resource for an item.
     * First Int is the image resource ID, second is the color filter to apply.
     */
    var buildImageResource: ((item: T, position: Int) -> Pair<Int, Int>)? = null

    /** @Override */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        buildImageResource = null
    }

    /** @Override */
    override fun getItemViewType(position: Int): Int {
        return when {
            swipeable && values[position].isDeleted -> ViewType.Deleted.ordinal
            multiSelect -> ViewType.Selectable.ordinal
            else -> ViewType.Active.ordinal
        }
    }

    /** @Override */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (ViewType.fromInt(viewType)) {
            ViewType.Active, ViewType.Selectable -> {
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_name_average, parent, false)
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
        when (viewType) {
            ViewType.Active -> bindActiveViewHolder(holder, position)
            ViewType.Selectable -> bindSelectableViewHolder(holder, position)
            ViewType.Deleted -> bindDeletedViewHolder(holder, position)
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    /**
     * Set up views to display an active [INameAverage] item.
     *
     * @param holder the views to display item in
     * @param position the item to display
     */
    private fun bindActiveViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = values[position]
        holder.item = item
        holder.tvName?.text = item.name
        holder.tvAverage?.text = item.getRoundedAverage(1)

        val imageResource = buildImageResource?.invoke(item, position)
        imageResource?.let {
            holder.ivIcon?.setImageResource(it.first)
            holder.ivIcon?.setColorFilter(it.second)
        }

        holder.ivIcon?.visibility = View.VISIBLE
        holder.checkBox?.visibility = View.GONE

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.view.setOnClickListener(this)
        holder.view.setOnLongClickListener(this)
    }

    /**
     * Set up views to display a selectable [INameAverage] item.
     *
     * @param holder the views to display item in
     * @param position the item to display
     */
    private fun bindSelectableViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = values[position]
        holder.tvName?.text = values[position].name
        holder.tvAverage?.text = values[position].getRoundedAverage(1)
        holder.checkBox?.isChecked = selectedItems.contains(values[position])

        holder.ivIcon?.visibility = View.GONE
        holder.checkBox?.visibility = View.VISIBLE

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.view.setOnClickListener(this)
        holder.view.setOnLongClickListener(this)
    }

    /**
     * Set up views to display a deleted [INameAverage] item.
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
        /** Render average of the item. */
        val tvAverage: TextView? = view.findViewById(R.id.tv_average)
        /** Render type indicator of the item. */
        val ivIcon: ImageView? = view.findViewById(R.id.iv_name_average)
        /** Render a checkbox indicating if the item is selected or not. Invisible by default. */
        val checkBox: CheckBox? = view.findViewById(R.id.check_name_average)

        /** Render name of the deleted item. */
        val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        /** INameAverage item. */
        var item: T? = null
    }
}
