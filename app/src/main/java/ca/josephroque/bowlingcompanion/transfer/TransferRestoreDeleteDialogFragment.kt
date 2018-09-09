package ca.josephroque.bowlingcompanion.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.android.synthetic.main.dialog_transfer_restore_delete.tv_no_backup as noBackupText
import kotlinx.android.synthetic.main.dialog_transfer_restore_delete.btn_restore as restoreButton
import kotlinx.android.synthetic.main.dialog_transfer_restore_delete.btn_delete as deleteButton
import kotlinx.android.synthetic.main.dialog_transfer_restore_delete.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

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

    private var fileTask: Deferred<Int?>? = null

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_restore -> {
                promptRestoreBackup()
            }
            R.id.btn_delete -> {
                promptDeleteBackup()
            }
        }
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

    override fun onStart() {
        super.onStart()
        DatabaseHelper.closeInstance()

        dialog.setOnKeyListener { _, keyCode, _ ->
            return@setOnKeyListener keyCode == android.view.KeyEvent.KEYCODE_BACK && fileTask != null
        }

        updateButtons()
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

    private fun updateButtons() {
        val context = context
        if (context != null) {
            val userData = UserData(context)
            if (userData.backupFile.exists()) {
                noBackupText.visibility = View.GONE
                restoreButton.isEnabled = true
                deleteButton.isEnabled = true
                return
            }
        }

        noBackupText.visibility = View.VISIBLE
        restoreButton.isEnabled = false
        deleteButton.isEnabled = false
    }

    private fun promptDeleteBackup() {
        val context = context ?: return
        val onClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                deleteBackup()
            }

            dialog.dismiss()
        }

        AlertDialog.Builder(context)
                .setTitle(R.string.delete_backup_title)
                .setMessage(R.string.delete_backup_message)
                .setPositiveButton(R.string.delete, onClickListener)
                .setNegativeButton(R.string.cancel, onClickListener)
                .create()
                .show()
    }

    private fun deleteBackup() {
        val context = context ?: return
        val userData = UserData(context)
        launch(Android) {
            fileTask = async(CommonPool) {
                return@async if (userData.deleteBackup().await()) null else R.string.error_delete_backup_failed_message
            }
            val error = fileTask?.await()
            fileTask = null

            if (error != null) {
                BCError(R.string.error_delete_backup_failed_title, error, BCError.Severity.Error).show(context)
            }

            updateButtons()
        }
    }

    private fun promptRestoreBackup() {
        val context = context ?: return
        val onClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                restoreBackup()
            }

            dialog.dismiss()
        }

        AlertDialog.Builder(context)
                .setTitle(R.string.restore_backup_title)
                .setMessage(R.string.restore_backup_message)
                .setPositiveButton(R.string.restore, onClickListener)
                .setNegativeButton(R.string.cancel, onClickListener)
                .create()
                .show()
    }

    private fun restoreBackup() {
        val context = context ?: return
        val userData = UserData(context)
        launch(Android) {
            fileTask = async(CommonPool) {
                return@async if (userData.restoreBackup().await()) null else R.string.error_restore_backup_failed_message
            }
            val error = fileTask?.await()
            fileTask = null

            if (error != null) {
                BCError(R.string.error_restore_backup_failed_title, error, BCError.Severity.Error).show(context)
            }

            updateButtons()
        }
    }
}
