package ca.josephroque.bowlingcompanion.core.analytics.trackable.app

import ca.josephroque.bowlingcompanion.core.analytics.TrackableEvent

// DidRequireIssue589Backup indicates that a backup was required for the legacy migration
data class AppLegacyMigrationCompleted(val didRequireIssue589Backup: Boolean) : TrackableEvent {
	override val name = "App.LegacyMigrationCompleted"
	override val payload: Map<String, String>
		get() = mapOf(
			"DidRequireIssue589Backup" to didRequireIssue589Backup.toString(),
		)
}
