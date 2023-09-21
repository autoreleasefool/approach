import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct Fouls: Statistic, TrackablePerFrame, PercentageStatistic {
		public static var title: String { Strings.Statistics.Title.fouls }
		public static var category: StatisticCategory { .fouls }
		public static var preferredTrendDirection: StatisticTrendDirection? { .downwards }

		public static var numeratorTitle: String { Strings.Statistics.Title.fouls }
		public static var denominatorTitle: String { Strings.Statistics.Title.totalRolls }
		public static var includeNumeratorInFormattedValue: Bool { false }

		private var totalRolls = 0
		private var fouls = 0

		public var numerator: Int {
			get { fouls }
			set { fouls = newValue }
		}

		public var denominator: Int {
			get { totalRolls }
			set { totalRolls = newValue }
		}

		public init() {}
		init(fouls: Int, totalRolls: Int) {
			self.fouls = fouls
			self.totalRolls = totalRolls
		}

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			self.totalRolls += byFrame.rolls.count
			self.fouls += byFrame.rolls.filter(\.roll.didFoul).count
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}
