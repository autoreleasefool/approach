import GRDB
import ModelsLibrary

extension DerivableRequest where RowDecoder: Archivable {
	public func isArchived() -> Self {
		let archivedOn = Column("archivedOn")
		return filter(archivedOn != nil)
	}

	public func isNotArchived() -> Self {
		let archivedOn = Column("archivedOn")
		return filter(archivedOn == nil)
	}

	public func orderByArchivedDate() -> Self {
		let archivedOn = Column("archivedOn")
		return order(archivedOn.desc)
	}
}
