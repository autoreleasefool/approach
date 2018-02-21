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
        private val TAG = "BowlerFragment"

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
    private var mListener: OnBowlerFragmentInteractionListener? = null
    /** Adapter to manage rendering the list of bowlers. */
    private var mBowlerAdapter: NameAverageRecyclerViewAdapter? = null
    /** Bowlers to display. */
    private var mBowlers: List<Bowler> = ArrayList()

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bowler_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            mBowlerAdapter = NameAverageRecyclerViewAdapter(DummyContent.BOWLERS, this)

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = mBowlerAdapter
            NameAverageRecyclerViewAdapter.applyDefaultDivider(view, context)
        }
        return view
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBowlerFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnBowlerFragmentInteractionListener")
        }
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        refreshBowlerList()
    }

    /**
     * Reload the list of bowlers and update list.
     */
    fun refreshBowlerList() {
        val context = context?: return
        launch(Android) {
            val bowlers = Bowler.fetchAll(context)
            mBowlers = bowlers.await()
            mBowlerAdapter?.setElements(mBowlers)
            mBowlerAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * Handles interaction with the selected bowler.
     *
     * @param item the bowler the user is interacting with
     */
    override fun onNAItemClick(item: INameAverage) {
        if (item is Bowler) {
            mListener?.onBowlerSelected(item)
        }
    }

    /**
     * Deletes the selected bowler.
     *
     * @param item the bowler the user wishes to delete
     */
    override fun onNAItemDelete(item: INameAverage) {
        if (item is Bowler) {

        }
    }

    /** @Override */
    override fun onNAItemLongClick(item: INameAverage) {

    }

    /**
     * Handles interactions with the Bowler list.
     */
    interface OnBowlerFragmentInteractionListener {

        /**
         * Indicates a bowler has been selected and further details should be shown to the user.
         *
         * @param bowler the bowler that the user has selected
         */
        fun onBowlerSelected(bowler: Bowler)
    }
}
