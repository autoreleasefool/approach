package ca.josephroque.bowlingcompanion.feature.gearlist.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.R

@Composable
fun GearKind.filterDescription(): String = when (this) {
	GearKind.BOWLING_BALL -> stringResource(R.string.gear_kind_filter_bowling_ball)
	GearKind.SHOES -> stringResource(R.string.gear_kind_filter_shoes)
	GearKind.OTHER -> stringResource(R.string.gear_kind_filter_other)
	GearKind.TOWEL -> stringResource(R.string.gear_kind_filter_towel)
}