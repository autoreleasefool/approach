extension Alley {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String
		public let address: String?
		public let material: Material?
		public let pinFall: PinFall?
		public let mechanism: Mechanism?
		public let pinBase: PinBase?

		public init(
			id: Alley.ID,
			name: String,
			address: String?,
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
