package ca.josephroque.bowlingcompanion.feature.opponentslist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import ca.josephroque.bowlingcompanion.core.components.list.footer
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentItemRow
import java.util.UUID

@Composable
internal fun OpponentsListRoute(
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
	onOpenOpponentDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OpponentsListViewModel = hiltViewModel(),
) {
	val opponentsListState by viewModel.opponentsListState.collectAsStateWithLifecycle()

	OpponentsListScreen(
		opponentsListState = opponentsListState,
		onBackPressed = onBackPressed,
		onAddOpponent = onAddOpponent,
		onOpenOpponentDetails = onOpenOpponentDetails,
		modifier = modifier,
	)
}

@Composable
internal fun OpponentsListScreen(
	opponentsListState: OpponentsListUiState,
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
	onOpenOpponentDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			OpponentsListTopBar(
				onBackPressed = onBackPressed,
				onAddOpponent = onAddOpponent,
			)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			footer(R.string.opponent_list_description)

			when (opponentsListState) {
				OpponentsListUiState.Loading -> Unit
				is OpponentsListUiState.Success -> {
					items(
						items = opponentsListState.list,
						key = { it.id },
						contentType = { "opponent" },
					) { opponent ->
						OpponentItemRow(
							opponent = opponent,
							onClick = { onOpenOpponentDetails(opponent.id) },
						)
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OpponentsListTopBar(
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.opponent_list_title),
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			IconButton(onClick = onBackPressed) {
				Icon(
					Icons.Default.ArrowBack,
					contentDescription = stringResource(R.string.cd_back),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		actions = {
			IconButton(onClick = onAddOpponent) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.opponent_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}