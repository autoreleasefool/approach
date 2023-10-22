import Foundation
import GRDB
import ModelsLibrary

extension Bowler {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
		public var kind: Kind
		public var isArchived: Bool

		public init(
			id: Bowler.ID,
			name: String,
			kind: Kind,
			isArchived: Bool
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.isArchived = isArchived
		}
	}
}

extension Bowler.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "bowler"
}

extension Bowler.Kind: DatabaseValueConvertible {}

extension Bowler.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let kind = Column(CodingKeys.kind)
		public static let isArchived = Column(CodingKeys.isArchived)
	}
}

extension DerivableRequest<Bowler.Database> {
	public func orderByName() -> Self {
		let name = Bowler.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	public func filter(byKind: Bowler.Kind?) -> Self {
		guard let byKind else { return self }
		let kind = Bowler.Database.Columns.kind
		return filter(kind == byKind)
	}

	public func isNotArchived() -> Self {
		let isArchived = Bowler.Database.Columns.isArchived
		return filter(isArchived == false)
	}

	public func isArchived() -> Self {
		let isArchived = Bowler.Database.Columns.isArchived
		return filter(isArchived == true)
	}
}

extension Bowler.Summary: FetchableRecord {}

extension Bowler.Opponent: FetchableRecord {}

extension Bowler.List: FetchableRecord {}

extension Bowler.OpponentDetails: FetchableRecord {}

extension Bowler.Archived: FetchableRecord {}
