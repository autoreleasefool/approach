package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.R

@Composable
fun SendEmailButton(
	errorMessage: String?,
	versionName: String,
	versionCode: String,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	OutlinedButton(
		onClick = {
			val recipient = context.resources.getString(R.string.data_error_email_recipient)
			val subject = context.resources.getString(
				R.string.data_error_email_subject,
				versionName,
				versionCode,
			)
			val body = errorMessage ?: context.resources.getString(R.string.data_error_unknown)
			val emailIntent = Intent(Intent.ACTION_SEND).apply {
				setDataAndType(Uri.parse("mailto:"), "message/rfc822")
				putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
				putExtra(Intent.EXTRA_SUBJECT, subject)
				putExtra(Intent.EXTRA_TEXT, body)
			}

			context.startActivity(
				Intent.createChooser(
					emailIntent,
					context.resources.getString(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email)
				)
			)
		},
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		Text(
			text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}