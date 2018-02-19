package ca.josephroque.bowlingcompanion.bowlers

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.inputmethod.InputMethodManager
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.dialog_bowler.*
import kotlinx.android.synthetic.main.dialog_bowler.view.*


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new bowler.
 */
class BowlerDialog : DialogFragment() {

    companion object {
        /** Logging identifier. */
        private val TAG = "BowlerDialog"

        /** Identifier for the [Bowler] to be edited. */
        private val ARG_BOWLER = "arg_bowler"

        fun newInstance(bowler: Bowler?): BowlerDialog {
            val dialog = BowlerDialog()
            val args = Bundle()

            if (bowler != null) {
                args.putParcelable(ARG_BOWLER, bowler)
            }

            dialog.arguments = args
            return dialog
        }
    }

    /** Bowler to be edited, or null if a new bowler is to be created. */
    private var mBowler: Bowler? = null

    /** Interaction handler. */
    private var mListener: OnBowlerDialogInteractionListener? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBowler = arguments?.getParcelable(ARG_BOWLER) ?: savedInstanceState?.getParcelable(ARG_BOWLER)

        val rootView = inflater.inflate(R.layout.dialog_bowler, container, false)

        if (mBowler == null) {
            rootView.toolbar_bowler.setTitle(R.string.new_bowler)
        } else {
            rootView.toolbar_bowler.setTitle(R.string.edit_bowler)
        }

        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(rootView.toolbar_bowler)

        val actionBar = activity?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        setHasOptionsMenu(true)
        return rootView
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBowlerDialogInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnBowlerDialogInteractionListener")
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

        // Requesting input focus and showing keyboard
        input_name.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input_name, InputMethodManager.SHOW_IMPLICIT)

        if (mBowler != null) {
            btn_delete.visibility = View.VISIBLE
        }
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_BOWLER, mBowler)
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_bowler_dialog, menu)
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        App.hideSoftKeyBoard(activity!!)
        dismiss()

        return when (item.itemId) {
            R.id.action_save -> {
                mListener?.onCreateBowler(input_name.text.toString())
                true
            }
            else -> {
                true
            }
        }
    }

    /**
     * Handles interactions with the dialog.
     */
    interface OnBowlerDialogInteractionListener {

        /**
         * Indicates when the user has prompted to create a new [Bowler]
         *
         * @param name the name for their new [Bowler]
         */
        fun onCreateBowler(name: String)
    }
}