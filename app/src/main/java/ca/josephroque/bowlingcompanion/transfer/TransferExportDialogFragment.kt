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
import kotlinx.android.synthetic.main.dialog_transfer_export.export_status as exportStatus
import kotlinx.android.synthetic.main.dialog_transfer_export.export_next_step as exportNextStep
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_cancel as cancelButton
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_export as exportButton
import kotlinx.android.synthetic.main.dialog_transfer_export.progress as progressView
import kotlinx.android.synthetic.main.dialog_transfer_export.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A dialog fragment to export user's data.
 */
class TransferExportDialogFragment : BaseDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferExportDialogFragment"

        fun newInstance(): TransferExportDialogFragment {
            return TransferExportDialogFragment()
        }
    }

    private var exportTask: Deferred<String?>? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_export -> {
                exportUserData()
            }
            R.id.btn_cancel -> {
                exportTask?.cancel()
                exportTask = null
            }
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_export, container, false)

        setupToolbar(view)
        view.btn_export.setOnClickListener(onClickListener)
        view.btn_cancel.setOnClickListener(onClickListener)

        return view
    }

    override fun onStart() {
        super.onStart()
        DatabaseHelper.closeInstance()

        dialog.setOnKeyListener { _, keyCode, _ ->
            return@setOnKeyListener keyCode == android.view.KeyEvent.KEYCODE_BACK && exportTask != null
        }
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

    private fun getServerConnection(): TransferServerConnection? {
        val context = this@TransferExportDialogFragment.context ?: return null
        return TransferServerConnection.openConnection(context).apply {
            this.progressView = this@TransferExportDialogFragment.progressView
            this.cancelButton = this@TransferExportDialogFragment.cancelButton
        }
    }

    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setTitle(R.string.export)
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun exportFailed() {
        exportButton.visibility = View.VISIBLE
        exportStatus.visibility = View.GONE
        exportNextStep.visibility = View.GONE
    }

    private fun exportSucceeded(key: String) {
        exportNextStep.visibility = View.VISIBLE
        exportStatus.apply {
            text = resources.getString(R.string.export_upload_complete, key)
            visibility = View.VISIBLE
        }
    }

    private fun exportUserData() {
        launch(Android) {
            val connection = getServerConnection() ?: return@launch
            exportButton.visibility = View.GONE

            if (!connection.prepareConnection().await()) {
                exportFailed()
            }

            exportTask = connection.uploadUserData()
            val key = exportTask?.await()
            exportTask = null
            if (key == null) {
                exportFailed()
            } else {
                exportSucceeded(key)
            }
        }
    }
}
