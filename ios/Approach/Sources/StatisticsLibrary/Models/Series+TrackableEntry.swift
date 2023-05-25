import ModelsLibrary

extension Series {
	public struct TrackableEntry: Identifiable, Decodable {
		public let id: Series.ID
		public let numberOfGames: Int
		public let total: Int
	}
}
