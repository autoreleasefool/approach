import Foundation
import IdentifiedCollections
import ModelsLibrary

extension Alley {
	public typealias Create = Edit
}

extension Alley.Create {
	public static func `default`(withId: UUID) -> Self {
		.init(id: withId, name: "")
	}
}

extension Alley {
	public struct CreateWithLanes: Equatable {
		public var alley: Create
		public var lanes: IdentifiedArrayOf<Lane.Summary>

		public init(alley: Create, lanes: IdentifiedArrayOf<Lane.Summary>) {
			self.alley = alley
			self.lanes = lanes
		}
	}
}
