import Foundation
import SharedModelsLibrary
import StringsLibrary

extension Average {
	public struct FetchRequest: Equatable {
		public let entityIds: EntityIDs

		public init(entityIds: EntityIDs) {
			self.entityIds = entityIds
		}
	}
}

extension Average.FetchRequest {
	public enum EntityIDs: Hashable {
		case bowlers([Bowler.ID])
		case alleys([Alley.ID])
		case gear([Gear.ID])
		case lanes([Lane.ID])
		case leagues([League.ID])
		case series([Series.ID])
	}
}
