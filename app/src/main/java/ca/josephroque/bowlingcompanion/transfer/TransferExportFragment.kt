package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.dialog_transfer_export.export_status as exportStatus
import kotlinx.android.synthetic.main.dialog_transfer_export.export_next_step as exportNextStep
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_cancel as cancelButton
import kotlinx.android.synthetic.main.dialog_transfer_export.btn_export as exportButton
import kotlinx.android.synthetic.main.dialog_transfer_export.progress as progressView
import kotlinx.android.synthetic.main.dialog_transfer_export.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to export user's data.
 */
class TransferExportFragment : BaseTransferFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferExportFragment"

        fun newInstance(): TransferExportFragment {
            return TransferExportFragment()
        }
    }

    private var exportJob: Job? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_export -> {
                exportUserData()
            }
            R.id.btn_cancel -> {
                exportJob?.cancel()
                exportJob = null
            }
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.export
    override val isBackEnabled = exportJob == null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_export, container, false)

        view.btn_export.setOnClickListener(onClickListener)
        view.btn_cancel.setOnClickListener(onClickListener)

        return view
    }

    // MARK: Private functions

    private fun getServerConnection(): TransferServerConnection? {
        val context = this@TransferExportFragment.context ?: return null
        return TransferServerConnection.openConnection(context).apply {
            this.progressView = this@TransferExportFragment.progressView
            this.cancelButton = this@TransferExportFragment.cancelButton
        }
    }

    private fun exportFailed() {
        exportButton.visibility = View.VISIBLE
        exportNextStep.visibility = View.GONE
        exportStatus.apply {
            text = resources.getString(R.string.export_upload_failed)
            visibility = View.VISIBLE
        }
    }

    private fun exportSucceeded(serverResponse: String) {
        val requestIdRegex = "requestId:(.*)".toRegex()
        val key = requestIdRegex.matchEntire(serverResponse)?.groups?.get(1)?.value
        if (key == null) {
            exportFailed()
            return
        }

        exportNextStep.visibility = View.VISIBLE
        exportStatus.apply {
            text = resources.getString(R.string.export_upload_complete, key)
            visibility = View.VISIBLE
        }
    }

    private fun exportUserData() {
        exportJob = Job()

        launch(Android) {
            Analytics.trackTransferExport(Analytics.Companion.EventTime.Begin)
            try {
                val parentJob = exportJob ?: return@launch
                val connection = getServerConnection() ?: return@launch

                exportButton.visibility = View.GONE
                exportStatus.visibility = View.GONE

                if (!connection.prepareConnection(parentJob).await()) {
                    exportFailed()
                }

                val serverResponse = connection.uploadUserData(parentJob).await()
                exportJob = null
                if (serverResponse.isNullOrEmpty()) {
                    exportFailed()
                } else {
                    exportSucceeded(serverResponse!!)
                }
            } catch(ex: Exception) {
                exportFailed()
            } finally {
                Analytics.trackTransferExport(Analytics.Companion.EventTime.End)
            }
        }
    }
}
