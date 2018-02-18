package ca.josephroque.bowlingcompanion.bowlers

import android.app.Dialog
import android.content.Context
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.dialog_new_bowler.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import ca.josephroque.bowlingcompanion.App
import kotlinx.android.synthetic.main.dialog_new_bowler.view.*
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT




/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new bowler.
 */
class NewBowlerDialog : DialogFragment() {

    companion object {
        private val TAG = "NewBowlerDialog"
    }

    private var mListener: OnNewBowlerInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_new_bowler, container, false)
        rootView.toolbar_new_bowler.setTitle(R.string.new_bowler)

        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(rootView.toolbar_new_bowler)

        val actionBar = activity?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        setHasOptionsMenu(true)
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNewBowlerInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNewBowlerInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onResume() {
        super.onResume()

        // Requesting input focus and showing keyboard
        input_name.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input_name, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_bowler, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        App.hideSoftKeyBoard(activity!!)
        dismiss()

        return when (item.itemId) {
            R.id.action_save -> {
                mListener?.createBowler(input_name.text.toString())
                true
            }
            else -> {
                true
            }
        }
    }

    interface OnNewBowlerInteractionListener {
        fun createBowler(name: String)
    }
}