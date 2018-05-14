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
import ca.josephroque.bowlingcompanion.common.interfaces.INameAverage

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [INameAverage] and makes a call to the
 * specified listener.
 */
class NameAverageRecyclerViewAdapter<T : INameAverage>(
        values: List<T>,
        listener: OnAdapterInteractionListener<T>?
) : BaseRecyclerViewAdapter<T>(values, listener) {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter<T>.ViewHolder {
        return when (ViewType.fromInt(viewType)) {
            ViewType.Active, ViewType.Selectable -> ViewHolderActive(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_name_average, parent, false),
                    viewType == ViewType.Selectable.ordinal
                    )
            ViewType.Deleted -> ViewHolderDeleted(LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item_deleted, parent, false))
            else -> throw IllegalArgumentException("View Type `$viewType` is invalid")
        }
    }

    /** @Override */
    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<T>.ViewHolder, position: Int) {
        holder.bind(values[position], position)
    }

    /**
     * Build and render an active or selectable item in the list.
     */
    inner class ViewHolderActive(view: View, private val selectable: Boolean) : BaseRecyclerViewAdapter<T>.ViewHolder(view) {
        /** Render name of the item. */
        private val tvName: TextView? = view.findViewById(R.id.tv_name)
        /** Render average of the item. */
        private val tvAverage: TextView? = view.findViewById(R.id.tv_average)
        /** Render type indicator of the item. */
        private val ivIcon: ImageView? = view.findViewById(R.id.iv_name_average)
        /** Render a checkbox indicating if the item is selected or not. Invisible by default. */
        private val checkBox: CheckBox? = view.findViewById(R.id.check_name_average)

        override fun bind(item: T, position: Int) {
            val context = itemView.context
            tvName?.text = item.name
            tvAverage?.text = item.getRoundedAverage(1)
            checkBox?.isChecked = selectable && selectedItems.contains(item)

            if (selectable) {
                ivIcon?.visibility = View.GONE
                checkBox?.visibility = View.VISIBLE
                checkBox?.isChecked = selectedItems.contains(item)
            } else {
                ivIcon?.visibility = View.VISIBLE
                checkBox?.visibility = View.GONE

                val imageResource = buildImageResource?.invoke(item, position)
                imageResource?.let {
                    ivIcon?.setImageResource(it.first)
                    ivIcon?.setColorFilter(it.second)
                }
            }

            if (position % 2 == 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
            }

            itemView.setOnClickListener(this@NameAverageRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@NameAverageRecyclerViewAdapter)
        }
    }

    /**
     * Build and render a deleted item in the list.
     */
    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<T>.ViewHolder(view) {
        /** Render name of the deleted item. */
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        /** Button to undo deletion of an item. */
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        override fun bind(item: T, position: Int) {
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
