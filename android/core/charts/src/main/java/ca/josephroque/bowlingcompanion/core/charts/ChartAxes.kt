package ca.josephroque.bowlingcompanion.core.charts

import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis

@Composable
fun rememberEmptyBottomAxis(): HorizontalAxis<AxisPosition.Horizontal.Bottom> = rememberBottomAxis(
	label = null,
	tick = null,
	guideline = null,
)
