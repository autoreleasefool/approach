import ExtensionsLibrary
import GRDB
import SharedModelsLibrary

extension Team: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw ValidationError.usingPlaceholderId }
	}
}
