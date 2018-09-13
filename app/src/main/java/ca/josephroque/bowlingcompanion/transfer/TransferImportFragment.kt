package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
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
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to import user's data.
 */
class TransferImportFragment : BaseTransferFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferImportFragment"

        fun newInstance(): TransferImportFragment {
            return TransferImportFragment()
        }
    }

    private var fileTask: Deferred<Int?>? = null
    private var importJob: Job? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_import -> {
                val code = keyInput.text.toString()
                importUserData(code)
            }
            R.id.btn_cancel -> {
                importJob?.cancel()
                importJob = null
            }
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.data_import
    override val isBackEnabled = importJob == null && fileTask == null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_import, container, false)

        view.btn_import.setOnClickListener(onClickListener)
        view.btn_cancel.setOnClickListener(onClickListener)

        return view
    }

    // MARK: Private functions

    private fun getServerConnection(): TransferServerConnection? {
        val context = this@TransferImportFragment.context ?: return null
        return TransferServerConnection.openConnection(context).apply {
            this.progressView = this@TransferImportFragment.progressView
            this.cancelButton = this@TransferImportFragment.cancelButton
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
        importJob = Job()

        launch(Android, parent = importJob) {
            val parentJob = importJob ?: return@launch
            val connection = getServerConnection() ?: return@launch
            Analytics.trackTransferImport(Analytics.Companion.EventTime.Begin)
            importButton.visibility = View.GONE

            if (!connection.prepareConnection(parentJob).await()) {
                importFailed()
            }

            if (connection.downloadUserData(key, parentJob).await()) {
                importSucceeded()
            } else {
                importFailed()
            }

            Analytics.trackTransferImport(Analytics.Companion.EventTime.End)
        }
    }

    private fun overwriteData(userData: UserData) {
        val context = this@TransferImportFragment.context ?: return
        launch(Android) {
            fileTask = async(CommonPool) {
                if (!userData.backup().await()) {
                    return@async R.string.error_data_backup_failed
                }
                if (!userData.overwriteData().await()) {
                    userData.deleteBackup().await()
                    return@async R.string.error_overwrite_data_failed
                }

                userData.deleteDownload().await()

                return@async null
            }

            val error = fileTask?.await()
            fileTask = null

            if (error != null) {
                BCError(R.string.import_error, error, BCError.Severity.Error).show(context)
            }
        }
    }

    private fun deleteDownload(userData: UserData) {
        launch(Android) {
            fileTask = async(CommonPool) {
                userData.deleteDownload().await()
                return@async null
            }
            fileTask?.await()
            fileTask = null
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
