package ca.josephroque.bowlingcompanion.transfer

import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Declares values which transfer menu fragments must provide
 */
abstract class BaseTransferFragment : BaseFragment() {

    abstract val toolbarTitle: Int?

    abstract val isBackEnabled: Boolean

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }
}
