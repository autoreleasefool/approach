import Foundation
import GRDB
import ModelsLibrary

extension Alley {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "alley"

		public let id: Alley.ID
		public var name: String
		public var material: Material?
		public var pinFall: PinFall?
		public var mechanism: Mechanism?
		public var pinBase: PinBase?
		public var locationId: Location.ID?

		public init(
			id: Alley.ID,
			name: String,
			material: Material?,
			pinFall: PinFall?,
			mechanism: Mechanism?,
			pinBase: PinBase?,
			locationId: Location.ID?
		) {
			self.id = id
			self.name = name
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
			self.locationId = locationId
		}
	}
}

extension Alley.Material: DatabaseValueConvertible {}
extension Alley.PinFall: DatabaseValueConvertible {}
extension Alley.Mechanism: DatabaseValueConvertible {}
extension Alley.PinBase: DatabaseValueConvertible {}

extension Alley.Database: FetchableRecord, PersistableRecord {
	public static let lanes = hasMany(Lane.Database.self)
	public var lanes: QueryInterfaceRequest<Lane.Database> {
		request(for: Self.lanes)
	}

	public static let location = belongsTo(Location.Database.self)
	public var location: QueryInterfaceRequest<Location.Database> {
		request(for: Self.location)
	}
}

extension Alley.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let material = Column(CodingKeys.material)
		public static let pinFall = Column(CodingKeys.pinFall)
		public static let mechanism = Column(CodingKeys.mechanism)
		public static let pinBase = Column(CodingKeys.pinBase)
		public static let locationId = Column(CodingKeys.locationId)
	}
}

extension Alley.Summary: TableRecord, FetchableRecord {
	public static let databaseTableName = Alley.Database.databaseTableName
}
