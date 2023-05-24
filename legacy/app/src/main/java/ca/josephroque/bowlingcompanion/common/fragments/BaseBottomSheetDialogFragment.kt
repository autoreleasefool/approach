package ca.josephroque.bowlingcompanion.common.fragments

import android.content.Context
import android.support.design.widget.BottomSheetDialogFragment
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import java.lang.RuntimeException

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Base implementation for BottomSheetDialogFragment
 */
abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseBottomSheetDialogFragment"

        enum class BottomSheetType {
            MatchPlay
        }

        fun getBottomSheetType(fragment: BaseBottomSheetDialogFragment): BottomSheetType? {
            return when (fragment) {
                is MatchPlaySheet -> BottomSheetType.MatchPlay
                else -> null
            }
        }
    }

    var delegate: BaseBottomSheetDialogFragmentDelegate? = null

    // MARK: Lifecycle functions

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? BaseBottomSheetDialogFragmentDelegate ?: throw RuntimeException("${context!!} must implement BaseBottomSheetDialogFragmentDelegate")
        delegate = context
    }

    override fun onDetach() {
        super.onDetach()
        delegate?.bottomSheetDetached()
        delegate = null
    }

    // MARK: BaseBottomSheetDialogFragmentDelegate

    interface BaseBottomSheetDialogFragmentDelegate {
        fun <Delegate> getBottomSheetDelegate(): Delegate?
        fun bottomSheetDetached()
    }
}
