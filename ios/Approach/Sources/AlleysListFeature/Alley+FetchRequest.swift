import AlleysRepositoryInterface
import ModelsLibrary

extension Alley.Summary {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Alley.Ordering

		public init(filter: Filter, ordering: Alley.Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.Summary.FetchRequest {
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
