package ca.josephroque.bowlingcompanion.core.statistics.charts.utils

import ca.josephroque.bowlingcompanion.core.statistics.models.ChartSize
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout

val ChartSize.horizontalLayout: HorizontalLayout
	get() = when (this) {
		ChartSize.DEFAULT -> HorizontalLayout.FullWidth(
			unscalableStartPaddingDp = 16f,
			unscalableEndPaddingDp = 16f,
		)
		ChartSize.COMPACT -> HorizontalLayout.FullWidth(
			unscalableStartPaddingDp = 0f,
			unscalableEndPaddingDp = 0f,
		)
	}
