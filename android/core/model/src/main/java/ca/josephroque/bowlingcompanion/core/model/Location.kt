package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class Location(
	val id: UUID,
	val title: String,
	val subtitle: String,
	val latitude: Double,
	val longitude: Double,
)