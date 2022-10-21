import SharedModelsLibrary

extension Game {
	public struct FetchRequest {
		public var series: Series.ID
		public var ordering: Ordering

		public init(series: Series.ID, ordering: Ordering) {
			self.series = series
			self.ordering = ordering
		}
	}
}

extension Game.FetchRequest {
	public enum Ordering {
		case byOrdinal
	}
}
