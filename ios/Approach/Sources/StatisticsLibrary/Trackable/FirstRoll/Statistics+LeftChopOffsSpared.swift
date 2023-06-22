import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct LeftChopOffsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.leftChopOffsSpared }
		public static var category: StatisticCategory { .onFirstRoll }

		public static var denominatorTitle: String { Strings.Statistics.Title.leftChopOffs }

		private var leftChopOffs = 0
		private var leftChopOffsSpared = 0

		public var numerator: Int {
			get { leftChopOffsSpared }
			set { leftChopOffsSpared = newValue }
		}

		public var denominator: Int {
			get { leftChopOffs }
			set { leftChopOffs = newValue }
		}

		public init() {}
		init(leftChopOffs: Int, leftChopOffsSpared: Int) {
			self.leftChopOffs = leftChopOffs
			self.leftChopOffsSpared = leftChopOffsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isLeftChopOff {
				leftChopOffs += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					leftChopOffsSpared += 1
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
