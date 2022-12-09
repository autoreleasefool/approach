import SharedModelsLibrary

extension Lane {
	public struct FetchRequest: Equatable {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Lane.FetchRequest {
	public enum Filter: Equatable {
		case id(Lane.ID)
		case alley(Alley.ID)
	}
}

extension Lane.FetchRequest {
	public enum Ordering: Equatable {
		case byLabel
	}
}
