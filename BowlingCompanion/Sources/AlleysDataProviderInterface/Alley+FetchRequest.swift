import SharedModelsLibrary

extension Alley {
	public struct FetchRequest {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter] = [], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.FetchRequest {
	public enum Filter: Equatable {
		case id(Alley.ID)
		case material(Alley.Material)
		case pinFall(Alley.PinFall)
		case pinBase(Alley.PinBase)
		case mechanism(Alley.Mechanism)
		case name(String)
	}
}

extension Alley.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
