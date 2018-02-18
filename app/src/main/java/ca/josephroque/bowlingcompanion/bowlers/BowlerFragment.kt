package ca.josephroque.bowlingcompanion.bowlers

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.dummy.DummyContent
import ca.josephroque.bowlingcompanion.common.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.INameAverage
import android.view.*
import ca.josephroque.bowlingcompanion.BowlerTeamListActivity
import ca.josephroque.bowlingcompanion.utils.Android
import kotlinx.coroutines.experimental.*


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of Bowlers.
 */
class BowlerFragment : Fragment(), NameAverageRecyclerViewAdapter.OnNameAverageInteractionListener {

    companion object {
        private val TAG = "BowlerFragment"
        fun newInstance(): BowlerFragment {
            return BowlerFragment()
        }
    }

    private var mListener: OnBowlerFragmentInteractionListener? = null
    private var mBowlerAdapter: NameAverageRecyclerViewAdapter? = null

    private var mEditingBowlers: Boolean = false
        private set(value) {
            val activity: BowlerTeamListActivity = activity as? BowlerTeamListActivity ?: return
            if (value) {
                activity.hideFab(BowlerTeamListActivity.PRIMARY)
            } else {
                activity.showFab(BowlerTeamListActivity.PRIMARY)
            }

            activity.invalidateOptionsMenu()
        }

    /** Bowlers to display. */
    private var mBowlers: List<Bowler> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bowler_list, container, false)

        if (view is RecyclerView) {
            val context = view.getContext()
            mBowlerAdapter = NameAverageRecyclerViewAdapter(DummyContent.BOWLERS, this)

            view.adapter = mBowlerAdapter
            view.layoutManager = LinearLayoutManager(context)
            NameAverageRecyclerViewAdapter.applyDefaultDivider(view, context)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bowlers, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_edit)?.isVisible = !mEditingBowlers
        menu.findItem(R.id.action_done).isVisible = mEditingBowlers
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                mEditingBowlers = true
                return true
            }
            R.id.action_done -> {
                mEditingBowlers = false
                return true
            }
            R.id.action_transfer -> {
//                if (mBowlerCallback != null)
//                    mBowlerCallback.onDataTransferSelected()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

    override fun onResume() {
        super.onResume()
        mEditingBowlers = false

        val context = context?: return
        launch(Android) {
            val bowlers = Bowler.fetchAll(context)
            mBowlers = bowlers.await()
            mBowlerAdapter?.setElements(mBowlers)
            mBowlerAdapter?.notifyDataSetChanged()
        }
    }

    override fun onNAItemClick(item: INameAverage) {
        if (item is Bowler) {
            mListener?.onBowlerSelected(item)
        }
    }

    override fun onNAItemDelete(item: INameAverage) {
        if (item is Bowler) {

        }
    }

    override fun onNAItemLongClick(item: INameAverage) {}

    /**
     * Handles interactions with the Bowler list.
     */
    interface OnBowlerFragmentInteractionListener {
        fun onBowlerSelected(bowler: Bowler)
    }
}
