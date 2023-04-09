import ModelsLibrary

extension Bowler {
	public struct FetchRequest: Equatable {
		public let ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Bowler.FetchRequest {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}
