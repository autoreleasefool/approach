import SharedModelsLibrary

extension Alley {
	public struct SingleFetchRequest: Equatable {
		public let filter: Filter

		public init(filter: Filter) {
			self.filter = filter
		}
	}
}

extension Alley.SingleFetchRequest {
	public enum Filter: Equatable {
		case id(Alley.ID)
	}
}

// MARK: - FetchRequest

extension Alley {
	public struct FetchRequest: Equatable {
		public let filter: Filter?
		public let ordering: Ordering

		public init(filter: Filter?, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.FetchRequest {
	public enum Filter: Equatable {
		case properties(
			material: Alley.Material?,
			pinFall: Alley.PinFall?,
			pinBase: Alley.PinBase?,
			mechanism: Alley.Mechanism?
		)
		case name(String)
	}
}

extension Alley.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
