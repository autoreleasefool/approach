import SharedModelsLibrary

extension Game {
	public struct FetchRequest {
		public var series: Series.ID

		public init(series: Series.ID) {
			self.series = series
		}
	}
}
