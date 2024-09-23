import IdentifiedCollections
import LanesRepositoryInterface
import LocationsRepositoryInterface
import ModelsLibrary

extension Alley {
	public struct Edit: Identifiable, Codable, Equatable, Sendable {
		public let id: Alley.ID
		public var name: String
		public var material: Material?
		public var pinFall: PinFall?
		public var mechanism: Mechanism?
		public var pinBase: PinBase?
		public var location: Location.Edit?

		public static let placeholder = Edit(id: Alley.ID(), name: "")
	}
}

extension Alley {
	public struct EditWithLanes: Equatable, Codable, Sendable {
		public var alley: Edit
		public var lanes: IdentifiedArrayOf<Lane.Edit>

		public static let placeholder = EditWithLanes(alley: .placeholder, lanes: [])
	}
}
