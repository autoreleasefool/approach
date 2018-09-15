package ca.josephroque.bowlingcompanion.bowlers

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Window
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_bowler.input_name as nameInput
import kotlinx.android.synthetic.main.dialog_bowler.btn_delete as deleteButton
import kotlinx.android.synthetic.main.dialog_bowler.view.*
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new bowler.
 */
class BowlerDialog : BaseDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BowlerDialog"

        private const val ARG_BOWLER = "${TAG}_BOWLER"

        fun newInstance(bowler: Bowler?): BowlerDialog {
            val dialog = BowlerDialog()
            dialog.arguments = Bundle().apply { bowler?.let { putParcelable(ARG_BOWLER, bowler) } }
            return dialog
        }
    }

    private var bowler: Bowler? = null
    private var delegate: BowlerDialogDelegate? = null

    private val deleteListener = View.OnClickListener {
        safeLet(context, bowler) { context, bowler ->
            AlertDialog.Builder(context)
                    .setTitle(String.format(context.resources.getString(R.string.query_delete_item), bowler.name))
                    .setMessage(R.string.dialog_delete_item_message)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        delegate?.onDeleteBowler(bowler)
                        dismiss()

                        Analytics.trackDeleteBowler()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bowler = arguments?.getParcelable(ARG_BOWLER)

        val rootView = inflater.inflate(R.layout.dialog_bowler, container, false)
        bowler?.let { resetInputs(it, rootView) }
        setupToolbar(rootView)
        setupInput(rootView)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parentFragment = parentFragment as? BowlerDialogDelegate ?: throw RuntimeException("${parentFragment!!} must implement BowlerDialogDelegate")
        delegate = parentFragment
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Requesting input focus and showing keyboard
        nameInput.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(nameInput, InputMethodManager.SHOW_IMPLICIT)

        bowler?.let { deleteButton.visibility = View.VISIBLE }
        nameInput.setSelection(nameInput.text.length)
        updateSaveButton()
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

    private fun setupToolbar(rootView: View) {
        if (bowler == null) {
            rootView.toolbar_bowler.setTitle(R.string.new_bowler)
        } else {
            rootView.toolbar_bowler.setTitle(R.string.edit_bowler)
        }

        rootView.toolbar_bowler.apply {
            inflateMenu(R.menu.dialog_bowler)
            menu.findItem(R.id.action_save).isEnabled = bowler?.name?.isNotEmpty() == true
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveBowler()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun setupInput(rootView: View) {
        rootView.btn_delete.setOnClickListener(deleteListener)
        rootView.input_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })
    }

    private fun canSave() = nameInput.text.isNotEmpty()

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

    private fun saveBowler() {
        launch(Android) {
            this@BowlerDialog.context?.let { context ->
                val name = nameInput.text.toString()

                if (canSave()) {
                    val oldBowler = bowler
                    val (newBowler, error) = if (oldBowler != null) {
                        Bowler.save(context, oldBowler.id, name, oldBowler.average).await()
                    } else {
                        Bowler.save(context, -1, name).await()
                    }

                    if (error != null) {
                        error.show(context)
                        bowler?.let { resetInputs(it) }
                    } else if (newBowler != null) {
                        dismiss()
                        delegate?.onFinishBowler(newBowler)

                        if (oldBowler == null) {
                            Analytics.trackCreateBowler()
                        } else {
                            Analytics.trackEditBowler()
                        }
                    }
                }
            }
        }
    }

    private fun resetInputs(bowler: Bowler, rootView: View? = null) {
        val nameInput = rootView?.input_name ?: this.nameInput
        nameInput.setText(bowler.name)
    }

    // MARK: BowlerDialogDelegate

    interface BowlerDialogDelegate {
        fun onFinishBowler(bowler: Bowler)
        fun onDeleteBowler(bowler: Bowler)
    }
}
