extension SeriesLane {
	public struct Summary: Codable, Equatable {
		public let seriesId: Series.ID
		public let laneId: Lane.ID
	}
}
