package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize

@Composable
fun SeriesItemSize.icon(): Painter = when (this) {
	SeriesItemSize.COMPACT -> painterResource(R.drawable.ic_list)
	SeriesItemSize.DEFAULT -> painterResource(R.drawable.ic_expanded_rows)
}

@Composable
fun SeriesItemSize.contentDescription(): String = when (this) {
	SeriesItemSize.COMPACT -> stringResource(R.string.cd_series_item_size_compact)
	SeriesItemSize.DEFAULT -> stringResource(R.string.cd_series_item_size_default)
}
