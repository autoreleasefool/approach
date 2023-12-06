package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.MutedEmptyState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.alleysList
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.gearList
import java.util.UUID

@Composable
fun Accessories(
	modifier: Modifier = Modifier,
	accessoriesState: AccessoriesUiState,
	alleysListState: AlleysListUiState?,
	gearListState: GearListUiState?,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
) {
	LazyColumn(modifier = modifier.fillMaxSize()) {
		header(
			titleResourceId = R.string.accessory_list_alleys_title,
			action = HeaderAction(
				actionResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_view_all,
				onClick = onViewAllAlleys,
			)
		)

		if (alleysListState != null) {
			if (alleysListState.list.isEmpty()) {
				item {
					MutedEmptyState(
						title = R.string.accessory_list_alley_empty_title,
						message = R.string.accessory_list_alley_empty_message,
						icon = ca.josephroque.bowlingcompanion.feature.alleyslist.ui.R.drawable.alleys_list_empty_state,
						modifier = Modifier.padding(bottom = 16.dp),
					)
				}
			} else {
				alleysList(
					list = alleysListState.list,
					onAlleyClick = {  onShowAlleyDetails(it.id) },
				)

				item {
					if (alleysListState.list.size > accessoriesState.alleysItemLimit) {
						ListSectionFooter(
							footer = stringResource(
								R.string.accessory_list_x_most_recent,
								accessoriesState.alleysItemLimit
							)
						)
					}
				}
			}
		}

		item {
			Divider(thickness = 8.dp)
		}

		header(
			titleResourceId = R.string.accessory_list_gear_title,
			action = HeaderAction(
				actionResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_view_all,
				onClick = onViewAllGear,
			)
		)

		if (gearListState != null) {
			if (gearListState.list.isEmpty()) {
				item {
					MutedEmptyState(
						title = R.string.accessory_list_gear_empty_title,
						message = R.string.accessory_list_gear_empty_message,
						icon = ca.josephroque.bowlingcompanion.feature.gearlist.ui.R.drawable.gear_list_empty_state,
						modifier = Modifier.padding(bottom = 16.dp),
					)
				}
			} else {
				gearList(
					list = gearListState.list,
					onGearClick = { onShowGearDetails(it.id) },
				)

				item {
					if (gearListState.list.size > accessoriesState.gearItemLimit) {
						ListSectionFooter(
							footer = stringResource(R.string.accessory_list_x_most_recent, accessoriesState.gearItemLimit)
						)
					}
				}
			}
		}
	}
}

data class AccessoriesUiState(
	val isAccessoryMenuExpanded: Boolean = false,
	val alleysItemLimit: Int,
	val gearItemLimit: Int,
)