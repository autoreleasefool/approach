import ModelsLibrary

extension Alley {
	public struct Filters: Equatable {
		public let material: Material?
		public let pinFall: PinFall?
		public let mechanism: Mechanism?
		public let pinBase: PinBase?

		public init(
			material: Material? = nil,
			pinFall: PinFall? = nil,
			mechanism: Mechanism? = nil,
			pinBase: PinBase? = nil
		) {
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
		}
	}
}
