import AlleysRepositoryInterface
import ModelsLibrary

extension Alley.List {
	public struct FetchRequest: Equatable {
		public let filter: Filter
		public let ordering: Alley.Ordering

		public init(filter: Filter, ordering: Alley.Ordering) {
			self.filter = filter
			self.ordering = ordering
		}
	}
}

extension Alley.List.FetchRequest {
	public struct Filter: Hashable {
		public var material: Alley.Material?
		public var pinFall: Alley.PinFall?
		public var pinBase: Alley.PinBase?
		public var mechanism: Alley.Mechanism?

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
