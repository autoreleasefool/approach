package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_transfer_dialog.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment to enable transferring the user's data to a new device, or transferring from another device to their current one.
 */
class TransferDialogFragment : BaseDialogFragment(),
        View.OnClickListener {

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

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_dialog, container, false)

        setupToolbar(view)
        view.btn_export.setOnClickListener(this)
        view.btn_import.setOnClickListener(this)
        view.btn_restore_delete.setOnClickListener(this)

        return view
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

    // MARK: OnClickListener

    /** @Override */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_export -> {
                TODO("Show export menu")
            }
            R.id.btn_import -> {
                TODO("Show import menu")
            }
            R.id.btn_restore_delete -> {
                TODO("Show restore/delete menu")
            }
        }
    }

}
