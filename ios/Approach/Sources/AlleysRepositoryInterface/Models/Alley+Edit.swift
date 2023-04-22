import IdentifiedCollections
import LanesRepositoryInterface
import ModelsLibrary

extension Alley {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public var name: String
		public var address: String?
		public var material: Material?
		public var pinFall: PinFall?
		public var mechanism: Mechanism?
		public var pinBase: PinBase?
	}
}

extension Alley {
	public struct EditWithLanes: Equatable, Codable {
		public var alley: Edit
		public var lanes: IdentifiedArrayOf<Lane.Edit>
	}
}
