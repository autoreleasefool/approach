package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.MutedEmptyState
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding.AccessoriesOnboarding
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.alleysList
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.gearList

@Composable
fun Accessories(
	state: AccessoriesUiState,
	onAction: (AccessoriesUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingOnboarding) {
		AccessoriesOnboarding(
			onAction = { onAction(AccessoriesUiAction.Onboarding(it)) },
		)
	}

	LazyColumn(modifier = modifier.fillMaxSize()) {
		header(
			titleResourceId = R.string.accessory_list_alleys_title,
			action = HeaderAction(
				actionResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_view_all,
				onClick = { onAction(AccessoriesUiAction.ViewAllAlleysClicked) },
			),
		)

		if (state.alleysList != null) {
			if (state.alleysList.list.isEmpty()) {
				item {
					MutedEmptyState(
						title = R.string.accessory_list_alley_empty_title,
						message = R.string.accessory_list_alley_empty_message,
						icon = @Suppress("ktlint:standard:max-line-length")
						ca.josephroque.bowlingcompanion.feature.alleyslist.ui.R.drawable.alleys_list_empty_state,
						modifier = Modifier.padding(bottom = 16.dp),
					)
				}
			} else {
				alleysList(
					list = state.alleysList.list,
					onAlleyClick = { onAction(AccessoriesUiAction.AlleyClicked(it)) },
				)

				item {
					if (state.alleysList.list.size > state.alleysItemLimit) {
						ListSectionFooter(
							footer = stringResource(
								R.string.accessory_list_x_most_recent,
								state.alleysItemLimit,
							),
						)
					}
				}
			}
		}

		item {
			HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 8.dp)
		}

		header(
			titleResourceId = R.string.accessory_list_gear_title,
			action = HeaderAction(
				actionResourceId = ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_view_all,
				onClick = { onAction(AccessoriesUiAction.ViewAllGearClicked) },
			),
		)

		if (state.gearList != null) {
			if (state.gearList.list.isEmpty()) {
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
					list = state.gearList.list,
					onGearClick = { onAction(AccessoriesUiAction.GearClicked(it)) },
				)

				item {
					if (state.gearList.list.size > state.gearItemLimit) {
						ListSectionFooter(
							footer = stringResource(R.string.accessory_list_x_most_recent, state.gearItemLimit),
						)
					}
				}
			}
		}
	}
}
