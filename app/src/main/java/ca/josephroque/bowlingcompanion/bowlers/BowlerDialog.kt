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
        private const val TAG = "BowlerDialog"

        /** Identifier for the [Bowler] to be edited. */
        private const val ARG_BOWLER = "${TAG}_BOWLER"

        fun newInstance(bowler: Bowler?): BowlerDialog {
            val dialog = BowlerDialog()
            val args = Bundle()
            bowler?.let { args.putParcelable(ARG_BOWLER, bowler) }
            dialog.arguments = args
            return dialog
        }
    }

    /** Bowler to be edited, or null if a new bowler is to be created. */
    private var bowler: Bowler? = null

    /** Interaction handler. */
    private var listener: OnBowlerDialogInteractionListener? = null

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bowler = arguments?.getParcelable(ARG_BOWLER) ?: savedInstanceState?.getParcelable(ARG_BOWLER)

        val rootView = inflater.inflate(R.layout.dialog_bowler, container, false)

        if (bowler == null) {
            rootView.toolbar_bowler.setTitle(R.string.new_bowler)
        } else {
            rootView.toolbar_bowler.setTitle(R.string.edit_bowler)
        }

        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(rootView.toolbar_bowler)

        activity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        setHasOptionsMenu(true)
        return rootView
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnBowlerDialogInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnBowlerDialogInteractionListener")
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

        // Requesting input focus and showing keyboard
        input_name.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input_name, InputMethodManager.SHOW_IMPLICIT)

        bowler?.let { btn_delete.visibility = View.VISIBLE }
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_BOWLER, bowler)
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

        activity?.supportFragmentManager?.popBackStack()
        dismiss()

        return when (item.itemId) {
            R.id.action_save -> {
                if (bowler == null) {
                    listener?.onFinishBowler(Bowler(input_name.text.toString(), 0.0, -1))
                } else {
                    listener?.onFinishBowler(Bowler(input_name.text.toString(), bowler!!.average, bowler!!.id))
                }
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
         * Indicates when the user has finished editing the [Bowler]
         *
         * @param bowler the finished [Bowler]
         */
        fun onFinishBowler(bowler: Bowler)
    }
}
