package ca.josephroque.bowlingcompanion.utils


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Color extensions.
 */
@Suppress("MemberVisibilityCanBePrivate")
object Color {

    /** Maximum alpha value. */
    private const val ALPHA_MAX = 255

    /** Alpha value for secondary items. */
    const val ALPHA_SECONDARY = 179

    /** Alpha value for primary items. */
    const val ALPHA_PRIMARY = ALPHA_MAX

    /** Alpha value for disabled items. ~70% */
    const val ALPHA_DISABLED = ALPHA_SECONDARY

    /** Alpha value for enabled items. 100% */
    const val ALPHA_ENABLED = ALPHA_PRIMARY
}
