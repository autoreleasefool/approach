package ca.josephroque.bowlingcompanion.feature.alleyslist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.BackButton
import java.util.UUID

@Composable
internal fun AlleysListRoute(
	onBackPressed: () -> Unit,
	onEditAlley: (UUID) -> Unit,
	onAddAlley: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AlleysListViewModel = hiltViewModel(),
) {
	val alleysListState by viewModel.alleysListState.collectAsStateWithLifecycle()

	AlleysListScreen(
		alleysListState = alleysListState,
		onBackPressed = onBackPressed,
		onAddAlley = onAddAlley,
		onAlleyClick = onShowAlleyDetails,
		modifier = modifier,
	)
}

@Composable
internal fun AlleysListScreen(
	alleysListState: AlleysListUiState,
	onBackPressed: () -> Unit,
	onAlleyClick: (UUID) -> Unit,
	onAddAlley: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AlleysListTopBar(onAddAlley = onAddAlley, onBackPressed = onBackPressed)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			alleysList(
				alleysListState = alleysListState,
				onAlleyClick = onAlleyClick,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlleysListTopBar(
	onBackPressed: () -> Unit,
	onAddAlley: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.alley_list_title),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			IconButton(onClick = onAddAlley) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.alley_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}