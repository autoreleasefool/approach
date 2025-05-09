package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessoriesTopBar(
	isAccessoryMenuExpanded: Boolean,
	onAction: (AccessoriesUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.accessory_list_title),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		actions = {
			Box {
				IconButton(onClick = { onAction(AccessoriesUiAction.AddAccessoryClicked) }) {
					Icon(
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.accessory_list_add),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}

				DropdownMenu(
					expanded = isAccessoryMenuExpanded,
					onDismissRequest = { onAction(AccessoriesUiAction.AccessoryMenuDismissed) },
				) {
					DropdownMenuItem(
						text = {
							Text(
								text = stringResource(R.string.accessory_list_add_alley),
								style = MaterialTheme.typography.bodyMedium,
							)
						},
						onClick = { onAction(AccessoriesUiAction.AddAlleyClicked) },
					)

					DropdownMenuItem(
						text = {
							Text(
								text = stringResource(R.string.accessory_list_add_gear),
								style = MaterialTheme.typography.bodyMedium,
							)
						},
						onClick = { onAction(AccessoriesUiAction.AddGearClicked) },
					)
				}
			}
		},
	)
}
