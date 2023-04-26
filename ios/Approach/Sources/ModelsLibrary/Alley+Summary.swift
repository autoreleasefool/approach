extension Alley {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String
		public let material: Material?
		public let pinFall: PinFall?
		public let mechanism: Mechanism?
		public let pinBase: PinBase?
		public let location: Location.Summary?
	}
}
