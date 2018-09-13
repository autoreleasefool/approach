package ca.josephroque.bowlingcompanion.transfer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import kotlinx.android.synthetic.main.dialog_base_transfer.toolbar_transfer as toolbar
import kotlinx.android.synthetic.main.dialog_base_transfer.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * DialogFragment to manage data transfer menu.
 */
class BaseTransferDialogFragment : BaseDialogFragment(), TransferMenuFragment.TransferMenuDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseTransferDialogFragment"

        fun newInstance(): BaseTransferDialogFragment {
            return BaseTransferDialogFragment()
        }
    }

    private val onBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
        toolbar.setNavigationIcon(if (childFragmentManager.backStackEntryCount > 0) {
            R.drawable.ic_arrow_back
        } else {
            R.drawable.ic_dismiss
        })

        val transferFragment = childFragmentManager.fragments.last() as? BaseTransferFragment ?: return@OnBackStackChangedListener
        transferFragment.toolbarTitle?.let { toolbar.setTitle(it) }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_base_transfer, container, false)
        setupToolbar(view)

        if (savedInstanceState == null) {
            val fragment = TransferMenuFragment.newInstance()
            childFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragment)
                commit()
            }

            view.toolbar_transfer.setTitle(fragment.toolbarTitle)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        DatabaseHelper.closeInstance()
        childFragmentManager.addOnBackStackChangedListener(onBackStackChangedListener)
    }

    override fun onStop() {
        super.onStop()
        childFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

    private fun setupToolbar(rootView: View) {
        rootView.toolbar_transfer.apply {
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener { _ ->
                (childFragmentManager.fragments.lastOrNull() as? BaseTransferFragment)?.let {
                    if (!it.isBackEnabled) { return@setNavigationOnClickListener }
                }

                if (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                } else {
                    dismiss()
                }
            }
        }
    }

    private fun pushFragment(fragment: BaseFragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            addToBackStack("BaseTransferMenu")
            commit()
        }
    }

    // MARK: TransferMenuDelegate

    override fun showExportScreen() {
        val fragment = TransferExportFragment.newInstance()
        pushFragment(fragment)
    }

    override fun showImportScreen() {
        val fragment = TransferImportFragment.newInstance()
        pushFragment(fragment)
    }

    override fun showRestoreDeleteScreen() {
        val fragment = TransferRestoreDeleteFragment.newInstance()
        pushFragment(fragment)
    }
}
