import ModelsLibrary

extension League {
	public struct Edit: Identifiable, Equatable, Codable {
		public let id: League.ID
		public let recurrence: League.Recurrence
		public let defaultNumberOfGames: Int?

		public var name: String
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var location: Alley.Summary?

		public var asSeriesHost: SeriesHost {
			.init(
				id: id,
				name: name,
				defaultNumberOfGames: defaultNumberOfGames,
				alley: location,
				excludeFromStatistics: excludeFromStatistics,
				recurrence: recurrence
			)
		}
	}
}
