import Foundation
import GRDB
import ModelsLibrary

extension Gear {
	public struct Database: Sendable, Identifiable, Codable, Equatable, TableRecord {
		public static let databaseTableName = "gear"

		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var bowlerId: Bowler.ID?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			bowlerId: Bowler.ID?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.bowlerId = bowlerId
		}
	}
}

extension Gear.Kind: DatabaseValueConvertible {}

extension Gear.Database: FetchableRecord, PersistableRecord {
	public static let bowler = belongsTo(Bowler.Database.self)
}

extension Gear.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let kind = Column(CodingKeys.kind)
		public static let bowlerId = Column(CodingKeys.bowlerId)
	}
}

extension Gear.Summary: TableRecord, FetchableRecord, EncodableRecord {
	public static let databaseTableName = Gear.Database.databaseTableName
}
