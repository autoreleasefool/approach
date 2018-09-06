package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_transfer_restore_delete.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A dialog fragment to restore or delete the user's data.
 */
class TransferRestoreDeleteDialogFragment : BaseDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "TransferResDelFragment"

        fun newInstance(): TransferRestoreDeleteDialogFragment {
            return TransferRestoreDeleteDialogFragment()
        }
    }

    private val onClickListener = View.OnClickListener {
        TODO("restore or delete")
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_transfer_restore_delete, container, false)

        setupToolbar(view)
        view.btn_restore.setOnClickListener(onClickListener)
        view.btn_delete.setOnClickListener(onClickListener)

        return view
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setTitle(R.string.restore_or_delete)
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }
}
