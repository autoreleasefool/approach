import SharedModelsLibrary

extension Bowler {
	public struct FetchRequest {
		public let ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Bowler.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
