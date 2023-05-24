package ca.josephroque.bowlingcompanion.transfer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.android.synthetic.main.dialog_transfer_export.view.btn_export
import kotlinx.coroutines.experimental.CommonPool
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

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_export -> {
                exportUserData()
            }
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.export
    override val isBackEnabled = true

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_export, container, false)

        view.btn_export.setOnClickListener(onClickListener)

        return view
    }

    // MARK: Private functions

    private fun exportUserData() {
        launch(CommonPool) {
            val context = this@TransferExportFragment.context ?: return@launch
            val userData = UserData(context)

            if (!userData.exportData().await()) {
                launch(Android) {
                    BCError(R.string.export_error, R.string.error_data_export_failed, BCError.Severity.Error).show(context)
                }
                return@launch
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.transfer.TransferExportFileProvider",
                userData.exportFile)

            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.type = "application/octet-stream"
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)

            launch(Android) {
                startActivity(intent)
            }
        }
    }
}
