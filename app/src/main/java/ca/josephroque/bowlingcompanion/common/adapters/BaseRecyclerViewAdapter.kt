package ca.josephroque.bowlingcompanion.common.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A basic [RecyclerView.Adapter] to handle common operations.
 */
abstract class BaseRecyclerViewAdapter<Item : IIdentifiable>(
    values: List<Item>,
    protected var delegate: AdapterDelegate<Item>?
) : RecyclerView.Adapter<BaseRecyclerViewAdapter<Item>.ViewHolder>(),
        View.OnClickListener,
        View.OnLongClickListener {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseRecyclerViewAdapter"

        fun applyDefaultDivider(recyclerView: RecyclerView, context: Context) {
            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            recyclerView.addItemDecoration(itemDecorator)
        }
    }

    var swipeable: Boolean = false
        set(value) {
            if (multiSelect) {
                throw AssertionError("Cannot be multiSelect and swipeable simultaneously")
            }
            field = value
            notifyDataSetChanged()
        }

    var multiSelect: Boolean = false
        set(value) {
            if (swipeable) {
                throw AssertionError("Cannot be multiSelect and swipeable simultaneously")
            }
            field = value
            _selectedItems.clear()
            notifyDataSetChanged()
        }

    var longPressable: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var itemTouchHelper: ItemTouchHelper? = null
    private var recyclerView: RecyclerView? = null

    var items: List<Item> = values
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    open fun getItemAt(position: Int): Item {
        return items[position]
    }

    open fun getPositionOfItem(item: Item): Int {
        return item.indexInList(items)
    }

    private var _selectedItems: MutableSet<Item> = HashSet()
    val selectedItems: Set<Item>
        get() = _selectedItems

    val selectedItemsInOrder: List<Item>
        get() = items.filter { _selectedItems.contains(it) }

    // MARK: Lifecycle functions

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        itemTouchHelper = ItemTouchHelper(buildItemTouchHelper())
        itemTouchHelper?.attachToRecyclerView(this.recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
        delegate = null
        itemTouchHelper = null
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // MARK: BaseRecyclerViewAdapter

    fun setSelectedElementsWithIds(ids: Set<Long>) {
        if (multiSelect) {
            items.forEachIndexed { index: Int, it: Item ->
                if (ids.contains(it.id)) {
                    if (_selectedItems.add(it)) {
                        notifyItemChanged(index)
                    }
                } else {
                    if (_selectedItems.remove(it)) {
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    // MARK: OnClickListener

    override fun onClick(v: View) {
        recyclerView?.let {
            val position = it.getChildAdapterPosition(v)
            val item = getItemAt(position)
            if (multiSelect) {
                if (!_selectedItems.remove(item)) {
                    _selectedItems.add(item)
                }

                notifyItemChanged(position)
            }
            delegate?.onItemClick(item)
        }
    }

    // MARK: OnLongClickListener

    override fun onLongClick(v: View): Boolean {
        if (!longPressable) {
            return false
        }

        recyclerView?.let {
            delegate?.onItemLongClick(getItemAt(it.getChildAdapterPosition(v)))
            return true
        }

        return false
    }

    open fun buildItemTouchHelper(): ItemTouchHelper.Callback {
        return SwipeCallback()
    }

    // MARK:: ViewHolder

    abstract inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Item, position: Int)
    }

    // MARK: SwipeCallback

    inner class SwipeCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (swipeable) {
                val position = viewHolder.adapterPosition
                delegate?.onItemSwipe(getItemAt(position))
            }
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (swipeable) {
                ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            } else {
                0
            }
        }
    }

    // MARK: AdapterDelegate

    interface AdapterDelegate<in T : Any> {
        fun onItemClick(item: T)
        fun onItemLongClick(item: T)
        fun onItemSwipe(item: T)
        fun onItemDelete(item: T)
    }
}
