package ca.josephroque.bowlingcompanion.feature.settings.ui.developer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.sendEmail
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.settings.ui.R
import ca.josephroque.bowlingcompanion.feature.settings.ui.components.Link

@Composable
fun DeveloperSettings(
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(top = 16.dp),
	) {
		ContactCard()
		LearnMoreCard()
	}
}

@Composable
private fun ContactCard() {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		Text(
			text = stringResource(id = R.string.developer_settings_contact),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		)

		val context = LocalContext.current
		val uriHandler = LocalUriHandler.current

		Text(
			text = stringResource(R.string.developer_settings_contact_name),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		)

		Link(
			titleResourceId = R.string.developer_settings_contact_email,
			iconResourceId = RCoreDesign.drawable.ic_send,
			onClick = {
				val recipient = context.resources.getString(R.string.developer_settings_contact_email)
				val intentTitle = context.resources.getString(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email)
				sendEmail(recipient = recipient, intentTitle = intentTitle, context = context)
			},
		)

		Link(
			titleResourceId = R.string.developer_settings_contact_mastodon,
			subtitleResourceId = R.string.developer_settings_contact_mastodon_url,
			iconResourceId = RCoreDesign.drawable.ic_open_in_new,
			onClick = {
				val viewSourceUri = context.resources.getString(R.string.developer_settings_contact_mastodon_url)
				uriHandler.openUri(viewSourceUri)
			},
		)
	}
}

@Composable
private fun LearnMoreCard() {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		Text(
			text = stringResource(id = R.string.developer_settings_learn_more),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		)

		val context = LocalContext.current
		val uriHandler = LocalUriHandler.current

		Link(
			titleResourceId = R.string.developer_settings_website,
			subtitleResourceId = R.string.developer_settings_website_url,
			iconResourceId = RCoreDesign.drawable.ic_open_in_new,
			onClick = {
				val viewSourceUri = context.resources.getString(R.string.developer_settings_website_url)
				uriHandler.openUri(viewSourceUri)
			},
		)

		Link(
			titleResourceId = R.string.developer_settings_blog,
			subtitleResourceId = R.string.developer_settings_blog_url,
			iconResourceId = RCoreDesign.drawable.ic_open_in_new,
			onClick = {
				val viewSourceUri = context.resources.getString(R.string.developer_settings_blog_url)
				uriHandler.openUri(viewSourceUri)
			},
		)
	}
}

@Preview
@Composable
private fun DeveloperSettingsPreview() {
	Surface {
		DeveloperSettings()
	}
}