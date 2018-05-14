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
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_bowler.*
import kotlinx.android.synthetic.main.dialog_bowler.view.*
import kotlinx.coroutines.experimental.launch


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new bowler.
 */
class BowlerDialog : DialogFragment(), View.OnClickListener {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "BowlerDialog"

        /** Identifier for the [Bowler] to be edited. */
        private const val ARG_BOWLER = "${TAG}_BOWLER"

        /**
         * Create a new instance of the dialog.
         *
         * @param bowler [Bowler] to edit, or null to create a new bowler
         */
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bowler = arguments?.getParcelable(ARG_BOWLER) ?: savedInstanceState?.getParcelable(ARG_BOWLER)

        val rootView = inflater.inflate(R.layout.dialog_bowler, container, false)
        setupToolbar(rootView)
        setupInput(rootView)
        return rootView
    }

    /**
     * Set up title, style, and listeners for toolbar.
     *
     * @param rootView the root view
     */
    private fun setupToolbar(rootView: View) {
        if (bowler == null) {
            rootView.toolbar_bowler.setTitle(R.string.new_bowler)
        } else {
            rootView.toolbar_bowler.setTitle(R.string.edit_bowler)
        }

        rootView.toolbar_bowler.apply {
            inflateMenu(R.menu.menu_dialog_bowler)
            menu.findItem(R.id.action_save).isEnabled = bowler?.name?.isNotEmpty() == true
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveBowler()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    /**
     * Set up input items for callbacks on interactions.
     *
     * @param rootView the root view
     */
    private fun setupInput(rootView: View) {
        rootView.btn_delete.setOnClickListener(this)
        rootView.input_name.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parentFragment = parentFragment as? OnBowlerDialogInteractionListener ?: throw RuntimeException("${parentFragment!!} must implement OnBowlerDialogInteractionListener")
        listener = parentFragment
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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
        updateSaveButton()
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
     * Checks if the bowler can be saved or not.
     *
     * @return true if the bowler name is not empty, false otherwise
     */
    private fun canSave(): Boolean {
        return input_name.text.isNotEmpty()
    }

    /**
     * Update save button state based on text entered.
     */
    private fun updateSaveButton() {
        val saveButton = view?.toolbar_bowler?.menu?.findItem(R.id.action_save)
        if (canSave()) {
            saveButton?.isEnabled = true
            saveButton?.icon?.alpha = Color.ALPHA_ENABLED
        } else {
            saveButton?.isEnabled = false
            saveButton?.icon?.alpha = Color.ALPHA_DISABLED
        }
    }

    /**
     * Save the current bowler. Show errors if there are any.
     */
    private fun saveBowler() {
        launch(Android) {
            this@BowlerDialog.context?.let {
                val oldName = bowler?.name ?: ""
                val name = input_name.text.toString()

                if (canSave()) {
                    val newBowler = bowler ?: Bowler(-1, name, 0.0)
                    newBowler.name = name

                    val error = newBowler.save(it).await()
                    if (error != null) {
                        error.show(it)
                        newBowler.name = oldName
                        input_name.setText(oldName)
                    } else {
                        dismiss()
                        listener?.onFinishBowler(newBowler)
                    }
                }
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

        /**
         * Indicates the user wishes to delete the [Bowler].
         *
         * @param bowler the deleted [Bowler]
         */
        fun onDeleteBowler(bowler: Bowler)
    }
}
