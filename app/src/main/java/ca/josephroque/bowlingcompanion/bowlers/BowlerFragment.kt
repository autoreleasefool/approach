package ca.josephroque.bowlingcompanion.bowlers

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.INameAverage
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.dummy.DummyContent
import ca.josephroque.bowlingcompanion.common.Android
import kotlinx.coroutines.experimental.launch


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Bowlers.
 */
class BowlerFragment : Fragment(), NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BowlerFragment"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): BowlerFragment {
            return BowlerFragment()
        }
    }

    /** Interaction handler. */
    private var listener: OnBowlerFragmentInteractionListener? = null
    /** Adapter to manage rendering the list of bowlers. */
    private var bowlerAdapter: NameAverageRecyclerViewAdapter? = null
    /** Bowlers to display. */
    private var bowlers: List<Bowler> = ArrayList()

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bowler_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            bowlerAdapter = NameAverageRecyclerViewAdapter(DummyContent.BOWLERS, this)
            bowlerAdapter?.swipingEnabled = true

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = bowlerAdapter
            view.setHasFixedSize(true)
            NameAverageRecyclerViewAdapter.applyDefaultDivider(view, context)
        }
        return view
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnBowlerFragmentInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnBowlerFragmentInteractionListener")
        listener = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        refreshBowlerList(null)
    }

    /**
     * Reload the list of bowlers and update list.
     *
     * @param bowler if the bowler exists in the list only that entry will be updated
     */
    fun refreshBowlerList(bowler: Bowler?) {
        val context = context?: return
        launch(Android) {
            val index = bowler?.indexInList(this@BowlerFragment.bowlers) ?: -1
            if (index == -1) {
                val bowlers = Bowler.fetchAll(context).await()
                this@BowlerFragment.bowlers = bowlers
                bowlerAdapter?.setElements(this@BowlerFragment.bowlers)
            } else {
                bowlerAdapter?.setElementAt(index, bowler!!)
            }
        }
    }

    /**
     * Handles interaction with the selected bowler.
     *
     * @param item the bowler the user is interacting with
     */
    override fun onNAItemClick(item: INameAverage) {
        if (item is Bowler) {
            listener?.onBowlerSelected(item, false)
        }
    }

    /**
     * Deletes the selected bowler.
     *
     * @param item the bowler the user wishes to delete
     */
    override fun onNAItemDelete(item: INameAverage) {
        if (item is Bowler) {
            TODO("Delete bowler")
        }
    }

    /**
     * Sets the bowler to be deleted or active again (toggles from current state).
     *
     * @param item the bowler to delete or activate
     */
    override fun onNAItemSwipe(item: INameAverage) {
        if (item is Bowler) {
            val index = item.indexInList(bowlers)
            if (index != -1) {
                item.isDeleted = !item.isDeleted
                bowlerAdapter?.setElementAt(index, item)
            }
        }
    }

    /**
     * Shows option to edit the selected bowler.
     *
     * @param item the bowler the user wishes to edit
     */
    override fun onNAItemLongClick(item: INameAverage) {
        if (item is Bowler) {
            listener?.onBowlerSelected(item, true)
        }
    }

    /**
     * Handles interactions with the Bowler list.
     */
    interface OnBowlerFragmentInteractionListener {

        /**
         * Indicates a bowler has been selected and further details should be shown to the user.
         *
         * @param bowler the bowler that the user has selected
         * @param toEdit indicate if the user's intent is to edit the [Bowler] or select
         */
        fun onBowlerSelected(bowler: Bowler, toEdit: Boolean)
    }
}
