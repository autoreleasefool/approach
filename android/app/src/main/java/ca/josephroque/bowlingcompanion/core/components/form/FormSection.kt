package ca.josephroque.bowlingcompanion.core.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R

@Composable
fun FormSection(
	modifier: Modifier = Modifier,
	@StringRes titleResourceId: Int? = null,
	@StringRes footerResourceId: Int? = null,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = modifier,
	) {
		titleResourceId?.let {
			Text(
				text = stringResource(it),
				fontSize = 14.sp,
				fontWeight = FontWeight.Light,
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
			)
		}

		content()

		footerResourceId?.let {
			Text(
				text = stringResource(it),
				fontSize = 12.sp,
				fontWeight = FontWeight.Thin,
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
			)
		}
	}
}

@Preview
@Composable
fun FormSectionPreview() {
	Surface {
		FormSection(
			titleResourceId = R.string.bowler_list_title,
			footerResourceId = R.string.onboarding_new_user_logbook
		) {
			Text("Here is a basic item")
		}
	}
}