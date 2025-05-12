import GRDB
import ModelsLibrary

extension DerivableRequest where RowDecoder: Archivable {
	static var archivedOn: Column { Column("archivedOn") }

	public func isArchived() -> Self {
		filter(Self.archivedOn != nil)
	}

	public func isNotArchived() -> Self {
		filter(Self.archivedOn == nil)
	}

	public func orderByArchivedDate() -> Self {
		order(Self.archivedOn.desc)
	}
}
