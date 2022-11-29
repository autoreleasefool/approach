import SharedModelsLibrary

extension Alley {
	public struct Query {
		public let filter: [Filter]
		public let ordering: Ordering

		public init(filter: [Filter], ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.Query {
	public enum Filter {
		case id(Alley.ID)
		case material(Alley.Material)
		case pinFall(Alley.PinFall)
		case pinBase(Alley.PinBase)
		case mechanism(Alley.Mechanism)
		case name(String)
	}
}

extension Alley.Query {
	public enum Ordering {
		case byName
	}
}
