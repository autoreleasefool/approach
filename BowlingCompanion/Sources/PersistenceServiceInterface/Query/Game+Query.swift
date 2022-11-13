import SharedModelsLibrary

extension Game {
	public struct Query {
		public let series: Series.ID

		public init(series: Series.ID) {
			self.series = series
		}
	}
}
