package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.R
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState

@Composable
fun AccessoriesTabOnboarding(
		onDismiss: () -> Unit,
) {
	val sheetState = rememberFlexibleBottomSheetState(
		skipSlightlyExpanded = true,
		skipIntermediatelyExpanded = false,
		allowNestedScroll = false,
		isModal = true,
	)

	FlexibleBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.padding(16.dp)
				.verticalScroll(rememberScrollState()),
		) {
			Text(
				text = stringResource(R.string.accessory_onboarding_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				fontStyle = FontStyle.Italic,
			)

			Text(
				text = stringResource(R.string.accessory_onboarding_message),
				style = MaterialTheme.typography.bodyMedium,
			)

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier.fillMaxWidth(),
			) {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_bowling_ball),
					contentDescription = null,
					modifier = Modifier.size(24.dp),
				)

				Text(
					text = stringResource(R.string.accessory_onboarding_gear_message),
					style = MaterialTheme.typography.bodyMedium,
				)
			}

			Row(
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth(),
			) {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_alley),
					contentDescription = null,
					modifier = Modifier.size(24.dp),
				)

				Text(
					text = stringResource(R.string.accessory_onboarding_alley_message),
					style = MaterialTheme.typography.bodyMedium,
				)
			}

			Row(
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth(),
			) {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_label),
					contentDescription = null,
					modifier = Modifier.size(24.dp),
				)

				Text(
					text = stringResource(R.string.accessory_onboarding_lane_message),
					style = MaterialTheme.typography.bodyMedium,
				)
			}

			Button(
				onClick = onDismiss,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = stringResource(R.string.accessory_onboarding_get_started),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}