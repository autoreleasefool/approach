import Foundation
import GRDB
import ModelsLibrary

extension Alley {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
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

extension Alley.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "alley"
}

extension Alley.Material: DatabaseValueConvertible {}
extension Alley.PinFall: DatabaseValueConvertible {}
extension Alley.Mechanism: DatabaseValueConvertible {}
extension Alley.PinBase: DatabaseValueConvertible {}

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

extension DerivableRequest<Alley.Database> {
	public func orderByName() -> Self {
		let name = Alley.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	public func filter(
		_ material: Alley.Material?,
		_ pinFall: Alley.PinFall?,
		_ mechanism: Alley.Mechanism?,
		_ pinBase: Alley.PinBase?
	) -> Self {
		var query = self
		if let material {
			query = query.filter(Alley.Database.Columns.material == material)
		}
		if let pinFall {
			query = query.filter(Alley.Database.Columns.pinFall == pinFall)
		}
		if let mechanism {
			query = query.filter(Alley.Database.Columns.mechanism == mechanism)
		}
		if let pinBase {
			query = query.filter(Alley.Database.Columns.pinBase == pinBase)
		}
		return query
	}
}

extension Alley.Summary: FetchableRecord {}

extension Alley.List: FetchableRecord {}
