package ca.josephroque.bowlingcompanion.core.designsystem.components

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.sendEmail
import ca.josephroque.bowlingcompanion.core.designsystem.R

enum class SendEmailButtonStyle {
	PRIMARY,
	SECONDARY,
}

@Composable
fun SendEmailButton(
	versionName: String,
	versionCode: String,
	modifier: Modifier = Modifier,
	body: String? = null,
	onClick: () -> Unit = {},
	style: SendEmailButtonStyle = SendEmailButtonStyle.PRIMARY,
	@StringRes recipientRes: Int = R.string.default_email_recipient,
	@StringRes subjectRes: Int = R.string.default_email_subject,
	attachment: Uri? = null,
) {
	val context = LocalContext.current
	val onSendEmail = {
		onClick()

		val recipient = context.resources.getString(recipientRes)
		val subject = context.resources.getString(
			subjectRes,
			versionName,
			versionCode,
		)
		val intentTitle = context.resources.getString(R.string.action_send_email)

		sendEmail(
			recipient = recipient,
			intentTitle = intentTitle,
			subject = subject,
			attachment = attachment,
			body = body,
			context = context,
		)
	}

	val content: @Composable RowScope.() -> Unit = {
		if (attachment != null) {
			Icon(
				painterResource(R.drawable.ic_attach_file),
				contentDescription = null,
//				tint = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.padding(end = 8.dp),
			)
		}

		Text(
			text = stringResource(R.string.action_send_email),
			style = MaterialTheme.typography.bodyMedium,
		)
	}

	when (style) {
		SendEmailButtonStyle.PRIMARY -> OutlinedButton(
			onClick = onSendEmail,
			modifier = modifier.padding(horizontal = 16.dp),
			content = content,
		)
		SendEmailButtonStyle.SECONDARY -> TextButton(
			onClick = onSendEmail,
			modifier = modifier.padding(horizontal = 16.dp),
			content = content,
		)
	}
}