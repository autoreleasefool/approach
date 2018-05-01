package ca.josephroque.bowlingcompanion.common.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.IIdentifiable
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Basic [Fragment] implementation with a list.
 */
abstract class ListFragment<Item: IIdentifiable, ViewHolder: RecyclerView.ViewHolder, Adapter: BaseRecyclerViewAdapter<Item, ViewHolder>>: BaseFragment(),
        BaseRecyclerViewAdapter.OnAdapterInteractionListener<Item>
{

    companion object {
        /** Logging identifier. */
        private const val TAG = "ListFragment"
    }

    /** Adapter to manage rendering the list of items. */
    protected var adapter: Adapter? = null

    /** Items to display. */
    protected var items: MutableList<Item> = ArrayList()

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
    override fun onResume() {
        super.onResume()
        refreshList()
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
}