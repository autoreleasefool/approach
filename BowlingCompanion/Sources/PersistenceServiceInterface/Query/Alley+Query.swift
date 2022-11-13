import SharedModelsLibrary

extension Alley {
	public struct Query {
		public let ordering: Ordering

		public init(ordering: Ordering) {
			self.ordering = ordering
		}
	}
}

extension Alley.Query {
	public enum Ordering {
		case byName
	}
}
