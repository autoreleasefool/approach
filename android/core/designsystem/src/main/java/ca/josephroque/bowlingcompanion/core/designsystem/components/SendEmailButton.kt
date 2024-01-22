package ca.josephroque.bowlingcompanion.core.designsystem.components

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun SendEmailButton(
	errorMessage: String,
	versionName: String,
	versionCode: String,
	modifier: Modifier = Modifier,
	@StringRes recipientRes: Int = R.string.default_email_recipient,
	@StringRes subjectRes: Int = R.string.default_email_subject,
) {
	val context = LocalContext.current
	OutlinedButton(
		onClick = {
			val recipient = context.resources.getString(recipientRes)
			val subject = context.resources.getString(
				subjectRes,
				versionName,
				versionCode,
			)
			val emailIntent = Intent(Intent.ACTION_SEND).apply {
				setDataAndType(Uri.parse("mailto:"), "message/rfc822")
				putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
				putExtra(Intent.EXTRA_SUBJECT, subject)
				putExtra(Intent.EXTRA_TEXT, errorMessage)
			}

			context.startActivity(
				Intent.createChooser(
					emailIntent,
					context.resources.getString(R.string.action_send_email)
				)
			)
		},
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		Text(
			text = stringResource(R.string.action_send_email),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}