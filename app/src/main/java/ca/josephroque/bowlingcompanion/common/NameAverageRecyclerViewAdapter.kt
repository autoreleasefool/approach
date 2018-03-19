package ca.josephroque.bowlingcompanion.common

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * [RecyclerView.Adapter] that can display a [INameAverage] and makes a call to the
 * specified [OnNameAverageInteractionListener].
 */
class NameAverageRecyclerViewAdapter(
        private var values: List<INameAverage>,
        private var listener: OnNameAverageInteractionListener?
): RecyclerView.Adapter<NameAverageRecyclerViewAdapter.ViewHolder>(),
    View.OnClickListener,
    View.OnLongClickListener {

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

    /** Indicates if swiping is enabled on items in the [RecyclerView]. */
    var swipeable: Boolean = false
        set(value) {
            if (multiSelect) {
                throw AssertionError("Cannot be multiSelect and swipeable simultaneously")
            }
            field = value
        }

    /** Indicates if the list should be multi-select (if true), or single-select. */
    var multiSelect: Boolean = false
        set(value) {
            if (swipeable) {
                throw AssertionError("Cannot be multiSelect and swipeable simultaneously")
            }
            field = value
            _selectedItems.clear()
        }

    /** Reference to the attached [RecyclerView]. */
    private var recyclerView: RecyclerView? = null

    /** Handles complex interactions with the [RecyclerView] (swipe/drag). */
    private var itemTouchHelper: ItemTouchHelper? = null

    /** Currently selected items */
    private var _selectedItems: MutableSet<INameAverage> = HashSet()
    val selectedItems: Set<INameAverage>
        get() = _selectedItems

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        this.itemTouchHelper = ItemTouchHelper(SwipeCallback())
        this.itemTouchHelper?.attachToRecyclerView(this.recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        listener = null
        this.recyclerView = null
        this.itemTouchHelper = null
    }

    /** @Override */
    override fun getItemCount(): Int {
        return values.size
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
        holder.item = values[position]
        holder.tvName?.text = values[position].name
        holder.tvAverage?.text = values[position].getRoundedAverage(1)

        holder.ivIcon?.setColorFilter(Color.BLACK)
        if (holder.item is Bowler) {
            holder.ivIcon?.setImageResource(R.drawable.ic_person_white_24dp)
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
                listener?.onNAItemSwipe(values[position])
            } else {
                listener?.onNAItemDelete(values[position])
            }
        }
        holder.view.setOnClickListener(deletedItemListener)
        holder.tvUndo?.setOnClickListener(deletedItemListener)
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
     * Select the set of items with [ids] in the given set.
     *
     * @param ids items with these ids will be selected
     */
    fun setSelectedElementsWithIds(ids: Set<Long>) {
        if (multiSelect) {
            values.forEachIndexed({ index: Int, it: INameAverage ->
                if (ids.contains(it.id)) {
                    if (_selectedItems.add(it)) {
                        notifyItemChanged(index)
                    }
                } else {
                    if (_selectedItems.remove(it)) {
                        notifyItemChanged(index)
                    }
                }
            })
        }
    }

    /** @Override */
    override fun onClick(v: View) {
        recyclerView?.let {
            val position = it.getChildAdapterPosition(v)
            val item = values[position]
            if (multiSelect) {
                _selectedItems.let {
                    if (!it.remove(item)) {
                        it.add(item)
                    }
                }
                notifyItemChanged(position)
            }
            listener?.onNAItemClick(item)
        }
    }

    /** @Override */
    override fun onLongClick(v: View): Boolean {
        recyclerView?.let {
            if (multiSelect) {
                return false
            }

            listener?.onNAItemLongClick(values[it.getChildAdapterPosition(v)])
            return true
        }

        return false
    }

    inner class SwipeCallback: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        /** @Override */
        override fun onMove(
                recyclerView: RecyclerView?,
                viewHolder: RecyclerView.ViewHolder?,
                target: RecyclerView.ViewHolder?
        ): Boolean {
            return false
        }

        /** @Override */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            if (swipeable) {
                viewHolder?.let {
                    val position = it.adapterPosition
                    listener?.onNAItemSwipe(values[position])
                }
            }
        }

        /**
         * Disable swiping when [swipeable] is false.
         *
         * @Override
         */
        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            return if (swipeable) {
                ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            } else {
                0
            }
        }
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
         * Indicates user swiped an item away.
         *
         * @param item swiped item
         */
        fun onNAItemSwipe(item: INameAverage)

        /**
         * Indicates user deleted an item.
         *
         * @param item deleted item
         */
        fun onNAItemDelete(item: INameAverage)
    }
}
