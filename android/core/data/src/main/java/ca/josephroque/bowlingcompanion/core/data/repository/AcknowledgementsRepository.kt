package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.Acknowledgement
import kotlinx.coroutines.flow.Flow

interface AcknowledgementsRepository {
	fun getAcknowledgements(): Flow<List<Acknowledgement>>
	fun getAcknowledgement(name: String): Flow<Acknowledgement>
}