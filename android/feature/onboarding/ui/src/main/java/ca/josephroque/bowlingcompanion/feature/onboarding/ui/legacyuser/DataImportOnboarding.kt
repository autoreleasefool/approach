package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SendEmailButton
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun DataImportOnboarding(
	state: LegacyUserOnboardingUiState.DataImport,
	onAction: (DataImportUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp),
	) {
		DataImportCard(
			versionName = state.versionName,
			versionCode = state.versionCode,
			onAction = onAction,
		)
	}
}

@Composable
private fun DataImportCard(versionName: String, versionCode: String, onAction: (DataImportUiAction) -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 32.dp),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
		) {
			Text(
				text = stringResource(R.string.onboarding_legacy_data_importing),
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_data_importing_description),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 8.dp),
			)
		}

		HorizontalDivider()

		Column(
			modifier = Modifier.padding(16.dp),
		) {
			Text(
				text = stringResource(R.string.onboarding_legacy_data_importing_in_case_of_error),
				style = MaterialTheme.typography.bodyMedium,
			)

			SendEmailButton(
				versionName = versionName,
				versionCode = versionCode,
				onClick = { onAction(DataImportUiAction.SendEmailClicked) },
				modifier = Modifier
					.align(Alignment.End)
					.padding(top = 16.dp),
			)
		}
	}
}

@Preview
@Composable
private fun DataImportCardPreview() {
	DataImportOnboarding(
		state = LegacyUserOnboardingUiState.DataImport(
			versionName = "1.0.0",
			versionCode = "1",
		),
		onAction = {},
	)
}
