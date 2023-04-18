import ExtensionsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Alley {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "alley"

		public let id: Alley.ID
		public var name: String
		public var address: String?
		public var material: Material?
		public var pinFall: PinFall?
		public var mechanism: Mechanism?
		public var pinBase: PinBase?

		public init(
			id: Alley.ID,
			name: String,
			address: String?,
			material: Material?,
			pinFall: PinFall?,
			mechanism: Mechanism?,
			pinBase: PinBase?
		) {
			self.id = id
			self.name = name
			self.address = address
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
		}
	}
}

extension Alley.Material: DatabaseValueConvertible {}
extension Alley.PinFall: DatabaseValueConvertible {}
extension Alley.Mechanism: DatabaseValueConvertible {}
extension Alley.PinBase: DatabaseValueConvertible {}

extension Alley.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw PlaceholderIDValidationError() }
	}
}

extension Alley.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let address = Column(CodingKeys.address)
		public static let material = Column(CodingKeys.material)
		public static let pinFall = Column(CodingKeys.pinFall)
		public static let mechanism = Column(CodingKeys.mechanism)
		public static let pinBase = Column(CodingKeys.pinBase)
	}
}

extension Alley.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}
