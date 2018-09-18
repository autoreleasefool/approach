package ca.josephroque.bowlingcompanion.utils

import android.view.View

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * View utilities
 */
val View.isVisible: Boolean
    get() = visibility == View.VISIBLE
