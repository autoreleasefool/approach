package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_transfer_export.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A dialog fragment to export user's data.
 */
class TransferExportDialogFragment : BaseDialogFragment() {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "TransferExportDialogFragment"

        /**
         * Create a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): TransferExportDialogFragment {
            return TransferExportDialogFragment()
        }
    }

    private val onClickListener = View.OnClickListener {
        TODO("Begin export")
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_export, container, false)

        setupToolbar(view)
        view.btn_export.setOnClickListener(onClickListener)

        return view
    }

    /**
     * Set up title, style, and listeners for toolbar.
     *
     * @param rootView the root view
     */
    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setTitle(R.string.export)
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }
}
