package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.model.Acknowledgement
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OfflineFirstAcknowledgementsRepository @Inject constructor(
	private val fileManager: FileManager,
) : AcknowledgementsRepository {
	override fun getAcknowledgements(): Flow<List<Acknowledgement>> = flowOf(
		fileManager.getAssets("acknowledgements")
			.map { Pair(assetNameToAcknowledgementName(it), fileManager.getAsset("acknowledgements/$it")) }
			.map { Acknowledgement(it.first, it.second) }
			.sortedBy { it.name.lowercase() },
	)

	override fun getAcknowledgement(name: String): Flow<Acknowledgement> {
		val asset = fileManager.getAsset("acknowledgements/${acknowledgementNameToAssetName(name)}")
		return flowOf(Acknowledgement(name, asset))
	}

	private fun assetNameToAcknowledgementName(assetName: String): String =
		assetName.removeSuffix(".txt").replace("_", " ")

	private fun acknowledgementNameToAssetName(acknowledgementName: String): String =
		acknowledgementName.replace(" ", "_") + ".txt"
}
