import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct StrikeMiddleHits: Statistic, TrackablePerFrame, PercentageStatistic {
		public static var title: String { Strings.Statistics.Title.strikeMiddleHits }
		public static var category: StatisticCategory { .overall }

		public static var numeratorTitle: String { Strings.Statistics.Title.strikeMiddleHits }
		public static var denominatorTitle: String { Strings.Statistics.Title.middleHits }
		public static var includeNumeratorInFormattedValue: Bool { true }

		private var middleHits = 0
		private var strikes = 0

		public var numerator: Int {
			get { strikes }
			set { strikes = newValue }
		}

		public var denominator: Int {
			get { middleHits }
			set { middleHits = newValue }
		}

		public init() {}
		init(middleHits: Int, strikes: Int) {
			self.middleHits = middleHits
			self.strikes = strikes
		}

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			for roll in byFrame.firstRolls where roll.roll.pinsDowned.isMiddleHit {
				denominator += 1

				if roll.roll.pinsDowned.arePinsCleared {
					numerator += 1
				}
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}
