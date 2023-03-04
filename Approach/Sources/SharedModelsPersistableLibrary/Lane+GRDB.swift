import ExtensionsLibrary
import GRDB
import SharedModelsLibrary

extension Lane: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw ValidationError.usingPlaceholderId }
	}
}
