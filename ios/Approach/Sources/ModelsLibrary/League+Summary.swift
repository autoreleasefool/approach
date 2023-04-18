extension League {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let name: String
	}
}

extension League {
	public struct SeriesHost: Identifiable, Codable, Equatable {
		public let id: League.ID
		public let numberOfGames: Int?
		public let alley: Alley.Summary?
	}
}
