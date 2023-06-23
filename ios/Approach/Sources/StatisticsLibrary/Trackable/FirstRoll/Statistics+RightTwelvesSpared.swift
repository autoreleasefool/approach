import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightTwelvesSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightTwelvesSpared }
		public static var category: StatisticCategory { .onFirstRoll }

		public static var denominatorTitle: String { Strings.Statistics.Title.rightTwelves }

		private var rightTwelves = 0
		private var rightTwelvesSpared = 0

		public var numerator: Int {
			get { rightTwelvesSpared }
			set { rightTwelvesSpared = newValue }
		}

		public var denominator: Int {
			get { rightTwelves }
			set { rightTwelves = newValue }
		}

		public init() {}
		init(rightTwelves: Int, rightTwelvesSpared: Int) {
			self.rightTwelves = rightTwelves
			self.rightTwelvesSpared = rightTwelvesSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRightTwelve {
				rightTwelves += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightTwelvesSpared += 1
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
