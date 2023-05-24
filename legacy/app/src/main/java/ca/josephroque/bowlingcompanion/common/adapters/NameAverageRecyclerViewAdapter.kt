package ca.josephroque.bowlingcompanion.common.adapters

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
 * specified delegate.
 */
class NameAverageRecyclerViewAdapter<T : INameAverage>(
    items: List<T>,
    delegate: AdapterDelegate<T>?
) : BaseRecyclerViewAdapter<T>(items, delegate) {

    companion object {
        @Suppress("unused")
        private const val TAG = "NARecyclerViewAdapter"

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

    // MARK: BaseRecyclerViewAdapter

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        buildImageResource = null
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            swipeable && getItemAt(position).isDeleted -> ViewType.Deleted.ordinal
            multiSelect -> ViewType.Selectable.ordinal
            else -> ViewType.Active.ordinal
        }
    }

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

    override fun onBindViewHolder(holder: BaseRecyclerViewAdapter<T>.ViewHolder, position: Int) {
        holder.bind(getItemAt(position))
    }

    // MARK: ViewHolderActive

    inner class ViewHolderActive(view: View, private val selectable: Boolean) : BaseRecyclerViewAdapter<T>.ViewHolder(view) {
        private val tvName: TextView? = view.findViewById(R.id.tv_name)
        private val tvAverage: TextView? = view.findViewById(R.id.tv_average)
        private val ivIcon: ImageView? = view.findViewById(R.id.iv_name_average)
        private val checkBox: CheckBox? = view.findViewById(R.id.check_name_average)

        override fun bind(item: T) {
            val context = itemView.context

            tvName?.text = item.name
            tvAverage?.text = item.getDisplayAverage(context)
            checkBox?.isChecked = selectable && selectedItems.contains(item)

            if (selectable) {
                ivIcon?.visibility = View.GONE
                checkBox?.visibility = View.VISIBLE
                checkBox?.isChecked = selectedItems.contains(item)
            } else {
                ivIcon?.visibility = View.VISIBLE
                checkBox?.visibility = View.GONE

                val imageResource = buildImageResource?.invoke(item, adapterPosition)
                imageResource?.let {
                    ivIcon?.setImageResource(it.first)
                    ivIcon?.setColorFilter(it.second)
                }
            }

            itemView.setOnClickListener(this@NameAverageRecyclerViewAdapter)
            itemView.setOnLongClickListener(this@NameAverageRecyclerViewAdapter)
        }
    }

    // MARK: ViewHolderDeleted

    inner class ViewHolderDeleted(view: View) : BaseRecyclerViewAdapter<T>.ViewHolder(view) {
        private val tvDeleted: TextView? = view.findViewById(R.id.tv_deleted)
        private val tvUndo: TextView? = view.findViewById(R.id.tv_undo)

        override fun bind(item: T) {
            val context = itemView.context

            tvDeleted?.text = String.format(
                    context.resources.getString(R.string.query_delete_item),
                    getItemAt(adapterPosition).name
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
