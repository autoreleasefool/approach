package ca.josephroque.bowlingcompanion.feature.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun OnboardingBackground(modifier: Modifier = Modifier) {
	Box(modifier = modifier.fillMaxSize()) {
		val resources = LocalContext.current.resources
		val isDarkTheme = isSystemInDarkTheme()

		val image = remember(isDarkTheme) {
			val drawable = ResourcesCompat.getDrawable(
				resources,
				R.drawable.onboarding_pattern,
				null,
			) ?: return@remember null

			val tint = ResourcesCompat.getColor(
				resources,
				if (isDarkTheme) {
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.white
				} else {
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.black
				},
				null,
			)

			val tinted = DrawableCompat.wrap(drawable)
			DrawableCompat.setTint(tinted.mutate(), tint)

			return@remember tinted.toBitmap().asImageBitmap()
		} ?: return@Box

		val brush = remember(image) {
			ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated))
		}

		Box(
			modifier = Modifier
				.fillMaxSize()
				.alpha(0.4f)
				.background(brush),
		)
	}
}

@Composable
fun ReadableContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
	val density = LocalDensity.current
	var height by remember { mutableStateOf(0.dp) }

	Box(
		modifier = modifier
			.background(
				if (height == 0.dp) {
					Brush.verticalGradient(
						0f to Color.Transparent,
						1f to Color.Transparent,
					)
				} else {
					Brush.verticalGradient(
						0f to Color.Transparent,
						(16.dp / height.value).value to colorResource(R.color.container),
						1f - (16.dp / height.value).value to colorResource(R.color.container),
						1f to Color.Transparent,
					)
				},
			)
			.padding(vertical = 32.dp)
			.onSizeChanged { size ->
				height = with(density) { size.height.toDp() }
			},
	) {
		content()
	}
}
