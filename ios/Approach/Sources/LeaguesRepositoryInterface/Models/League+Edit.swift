import ModelsLibrary

extension League {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: League.ID

		public var name: String
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var location: Alley.Summary?
	}
}
