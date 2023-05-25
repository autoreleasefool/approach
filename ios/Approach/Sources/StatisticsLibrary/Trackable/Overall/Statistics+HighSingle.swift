import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSingle: Statistic, TrackablePerGame {
		public static let title = Strings.Statistics.Title.highSingle
		public static let category: StatisticCategory = .overall

		private var highSingle: Int = 0
		public var value: String { String(highSingle) }

		public init() {}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			highSingle = max(highSingle, byGame.score)
		}
	}
}
