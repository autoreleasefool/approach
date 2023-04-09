import ModelsLibrary

extension Alley {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Ordering

		public init(filter: Filter, ordering: Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.FetchRequest {
	public struct Filter: Equatable {
		public let material: Alley.Material?
		public let pinFall: Alley.PinFall?
		public let pinBase: Alley.PinBase?
		public let mechanism: Alley.Mechanism?

		public init(
			material: Alley.Material? = nil,
			pinFall: Alley.PinFall? = nil,
			mechanism: Alley.Mechanism? = nil,
			pinBase: Alley.PinBase? = nil
		) {
			self.material = material
			self.pinFall = pinFall
			self.pinBase = pinBase
			self.mechanism = mechanism
		}
	}
}

extension Alley.FetchRequest {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}
