package ca.josephroque.bowlingcompanion.transfer

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import kotlinx.android.synthetic.main.dialog_transfer_import.import_next_step as importNextStep
import kotlinx.android.synthetic.main.dialog_transfer_import.import_status as importStatus
import kotlinx.android.synthetic.main.dialog_transfer_import.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Files
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to import user's data.
 */
class TransferImportFragment : BaseTransferFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferImportFragment"
        private const val DATA_IMPORT_REQUEST = 0

        fun newInstance(): TransferImportFragment {
            return TransferImportFragment()
        }
    }

    private val sqlDbFileSignature = byteArrayOf(
        0x53.toByte(), 0x51.toByte(), 0x4C.toByte(), 0x69.toByte(),
        0x74.toByte(), 0x65.toByte(), 0x20.toByte(), 0x66.toByte(),
        0x6F.toByte(), 0x72.toByte(), 0x6D.toByte(), 0x61.toByte(),
        0x74.toByte(), 0x20.toByte(), 0x33.toByte(), 0x00.toByte()
    )

    private var fileTask: Deferred<Int?>? = null

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_import -> {
                importStatus.visibility = View.GONE
                importUserData()
            }
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.data_import
    override val isBackEnabled = fileTask == null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_import, container, false)

        view.btn_import.setOnClickListener(onClickListener)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            DATA_IMPORT_REQUEST -> handleImportedData(resultCode, data)
        }
    }

    // MARK: Private functions

    private fun importSucceeded() {
        importNextStep.visibility = View.VISIBLE
        importStatus.visibility = View.GONE
    }

    private fun importFailed(error: String) {
        importNextStep.visibility = View.GONE
        importStatus.apply {
            text = error
            visibility = View.VISIBLE
        }
    }

    private fun handleImportedData(resultCode: Int, data: Intent?) {
        Analytics.trackTransferImport(Analytics.Companion.EventTime.End)

        if (resultCode != Activity.RESULT_OK || data?.data == null) {
            importFailed(resources.getString(R.string.error_unknown))
        }

        val uri = data?.data ?: return
        val context = this.context ?: return

        launch(CommonPool) {
            var error: String? = null
            val userData = UserData(context)

            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (!Files.copyFile(inputStream, userData.importFile).await()) {
                    error = resources.getString(R.string.error_data_import_failed)
                }
            } catch (ex: IOException) {
                Log.e(TAG, "Failed to open input stream for import", ex)
                error = resources.getString(R.string.error_data_import_failed)
            } finally {
                try {
                    inputStream?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "Failed to close input stream for import", ex)
                }
            }

            if (error == null && !verifyData(userData.importFile)) {
                error = resources.getString(R.string.error_data_import_invalid)
            }

            launch(Android) {
                if (error != null) {
                    importFailed(error)
                } else {
                    promptUserToOverrideData()
                }
            }
        }
    }

    private fun importUserData() {
        Analytics.trackTransferImport(Analytics.Companion.EventTime.Begin)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            type = "*/*"
        }

        startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.import_from)), DATA_IMPORT_REQUEST)
    }

    private fun overwriteDataWithImport() {
        launch(CommonPool) {
            val context = this@TransferImportFragment.context ?: return@launch
            val userData = UserData(context)

            fileTask = async(CommonPool) {
                DatabaseHelper.closeInstance()

                if (!userData.backupData().await()) {
                    return@async R.string.error_data_backup_failed
                }

                if (!userData.overwriteDataWithImport().await()) {
                    userData.deleteBackup().await()
                    return@async R.string.error_overwrite_data_failed
                }

                userData.deleteImport().await()

                return@async null
            }

            val error = fileTask?.await()
            fileTask = null

            launch(Android) {
                if (error == null) {
                    importSucceeded()
                } else {
                    BCError(R.string.import_error, error, BCError.Severity.Error).show(context)
                }
            }
        }
    }

    private fun deleteImport() {
        launch(Android) {
            val context = this@TransferImportFragment.context ?: return@launch
            val userData = UserData(context)

            fileTask = async(CommonPool) {
                userData.deleteImport().await()
                return@async null
            }
            fileTask?.await()
            fileTask = null
        }
    }

    private fun promptUserToOverrideData() {
        val onClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                overwriteDataWithImport()
            } else {
                importFailed(resources.getString(R.string.error_data_import_cancelled))
                deleteImport()
            }

            dialog.dismiss()
        }

        val context = this.context ?: return
        AlertDialog.Builder(context)
                .setTitle(R.string.overwrite_data_title)
                .setMessage(R.string.overwrite_data_message)
                .setPositiveButton(R.string.overwrite, onClickListener)
                .setNegativeButton(R.string.cancel, onClickListener)
                .create()
                .show()
    }

    private fun verifyData(data: File): Boolean {
        if (!data.exists()) {
            return false
        }

        val fileSignature = ByteArray(16)
        var inputStream: BufferedInputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(data))
            inputStream.read(fileSignature, 0, 16)
        } catch (ex: IOException) {
            Log.e(TAG, "Failed to verify import data", ex)
            return false
        } finally {
            try {
                inputStream?.close()
            } catch (ex: IOException) {
                Log.e(TAG, "Failed to close input stream verifying import data", ex)
            }
        }

        return fileSignature.contentEquals(sqlDbFileSignature)
    }
}
