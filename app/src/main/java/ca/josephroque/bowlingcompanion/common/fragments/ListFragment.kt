package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.interfaces.IDeletable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.IRefreshable
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Basic [Fragment] implementation with a list.
 */
abstract class ListFragment<Item: IIdentifiable, Adapter: BaseRecyclerViewAdapter<Item>>: BaseFragment(),
        BaseRecyclerViewAdapter.OnAdapterInteractionListener<Item>,
        IRefreshable
{

    companion object {
        /** Logging identifier. */
        private const val TAG = "ListFragment"
    }

    /** Adapter to manage rendering the list of items. */
    protected var adapter: Adapter? = null

    /** Items to display. */
    private var items: MutableList<Item> = ArrayList()

    /** Handle list interaction events. */
    protected var listener: OnListFragmentInteractionListener? = null

    /** Set to true to ignore check for parentFragment listener in onAttach. */
    protected var canIgnoreListener = false

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_common_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            adapter = buildAdapter()

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = adapter
            view.setHasFixedSize(true)
            BaseRecyclerViewAdapter.applyDefaultDivider(view, context)
        }

        return view
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (canIgnoreListener) {
            return
        }

        val parent = parentFragment as? OnListFragmentInteractionListener ?: throw RuntimeException("${parentFragment!!} must implement OnListFragmentInteractionListener")
        listener = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        refreshList()
    }

    /** @Override */
    override fun refresh() {
        refreshList()
    }

    /** @Override */
    override fun onItemClick(item: Item) {
        listener?.onItemSelected(item, false)
    }

    /** @Override */
    override fun onItemDelete(item: Item) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1 && item is IDeletable) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    /** @Override */
    override fun onItemLongClick(item: Item) {
        listener?.onItemSelected(item, true)
    }

    /** @Override */
    override fun onItemSwipe(item: Item) {
        val index = item.indexInList(items)
        if (index != -1 && item is IDeletable) {
            item.isDeleted = !item.isDeleted
            adapter?.notifyItemChanged(index)
        }
    }

    /**
     * Refresh the list of items.
     *
     * @param item if this item is in the list, only it should be updated
     */
    fun refreshList(item: Item? = null) {
        launch(Android) {
            val index = item?.indexInList(this@ListFragment.items) ?: -1
            if (index == -1) {
                val items = fetchItems().await()
                this@ListFragment.items = items
                adapter?.setElements(items)
            } else {
                adapter?.notifyItemChanged(index)
            }
        }
    }

    /**
     * Retrieve a fresh list of [Item] instances.
     *
     * @return list of items
     */
    abstract fun fetchItems(): Deferred<MutableList<Item>>

    /**
     * Build an instance of [Adapter].
     *
     * @return the adapter for the list of items.
     */
    abstract fun buildAdapter(): Adapter

    /**
     * Handle interactions with the list.
     */
    interface OnListFragmentInteractionListener {

        /**
         * Indicates an item has been selected.
         *
         * @param item the item that the user has selected
         * @param longPress true if the item was long pressed, false if it was touched
         */
        fun onItemSelected(item: IIdentifiable, longPress: Boolean)
    }
}