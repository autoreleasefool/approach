import ModelsLibrary

extension League {
	public struct Editable: Identifiable, Equatable, Codable {
		public let bowler: Bowler.ID
		public let id: League.ID
		public var name: String
		public var recurrence: Recurrence
		public var numberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alley: Alley.ID?

		public init(
			bowler: Bowler.ID,
			id: League.ID,
			name: String,
			recurrence: Recurrence,
			numberOfGames: Int?,
			additionalPinfall: Int?,
			additionalGames: Int?,
			excludeFromStatistics: ExcludeFromStatistics,
			alley: Alley.ID?
		) {
			self.bowler = bowler
			self.id = id
			self.name = name
			self.recurrence = recurrence
			self.numberOfGames = numberOfGames
			self.additionalPinfall = additionalPinfall
			self.additionalGames = additionalGames
			self.excludeFromStatistics = excludeFromStatistics
			self.alley = alley
		}
	}
}
