import ModelsLibrary

extension League {
	public struct Edit: Identifiable, Equatable, Codable, Sendable {
		public let id: League.ID
		public let bowlerId: Bowler.ID
		public let recurrence: League.Recurrence

		public var name: String
		public var defaultNumberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var location: Alley.Summary?

		public var asSeriesHost: SeriesHost {
			.init(
				id: id,
				bowlerId: bowlerId,
				name: name,
				defaultNumberOfGames: defaultNumberOfGames,
				alley: location,
				excludeFromStatistics: excludeFromStatistics,
				recurrence: recurrence
			)
		}

		public static let placeholder = Edit(
			id: League.ID(),
			bowlerId: Bowler.ID(),
			recurrence: .repeating,
			name: "",
			defaultNumberOfGames: nil,
			excludeFromStatistics: .include
		)
	}
}
