package ca.josephroque.bowlingcompanion.feature.laneslist.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.ui.LaneRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

fun LazyListScope.lanesList(
	list: List<LaneListItem>,
	onLaneClick: (LaneListItem) -> Unit,
	onLaneDelete: ((LaneListItem) -> Unit)? = null,
) {
	items(
		items = list,
		key = { it.id },
	) { lane ->
		val deleteAction = onLaneDelete?.let {
			SwipeAction(
				icon = rememberVectorPainter(Icons.Filled.Delete),
				background = colorResource(R.color.destructive),
				onSwipe = { it(lane) },
			)
		}

		SwipeableActionsBox(
			startActions = listOfNotNull(deleteAction),
		) {
			LaneRow(
				lane = lane,
				onClick = { onLaneClick(lane) },
			)
		}
	}
}