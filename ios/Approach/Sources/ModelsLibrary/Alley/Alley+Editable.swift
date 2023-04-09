extension Alley {
	public struct Editable: Identifiable, Equatable {
		public let id: Alley.ID
		public var name: String
		public var address: String
		public var material: Material?
		public var pinFall: PinFall?
		public var mechanism: Mechanism?
		public var pinBase: PinBase?

		public init(
			id: Alley.ID,
			name: String,
			address: String,
			material: Material?,
			pinFall: PinFall?,
			mechanism: Mechanism?,
			pinBase: PinBase?
		) {
			self.id = id
			self.name = name
			self.address = address
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
		}
	}
}
