package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailsTopBar(
	seriesDate: LocalDate?,
	onAction: (SeriesDetailsUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	MediumTopAppBar(
		scrollBehavior = scrollBehavior,
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = seriesDate?.simpleFormat() ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(SeriesDetailsUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(SeriesDetailsUiAction.AddGameClicked) }) {
				Icon(
					Icons.Default.Add,
					contentDescription = stringResource(R.string.cd_add_game),
				)
			}
		},
	)
}
