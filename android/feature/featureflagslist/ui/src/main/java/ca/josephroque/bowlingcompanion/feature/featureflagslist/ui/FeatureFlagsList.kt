package ca.josephroque.bowlingcompanion.feature.featureflagslist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag

@Composable
fun FeatureFlagsList(
	state: FeatureFlagsListUiState,
	onAction: (FeatureFlagsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		item {
			Row(modifier = Modifier.padding(bottom = 16.dp)) {
				Spacer(modifier = Modifier.weight(1f))

				Button(
					onClick = { onAction(FeatureFlagsListUiAction.ResetOverridesClicked) },
				) {
					Text(text = stringResource(R.string.feature_flags_list_reset_all_overrides))
				}

				Spacer(modifier = Modifier.weight(1f))
			}
		}

		items(
			items = state.featureFlags,
			key = { it.flag.name },
		) { flagState ->
			FeatureFlagRow(
				flag = flagState.flag,
				checked = flagState.isEnabled,
				enabled = flagState.flag.isOverridable,
				onChange = { onAction(FeatureFlagsListUiAction.FeatureFlagToggled(flagState.flag, it)) },
			)
		}
	}
}

@Composable
private fun FeatureFlagRow(
	flag: FeatureFlag,
	checked: Boolean,
	enabled: Boolean,
	onChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	LabeledSwitch(
		title = flag.name,
		subtitle = flag.introduced,
		checked = checked,
		enabled = enabled,
		onCheckedChange = onChange,
		modifier = modifier.padding(vertical = 4.dp),
	)
}

@Preview
@Composable
private fun FeatureFlagsListPreview() {
	Surface {
		FeatureFlagsList(
			state = FeatureFlagsListUiState(
				featureFlags = FeatureFlag.entries.map {
					FeatureFlagState(
						flag = it,
						isEnabled = true,
					)
				},
			),
			onAction = {},
		)
	}
}
