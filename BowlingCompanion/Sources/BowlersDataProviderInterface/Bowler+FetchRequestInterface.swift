import SharedModelsLibrary

extension Bowler {
	public struct FetchRequest {
		public var ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Bowler.FetchRequest {
	public enum Ordering {
		case byLastModified
		case byName
	}
}
