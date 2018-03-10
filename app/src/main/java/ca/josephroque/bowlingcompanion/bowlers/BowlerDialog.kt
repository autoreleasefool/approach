package ca.josephroque.bowlingcompanion.bowlers

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_bowler.*
import kotlinx.android.synthetic.main.dialog_bowler.view.*


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new bowler.
 */
class BowlerDialog : DialogFragment(), View.OnClickListener {

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

        rootView.btn_delete.setOnClickListener(this)
        rootView.toolbar_bowler.apply {
            inflateMenu(R.menu.menu_bowler_dialog)
            menu.findItem(R.id.action_save).isEnabled = bowler?.name?.isNotEmpty() == true
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        val name = this@BowlerDialog.view?.input_name?.text.toString()
                        if (name.isNotEmpty()) {
                            dismiss()
                            if (bowler == null) {
                                listener?.onFinishBowler(Bowler(name, 0.0, -1))
                            } else {
                                listener?.onFinishBowler(Bowler(name, bowler!!.average, bowler!!.id))
                            }
                        }

                        true
                    }
                    else -> false
                }
            }
        }
        rootView.input_name.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton(s)
            }
        })

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

        bowler?.let {
            btn_delete.visibility = View.VISIBLE
            input_name.setText(it.name)
        }

        input_name.setSelection(input_name.text.length)
        updateSaveButton(bowler?.name)
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
    override fun onClick(v: View?) {
        safeLet(context, bowler) { context, bowler ->
            AlertDialog.Builder(context)
                    .setTitle(String.format(context.resources.getString(R.string.query_delete_item), bowler.name))
                    .setMessage(R.string.dialog_delete_item_message)
                    .setPositiveButton(R.string.delete, { _, _ ->
                        listener?.onDeleteBowler(bowler)
                        dismiss()
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    /**
     * Clean up dialog before calling super.
     */
    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    /**
     * Update save button state based on text entered.
     *
     * @param text the text entered
     */
    private fun updateSaveButton(text: CharSequence?) {
        val saveButton = view?.toolbar_bowler?.menu?.findItem(R.id.action_save)
        if (text?.isNotEmpty() == true) {
            saveButton?.isEnabled = true
            saveButton?.icon?.alpha = Color.ALPHA_ENABLED
        } else {
            saveButton?.isEnabled = false
            saveButton?.icon?.alpha = Color.ALPHA_DISABLED
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

        /**
         * Indicates the user wishes to delete the [Bowler].
         *
         * @param bowler the deleted [Bowler]
         */
        fun onDeleteBowler(bowler: Bowler)
    }
}
