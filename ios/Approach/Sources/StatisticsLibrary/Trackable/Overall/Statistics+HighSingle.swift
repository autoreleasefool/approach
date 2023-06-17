import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HighSingle: Statistic, GraphableStatistic, TrackablePerGame, GraphablePerGame {
		public static let title = Strings.Statistics.Title.highSingle
		public static let category: StatisticCategory = .overall

		private var highSingle: Int
		public var value: String { String(highSingle) }
		public var trackedValue: TrackedValue { .init(highSingle) }
		public var isEmpty: Bool { highSingle == 0 }

		public init() {
			self.init(highSingle: 0)
		}

		public init(highSingle: Int) {
			self.highSingle = highSingle
		}

		public mutating func adjust(byGame: Game.TrackableEntry, configuration: TrackablePerGameConfiguration) {
			highSingle = max(highSingle, byGame.score)
		}

		public mutating func accumulate(by: any GraphableStatistic) {
			guard let by = by as? Self else { return }
			self.highSingle = max(by.highSingle, self.highSingle)
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}
