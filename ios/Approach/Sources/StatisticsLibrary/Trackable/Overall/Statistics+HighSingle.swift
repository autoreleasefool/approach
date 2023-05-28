import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSingle: Statistic, TrackablePerGame {
		public static let title = Strings.Statistics.Title.highSingle
		public static let category: StatisticCategory = .overall

		private var highSingle: Int
		public var value: String { String(highSingle) }

		public init() {
			self.init(highSingle: 0)
		}

		public init(highSingle: Int) {
			self.highSingle = highSingle
		}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			highSingle = max(highSingle, byGame.score)
		}
	}
}
