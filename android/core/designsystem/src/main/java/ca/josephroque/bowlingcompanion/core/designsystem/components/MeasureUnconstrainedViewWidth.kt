package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun MeasureUnconstrainedViewWidth(
	viewToMeasure: @Composable () -> Unit,
	content: @Composable (measuredWidth: Dp) -> Unit,
) {
	SubcomposeLayout { constraints ->
		val measuredWidth = subcompose("viewToMeasure", viewToMeasure)[0]
			.measure(Constraints()).width.toDp()

		val contentPlaceable = subcompose("content") {
			content(measuredWidth)
		}[0].measure(constraints)
		layout(contentPlaceable.width, contentPlaceable.height) {
			contentPlaceable.place(0, 0)
		}
	}
}
