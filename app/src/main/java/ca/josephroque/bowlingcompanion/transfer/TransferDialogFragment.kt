package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_transfer.view.*
import java.lang.IllegalArgumentException

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to enable transferring the user's data to a new device, or transferring from another device to their current one.
 */
class TransferDialogFragment : BaseDialogFragment() {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "TransferDialogFragment"

        /**
         * Create a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): TransferDialogFragment {
            return TransferDialogFragment()
        }
    }

    private val onClickListener = View.OnClickListener {
        val newFragment = when (it.id) {
            R.id.btn_export -> TransferExportDialogFragment.newInstance()
            R.id.btn_import -> TransferImportDialogFragment.newInstance()
            R.id.btn_restore_delete -> TransferRestoreDeleteDialogFragment.newInstance()
            else -> throw IllegalArgumentException("$TAG: button not set up in onClickListener")
        }

        fragmentNavigation?.pushDialogFragment(newFragment)
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer, container, false)

        setupToolbar(view)
        view.btn_export.setOnClickListener(onClickListener)
        view.btn_import.setOnClickListener(onClickListener)
        view.btn_restore_delete.setOnClickListener(onClickListener)

        return view
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    /**
     * Set up title, style, and listeners for toolbar.
     *
     * @param rootView the root view
     */
    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setTitle(R.string.transfer_data)
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }
}
