import Foundation
import GRDB
import ModelsLibrary

extension Bowler {
	public struct Database: Archivable, Sendable, Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public var name: String
		public var kind: Kind
		public var archivedOn: Date?

		public init(
			id: Bowler.ID,
			name: String,
			kind: Kind,
			archivedOn: Date?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.archivedOn = archivedOn
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
		public static let archivedOn = Column(CodingKeys.archivedOn)
	}
}

extension Bowler.Summary: FetchableRecord {}

extension Bowler.Opponent: FetchableRecord {}

extension Bowler.List: FetchableRecord {}

extension Bowler.OpponentDetails: FetchableRecord {}

extension Bowler.Archived: FetchableRecord {}
