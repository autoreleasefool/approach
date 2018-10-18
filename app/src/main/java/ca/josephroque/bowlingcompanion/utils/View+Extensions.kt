package ca.josephroque.bowlingcompanion.utils

import android.view.View

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * View utilities
 */
var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) { visibility = if (value) View.VISIBLE else View.GONE }
