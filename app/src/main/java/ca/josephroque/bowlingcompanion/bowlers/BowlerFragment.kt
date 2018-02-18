package ca.josephroque.bowlingcompanion.bowlers

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.dummy.DummyContent
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.INameAverage

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Bowlers.
 */
class BowlerFragment : Fragment(), NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener {

    private var mListener: OnBowlerFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bowler_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = NameAverageRecyclerViewAdapter(DummyContent.BOWLERS, this)
            NameAverageRecyclerViewAdapter.applyDefaultDivider(view, context)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBowlerFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnBowlerFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onNAItemClick(item: INameAverage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNAItemDelete(item: INameAverage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNAItemLongClick(item: INameAverage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Handles interactions with the Bowler list.
     */
    interface OnBowlerFragmentInteractionListener {
        fun onBowlerSelected(bowler: Bowler)
    }

    companion object {
        fun newInstance(): BowlerFragment {
            return BowlerFragment()
        }
    }
}
