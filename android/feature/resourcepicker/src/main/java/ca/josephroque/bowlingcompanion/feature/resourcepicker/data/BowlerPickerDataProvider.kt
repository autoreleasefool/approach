package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BowlerPickerDataProvider @Inject constructor(
	bowlersRepository: BowlersRepository,
	kind: BowlerKind?,
) : ResourcePickerDataProvider {
	private val bowlers: Flow<List<BowlerSummary>> = when (kind) {
		BowlerKind.OPPONENT -> bowlersRepository.getOpponentsList().map { opponents ->
			opponents.map { BowlerSummary(it.id, it.name) }
		}
		BowlerKind.PLAYABLE, null -> bowlersRepository.getBowlersList(kind).map { bowlers ->
			bowlers.map { BowlerSummary(it.id, it.name) }
		}
	}
	
	override suspend fun loadResources(): List<ResourceItem> = bowlers
		.map { bowlers -> bowlers.map { ResourceItem.Bowler(it.id, it.name) } }
		.first()
}
