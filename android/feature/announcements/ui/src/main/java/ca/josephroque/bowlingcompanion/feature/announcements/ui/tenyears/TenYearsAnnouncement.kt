package ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.BowlingPattern
import ca.josephroque.bowlingcompanion.core.designsystem.components.ReadableContent
import ca.josephroque.bowlingcompanion.feature.announcements.ui.R

@Composable
fun TenYearsAnnouncement(onAction: (TenYearsAnnouncementUiAction) -> Unit, modifier: Modifier = Modifier) {
	Card(modifier = modifier) {
		Box {
			BowlingPattern(
				alpha = 0.3f,
				modifier = Modifier.matchParentSize(),
			)

			Column(
				modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				AnnouncementDescription()

				Button(onClick = { onAction(TenYearsAnnouncementUiAction.ClaimBadgeClicked) }) {
					Text(text = stringResource(R.string.announcement_ten_years_claim_badge))
				}
			}
		}
	}
}

@Composable
private fun AnnouncementDescription() {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = Modifier.verticalScroll(rememberScrollState()),
	) {
		ReadableContent(
			effectHeight = 8.dp,
			color = MaterialTheme.colorScheme.surfaceContainerHighest,
		) {
			Text(
				text = stringResource(R.string.announcement_ten_years_title),
				style = MaterialTheme.typography.titleMedium,
			)
		}

		Image(
			painterResource(ca.josephroque.bowlingcompanion.core.achievements.R.drawable.achievement_ten_years),
			contentDescription = null,
			modifier = Modifier
				.widthIn(max = 120.dp)
				.aspectRatio(1f),
		)

		ReadableContent(
			effectHeight = 8.dp,
			color = MaterialTheme.colorScheme.surfaceContainerHighest,
		) {
			Text(
				text = buildAnnotatedString {
					append(
						AnnotatedString(
							text = stringResource(R.string.announcement_ten_years_description_from_bowling_companion),
							spanStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
						),
					)

					withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
						append(" ")
					}

					append(
						AnnotatedString(
							text = stringResource(R.string.announcement_ten_years_description_hope_you_enjoyed),
							spanStyle = MaterialTheme.typography.bodyMedium.toSpanStyle(),
						),
					)
				},
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Preview
@Composable
fun TenYearsAnnouncementPreview() {
	Dialog(onDismissRequest = {}) {
		TenYearsAnnouncement(
			onAction = {},
		)
	}
}
