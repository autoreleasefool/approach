package ca.josephroque.bowlingcompanion.common.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.IIdentifiable

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A basic [RecyclerView.Adapter] to handle common operations.
 */
abstract class BaseRecyclerViewAdapter<Item: IIdentifiable, ViewHolder: RecyclerView.ViewHolder>(
        protected var values: List<Item>,
        protected var listener: OnAdapterInteractionListener<Item>?
) : RecyclerView.Adapter<ViewHolder>(),
        View.OnClickListener,
        View.OnLongClickListener {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BaseRecyclerViewAdapter"

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

    /** Handles complex interactions with the [RecyclerView] (swipe/drag). */
    private var itemTouchHelper: ItemTouchHelper? = null

    /** Reference to the attached [RecyclerView]. */
    private var recyclerView: RecyclerView? = null

    /** Currently selected items */
    private var _selectedItems: MutableSet<Item> = HashSet()
    val selectedItems: Set<Item>
        get() = _selectedItems

    /** @Override */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        itemTouchHelper = ItemTouchHelper(SwipeCallback())
        itemTouchHelper?.attachToRecyclerView(this.recyclerView)
    }

    /** @Override */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
        listener = null
        itemTouchHelper = null
    }

    /** @Override */
    override fun getItemCount(): Int {
        return values.size
    }

    /**
     * Update elements in the [RecyclerView].
     *
     * @param items new list of items to display
     */
    fun setElements(items: List<Item>) {
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
            values.forEachIndexed({ index: Int, it: Item ->
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
            listener?.onItemClick(item)
        }
    }

    /** @Override */
    override fun onLongClick(v: View): Boolean {
        recyclerView?.let {
            if (multiSelect) {
                return false
            }

            listener?.onItemLongClick(values[it.getChildAdapterPosition(v)])
            return true
        }

        return false
    }

    inner class SwipeCallback: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        /** @Override */
        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        /** @Override */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (swipeable) {
                val position = viewHolder.adapterPosition
                listener?.onItemSwipe(values[position])
            }
        }

        /**
         * Disable swiping when [swipeable] is false.
         *
         * @Override
         */
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (swipeable) {
                ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            } else {
                0
            }
        }
    }

    /**
     * Handles interactions with items in the list.
     */
    interface OnAdapterInteractionListener<in T : Any> {

        /**
         * Indicates user interaction with the item.
         *
         * @param item interacted item
         */
        fun onItemClick(item: T)

        /**
         * Indicates long click user interaction with the item.
         *
         * @param item interacted item
         */
        fun onItemLongClick(item: T)

        /**
         * Indicates user swiped an item away.
         *
         * @param item swiped item
         */
        fun onItemSwipe(item: T)

        /**
         * Indicates user deleted an item.
         *
         * @param item deleted item
         */
        fun onItemDelete(item: T)
    }
}