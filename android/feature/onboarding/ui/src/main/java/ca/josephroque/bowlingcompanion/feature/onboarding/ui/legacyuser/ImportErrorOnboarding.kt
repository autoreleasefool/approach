package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SendEmailButton
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun ImportErrorOnboarding(
	state: LegacyUserOnboardingUiState.ImportError,
	onAction: (ImportErrorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp),
	) {
		ImportErrorCard(
			message = state.message,
			exception = state.exception,
			versionName = state.versionName,
			versionCode = state.versionCode,
			attachment = state.legacyDbUri,
			onAction = onAction,
		)
	}
}

@Composable
private fun ImportErrorCard(
	message: String,
	exception: Exception?,
	versionName: String,
	versionCode: String,
	attachment: Uri?,
	onAction: (ImportErrorUiAction) -> Unit,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 32.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.errorContainer,
		),
	) {
		Column(
			modifier = Modifier
				.padding(16.dp),
		) {
			Text(
				text = stringResource(R.string.onboarding_legacy_data_error_title),
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_data_error_there_was_an_error),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 8.dp),
			)

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp),
			) {
				Icon(
					Icons.Default.Warning,
					tint = MaterialTheme.colorScheme.onErrorContainer,
					contentDescription = null,
				)

				Text(
					text = message,
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier,
				)
			}
		}

		HorizontalDivider()

		Column(
			modifier = Modifier.padding(16.dp),
		) {
			Text(
				text = stringResource(R.string.onboarding_legacy_data_error_report_error),
				style = MaterialTheme.typography.bodyMedium,
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_data_error_attach_data),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 8.dp),
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
			) {
				Spacer(modifier = Modifier.weight(1f))

				TextButton(
					onClick = { onAction(ImportErrorUiAction.RetryClicked) },
					modifier = Modifier.padding(end = 8.dp),
				) {
					Text(
						text = stringResource(
							ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_retry,
						),
						style = MaterialTheme.typography.bodyMedium,
					)
				}

				SendEmailButton(
					subjectRes = R.string.onboarding_legacy_data_error_email_subject,
					body = exception?.toString(),
					versionName = versionName,
					versionCode = versionCode,
					onClick = { onAction(ImportErrorUiAction.SendEmailClicked) },
					attachment = attachment,
				)
			}
		}
	}
}

@Preview
@Composable
private fun ImportErrorCardPreview() {
	ImportErrorOnboarding(
		state = LegacyUserOnboardingUiState.ImportError(
			message = "This is a preview of the error message.",
			exception = null,
		),
		onAction = {},
	)
}
