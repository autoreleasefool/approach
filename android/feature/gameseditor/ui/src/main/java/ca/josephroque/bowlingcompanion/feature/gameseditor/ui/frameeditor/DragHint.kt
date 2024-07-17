package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
fun DragHint(
	isVisible: Boolean,
	onAction: (FrameEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(
		visible = isVisible,
		enter = fadeIn(),
		exit = fadeOut(),
	) {
		Box(
			modifier = modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.2f)),
		) {
			DragHintAnimation()

			DragHintTipText(
				onAction = onAction,
				modifier = Modifier
					.padding(16.dp)
					.align(Alignment.BottomCenter),
			)
		}
	}
}

@Composable
private fun DragHintAnimation(modifier: Modifier = Modifier) {
	BoxWithConstraints(modifier = modifier.fillMaxSize()) {
		val infiniteTransition = rememberInfiniteTransition(label = "infinite")

		val opacity by infiniteTransition.animateFloat(
			label = "opacity",
			initialValue = 0f,
			targetValue = 0f,
			animationSpec = infiniteRepeatable(
				animation = keyframes {
					durationMillis = 2_000
					0.0f at 0 using LinearEasing
					1.0f at 500 using LinearEasing
					1.0f at 1_500 using LinearEasing
					0.0f at 2_000 using LinearEasing
				},
				repeatMode = RepeatMode.Restart,
			),
		)

		val xOffset by infiniteTransition.animateFloat(
			label = "xOffset",
			initialValue = 16.dp.value,
			targetValue = maxWidth.value - 16.dp.value,
			animationSpec = infiniteRepeatable(
				animation = keyframes {
					durationMillis = 2_000
					16.dp.value at 0 using FastOutSlowInEasing
					16.dp.value at 500 using FastOutSlowInEasing
					maxWidth.value - 16.dp.value - 64.dp.value at 1_500 using LinearEasing
					maxWidth.value - 16.dp.value - 64.dp.value at 2_000 using LinearEasing
				},
				repeatMode = RepeatMode.Restart,
			),
		)

		Box(
			modifier = Modifier
				.align(Alignment.Center)
				.fillMaxWidth(),
		) {
			Icon(
				painter = painterResource(R.drawable.ic_swipe),
				contentDescription = stringResource(R.string.cd_swipe_indicator),
				tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_200),
				modifier = Modifier
					.size(64.dp)
					.offset { IntOffset(xOffset.dp.roundToPx(), 0) }
					.alpha(opacity),
			)
		}
	}
}

@Composable
private fun DragHintTipText(
	onAction: (FrameEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(modifier = modifier) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			Text(
				text = stringResource(R.string.frame_editor_drag_hint_tip),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.weight(1f),
			)

			OutlinedButton(
				onClick = { onAction(FrameEditorUiAction.DragHintDismissed) },
				modifier = Modifier,
			) {
				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_hide,
					),
				)
			}
		}
	}
}

@Preview
@Composable
private fun DragHintPreview() {
	DragHint(
		isVisible = true,
		onAction = {},
		modifier = Modifier.fillMaxSize(),
	)
}
