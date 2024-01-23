package ca.josephroque.bowlingcompanion.core.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendEmail(
	recipient: String,
	intentTitle: String,
	subject: String? = null,
	body: String? = null,
	attachment: Uri? = null,
	context: Context,
) {
	val action = if (attachment == null) Intent.ACTION_SENDTO else Intent.ACTION_SEND
	val emailIntent = Intent(action).apply {
		if (attachment == null) {
			data = Uri.parse("mailto:")
		} else {
			type = "*/*"
		}

		putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
		if (subject != null) putExtra(Intent.EXTRA_SUBJECT, subject)
		if (body != null) putExtra(Intent.EXTRA_TEXT, body)
		if (attachment != null) putExtra(Intent.EXTRA_STREAM, attachment)
	}

	context.startActivity(
		Intent.createChooser(
			emailIntent,
			intentTitle,
		)
	)
}