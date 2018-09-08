package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import kotlinx.android.synthetic.main.dialog_transfer_import.import_next_step as importNextStep
import kotlinx.android.synthetic.main.dialog_transfer_import.btn_cancel as cancelButton
import kotlinx.android.synthetic.main.dialog_transfer_import.btn_import as importButton
import kotlinx.android.synthetic.main.dialog_transfer_import.progress as progressView
import kotlinx.android.synthetic.main.dialog_transfer_import.input_key as keyInput
import kotlinx.android.synthetic.main.dialog_transfer_import.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A dialog fragment to import user's data.
 */
class TransferImportDialogFragment : BaseDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferImportDialogFragment"

        fun newInstance(): TransferImportDialogFragment {
            return TransferImportDialogFragment()
        }
    }

    private var importDeferred: Deferred<Boolean?>? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_import -> {
                val code = keyInput.text.toString()
                importUserData(code)
            }
            R.id.btn_cancel -> {
                importDeferred?.cancel()
            }
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_import, container, false)

        setupToolbar(view)
        view.btn_import.setOnClickListener(onClickListener)
        view.btn_cancel.setOnClickListener(onClickListener)

        return view
    }

    override fun onStart() {
        super.onStart()
        DatabaseHelper.closeInstance()

        dialog.setOnKeyListener { _, keyCode, _ ->
            return@setOnKeyListener keyCode == android.view.KeyEvent.KEYCODE_BACK && importDeferred != null
        }
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

    private fun getServerConnection(): TransferServerConnection? {
        val context = this@TransferImportDialogFragment.context ?: return null
        return TransferServerConnection.openConnection(context).apply {
            this.progressView = this@TransferImportDialogFragment.progressView
            this.cancelButton = this@TransferImportDialogFragment.cancelButton
        }
    }

    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setTitle(R.string.data_import)
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun importFailed() {
        importButton.visibility = View.VISIBLE
        importNextStep.visibility = View.GONE
    }

    private fun importSucceeded() {
        importNextStep.visibility = View.VISIBLE
        promptUserToOverrideData()
    }

    private fun importUserData(key: String) {
        launch(Android) {
            val connection = getServerConnection() ?: return@launch
            importButton.visibility = View.GONE

            if (!connection.prepareConnection().await()) {
                importFailed()
            }

            importDeferred = connection.downloadUserData(key)
            if (importDeferred?.await() == true) {
                importSucceeded()
            } else {
                importFailed()
            }
        }
    }

    private fun overwriteData(userData: UserData) {
        launch(Android) {
            importDeferred = async(Android) {
                userData.backup().await()
                userData.overwriteData().await()
            }
            importDeferred?.await()
            importDeferred = null
        }
    }

    private fun deleteDownload(userData: UserData) {
        launch(Android) {
            importDeferred = userData.deleteDownload()
            importDeferred?.await()
            importDeferred = null
        }
    }

    private fun promptUserToOverrideData() {
        val context = context ?: return
        val userData = UserData(context)
        val onClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                overwriteData(userData)
            } else {
                deleteDownload(userData)
            }

            dialog.dismiss()
        }

        AlertDialog.Builder(context)
                .setTitle(R.string.overwrite_data_title)
                .setMessage(R.string.overwrite_data_message)
                .setPositiveButton(R.string.overwrite, onClickListener)
                .setNegativeButton(R.string.cancel, onClickListener)
                .create()
                .show()
    }
}
