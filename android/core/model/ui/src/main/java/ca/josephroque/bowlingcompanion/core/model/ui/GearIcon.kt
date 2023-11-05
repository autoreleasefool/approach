package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.GearKind

@Composable
fun GearKind.icon(): Painter = when (this) {
	GearKind.BOWLING_BALL -> painterResource(id = RCoreDesign.drawable.ic_bowling_ball)
	GearKind.SHOES -> painterResource(id = R.drawable.ic_shoe_prints)
	GearKind.OTHER -> painterResource(id = R.drawable.ic_circle_question)
	GearKind.TOWEL -> painterResource(id = R.drawable.ic_towel)
}