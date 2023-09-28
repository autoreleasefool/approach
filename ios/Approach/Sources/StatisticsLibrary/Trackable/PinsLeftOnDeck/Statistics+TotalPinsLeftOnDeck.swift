import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct TotalPinsLeftOnDeck: Statistic, TrackablePerFrame, CountingStatistic {
		public static var title: String { Strings.Statistics.Title.totalPinsLeftOnDeck }
		public static var category: StatisticCategory { .pinsLeftOnDeck }
		public static var preferredTrendDirection: StatisticTrendDirection? { .none }

		private var totalPinsLeftOnDeck = 0
		public var count: Int {
			get { totalPinsLeftOnDeck }
			set { totalPinsLeftOnDeck = newValue }
		}

		public init() {}
		init(totalPinsLeftOnDeck: Int) { self.totalPinsLeftOnDeck = totalPinsLeftOnDeck }

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			guard !byFrame.rolls.isEmpty else { return }
			totalPinsLeftOnDeck += byFrame.pinsLeftOnDeck.value
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}
