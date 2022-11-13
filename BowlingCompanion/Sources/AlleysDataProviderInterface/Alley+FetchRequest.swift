import SharedModelsLibrary

extension Alley {
	public struct FetchRequest {
		public let ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Alley.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
