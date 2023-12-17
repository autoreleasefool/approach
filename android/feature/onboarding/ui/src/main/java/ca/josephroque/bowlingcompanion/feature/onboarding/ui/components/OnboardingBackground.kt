package ca.josephroque.bowlingcompanion.feature.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun OnboardingBackground(modifier: Modifier = Modifier) {
	Box(modifier = modifier.fillMaxSize()) {
		val resources = LocalContext.current.resources
		val image = remember {
			ResourcesCompat.getDrawable(resources, R.drawable.onboarding_pattern, null)?.toBitmap()?.asImageBitmap()
		} ?: return@Box

		val brush = remember(image) {
			ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated))
		}

		Box(
			modifier = Modifier
				.fillMaxSize()
				.alpha(0.4f)
				.background(brush)
		)
	}
}