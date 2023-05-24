package ca.josephroque.bowlingcompanion.transfer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.dialog_transfer.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to enable transferring the user's data to a new device, or transferring from another device to their current one.
 */
class TransferMenuFragment : BaseTransferFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferMenuFragment"

        fun newInstance(): TransferMenuFragment {
            return TransferMenuFragment()
        }
    }

    private var delegate: TransferMenuDelegate? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_export -> delegate?.showExportScreen()
            R.id.btn_import -> delegate?.showImportScreen()
            R.id.btn_restore_delete -> delegate?.showRestoreDeleteScreen()
        }
    }

    // MARK: BaseTransferFragment

    override val toolbarTitle = R.string.transfer_data
    override val isBackEnabled = true

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer, container, false)

        view.btn_export.setOnClickListener(onClickListener)
        view.btn_import.setOnClickListener(onClickListener)
        view.btn_restore_delete.setOnClickListener(onClickListener)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        delegate = parentFragment as? TransferMenuDelegate ?: throw RuntimeException("${parentFragment!!} must implement TransferMenuDelegate")
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: TransferMenuDelegate

    interface TransferMenuDelegate {
        fun showExportScreen()
        fun showImportScreen()
        fun showRestoreDeleteScreen()
    }
}
