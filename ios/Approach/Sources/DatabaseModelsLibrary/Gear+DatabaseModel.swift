import ExtensionsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Gear {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "gear"

		public let id: Gear.ID
		public var name: String
		public var kind: Kind
		public var bowler: Bowler.ID?

		public init(
			id: Gear.ID,
			name: String,
			kind: Kind,
			bowler: Bowler.ID?
		) {
			self.id = id
			self.name = name
			self.kind = kind
			self.bowler = bowler
		}
	}
}

extension Gear.Kind: DatabaseValueConvertible {}

extension Gear.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw DBValidationError.usingPlaceholderId }
	}
}

extension Gear.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let kind = Column(CodingKeys.kind)
		public static let bowler = Column(CodingKeys.bowler)
	}
}
