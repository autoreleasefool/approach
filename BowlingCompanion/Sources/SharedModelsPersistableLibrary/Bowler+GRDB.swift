import ExtensionsLibrary
import GRDB
import SharedModelsLibrary

extension Bowler: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw ValidationError.usingPlaceholderId }
	}
}
