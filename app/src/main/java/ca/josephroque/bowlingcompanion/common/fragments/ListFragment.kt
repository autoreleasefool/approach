package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.interfaces.IDeletable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.IRefreshable
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_common_list.view.*
import kotlinx.android.synthetic.main.fragment_common_list.list as list
import kotlinx.android.synthetic.main.fragment_common_list.list_empty_view as emptyView
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Basic [Fragment] implementation with a list.
 */
abstract class ListFragment<Item : IIdentifiable, Adapter : BaseRecyclerViewAdapter<Item>> :
        BaseFragment(),
        BaseRecyclerViewAdapter.AdapterDelegate<Item>,
        IRefreshable {

    companion object {
        @Suppress("unused")
        private const val TAG = "ListFragment"
    }

    protected var adapter: Adapter? = null
    private var items: MutableList<Item> = ArrayList()

    protected var delegate: ListFragmentDelegate? = null
    protected var canIgnoreDelegate = false

    abstract val emptyViewImage: Int?
    abstract val emptyViewText: Int?

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_list, container, false)
        setupRecyclerView(rootView)
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (canIgnoreDelegate) {
            return
        }

        val parent = parentFragment as? ListFragmentDelegate ?: throw RuntimeException("${parentFragment!!} must implement ListFragmentDelegate")
        delegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun onStart() {
        super.onStart()
        refreshList()
    }

    override fun refresh() {
        refreshList()
    }

    // MARK: AdapterDelegate

    override fun onItemClick(item: Item) {
        delegate?.onItemSelected(item, false)
    }

    override fun onItemDelete(item: Item) {
        val context = context ?: return
        val index = item.indexInList(items)
        if (index != -1 && item is IDeletable) {
            items.removeAt(index)
            adapter?.notifyItemRemoved(index)
            item.delete(context)
        }
    }

    override fun onItemLongClick(item: Item) {
        delegate?.onItemSelected(item, true)
    }

    override fun onItemSwipe(item: Item) {
        val index = item.indexInList(items)
        if (index != -1 && item is IDeletable) {
            @Suppress("UNCHECKED_CAST")
            val updatedItem: Item = if (item.isDeleted) {
                item.cleanDeletion() as Item
            } else {
                item.markForDeletion() as Item
            }

            items[index] = updatedItem
            adapter?.notifyItemChanged(adapter?.getPositionOfItem(item) ?: index)
        }
    }

    // MARK: ListFragment

    abstract fun fetchItems(): Deferred<MutableList<Item>>

    abstract fun buildAdapter(): Adapter

    open fun listWasRefreshed() {}

    fun refreshList(item: Item? = null) {
        launch(Android) {
            val index = item?.indexInList(this@ListFragment.items) ?: -1
            if (item == null || index == -1) {
                val items = fetchItems().await()
                this@ListFragment.items = items
                adapter?.items = items
            } else {
                this@ListFragment.items[index] = item
                adapter?.notifyItemChanged(adapter?.getPositionOfItem(item) ?: index)
            }

            updateEmptyView()
            listWasRefreshed()
        }
    }

    // MARK: Private functions

    private fun setupRecyclerView(rootView: View) {
        val context = context ?: return
        adapter = buildAdapter()
        rootView.list.layoutManager = LinearLayoutManager(context)
        rootView.list.adapter = adapter
        rootView.list.setHasFixedSize(true)
        BaseRecyclerViewAdapter.applyDefaultDivider(rootView.list, context)
    }

    private fun updateEmptyView() {
        if (items.isEmpty()) {
            list.visibility = View.GONE
            emptyView.apply {
                emptyImageId = this@ListFragment.emptyViewImage
                emptyTextId = this@ListFragment.emptyViewText
                visibility = View.VISIBLE
            }
        } else {
            list.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    // MARK: ListFragmentDelegate

    interface ListFragmentDelegate {
        fun onItemSelected(item: IIdentifiable, longPress: Boolean)
    }
}
