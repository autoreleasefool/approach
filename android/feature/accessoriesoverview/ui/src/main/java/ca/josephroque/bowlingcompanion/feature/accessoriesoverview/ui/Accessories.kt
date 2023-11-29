package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
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
				footer(R.string.accessory_list_alley_empty)
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

		header(
			titleResourceId = R.string.accessory_list_gear_title,
			action = HeaderAction(
				actionResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_view_all,
				onClick = onViewAllGear,
			)
		)

		if (gearListState != null) {
			if (gearListState.list.isEmpty()) {
				footer(R.string.accessory_list_gear_empty)
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