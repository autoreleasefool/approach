package ca.josephroque.bowlingcompanion.feature.accessories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.alleysList
import ca.josephroque.bowlingcompanion.feature.gearlist.GearListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.gearList
import java.util.UUID

@Composable
internal fun AccessoriesRoute(
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AccessoriesViewModel = hiltViewModel(),
) {
	val accessoriesState by viewModel.uiState.collectAsStateWithLifecycle()
	val gearListState by viewModel.gearListState.collectAsStateWithLifecycle()
	val alleysListState by viewModel.alleysListState.collectAsStateWithLifecycle()

	AccessoriesScreen(
		accessoriesState = accessoriesState,
		alleysListState = alleysListState,
		gearListState = gearListState,
		onAddAccessory = viewModel::expandAccessoryMenu,
		onMinimizeAddAccessoryMenu = viewModel::minimizeAccessoryMenu,
		onAddAlley = {
			viewModel.minimizeAccessoryMenu()
			onAddAlley()
		},
		onAddGear = {
			viewModel.minimizeAccessoryMenu()
			onAddGear()
		},
		onViewAllAlleys = onViewAllAlleys,
		onViewAllGear = onViewAllGear,
		onShowAlleyDetails = onShowAlleyDetails,
		onShowGearDetails = onShowGearDetails,
		modifier = modifier,
	)
}

@Composable
internal fun AccessoriesScreen(
	accessoriesState: AccessoriesUiState,
	alleysListState: AlleysListUiState,
	gearListState: GearListUiState,
	onAddAccessory: () -> Unit,
	onMinimizeAddAccessoryMenu: () -> Unit,
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AccessoriesTopBar(
				isAccessoryMenuExpanded = accessoriesState.isAccessoryMenuExpanded,
				onAddAccessory = onAddAccessory,
				onMinimizeAddAccessoryMenu = onMinimizeAddAccessoryMenu,
				onAddAlley = onAddAlley,
				onAddGear = onAddGear,
			)
		}
	) { padding ->
		LazyColumn(
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		) {
			header(
				titleResourceId = R.string.accessory_list_alleys_title,
				action = HeaderAction(
					actionResourceId = RCoreDesign.string.action_view_all,
					onClick = onViewAllAlleys,
				)
			)

			alleysList(
				alleysListState = alleysListState,
				onAlleyClick = onShowAlleyDetails,
			)

			when (alleysListState) {
				AlleysListUiState.Loading -> Unit
				is AlleysListUiState.Success -> {
					item {
						if (alleysListState.list.size > alleysListItemLimit) {
							ListSectionFooter(
								footer = stringResource(R.string.accessory_list_x_most_recent, alleysListItemLimit)
							)
						}
					}
				}
			}

			header(
				titleResourceId = R.string.accessory_list_gear_title,
				action = HeaderAction(
					actionResourceId = RCoreDesign.string.action_view_all,
					onClick = onViewAllGear,
				)
			)

			gearList(
				gearListState = gearListState,
				onGearClick = onShowGearDetails,
			)

			when (gearListState) {
				GearListUiState.Loading -> Unit
				is GearListUiState.Success -> {
					item {
						if (gearListState.list.size > gearListItemLimit) {
							ListSectionFooter(
								footer = stringResource(R.string.accessory_list_x_most_recent, gearListItemLimit)
							)
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccessoriesTopBar(
	isAccessoryMenuExpanded: Boolean,
	onAddAccessory: () -> Unit,
	onMinimizeAddAccessoryMenu: () -> Unit,
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
) {
	CenterAlignedTopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.destination_accessories),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		actions = {
			Box {
				IconButton(onClick = onAddAccessory) {
					Icon(
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.accessory_list_add),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}

				DropdownMenu(
					expanded = isAccessoryMenuExpanded,
					onDismissRequest = onMinimizeAddAccessoryMenu,
				) {
					DropdownMenuItem(
						text = {
							Text(
							 text = stringResource(R.string.accessory_list_add_alley),
							 style = MaterialTheme.typography.bodyMedium,
							)
						},
						onClick = onAddAlley,
					)

					DropdownMenuItem(
						text = {
							Text(
								text = stringResource(R.string.accessory_list_add_gear),
								style = MaterialTheme.typography.bodyMedium,
							)
						},
						onClick = onAddGear,
					)
				}
			}
		}
	)
}

@Preview
@Composable
private fun AccessoriesPreview() {
	Surface {
		AccessoriesScreen(
			accessoriesState = AccessoriesUiState(isAccessoryMenuExpanded = false),
			alleysListState = AlleysListUiState.Success(listOf(
				AlleyListItem(id = UUID.randomUUID(), name = "Grandview Lanes", material = null, mechanism = null, pinBase = null, pinFall = null),
			)),
			gearListState = GearListUiState.Success(listOf(
				GearListItem(id = UUID.randomUUID(), name = "Yellow Ball", kind = GearKind.BOWLING_BALL, ownerName = null),
				GearListItem(id = UUID.randomUUID(), name = "Red Ball", kind = GearKind.BOWLING_BALL, ownerName = "Joseph"),
			)),
			onAddAccessory = {},
			onMinimizeAddAccessoryMenu = {},
			onAddAlley = {},
			onAddGear = {},
			onViewAllAlleys = {},
			onViewAllGear = {},
			onShowAlleyDetails = {},
			onShowGearDetails = {},
		)
	}
}