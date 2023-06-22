import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct RightChopOffsSpared: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.rightChopOffsSpared }
		public static var category: StatisticCategory { .onFirstRoll }

		public static var denominatorTitle: String { Strings.Statistics.Title.rightChopOffs }

		private var rightChopOffs = 0
		private var rightChopOffsSpared = 0

		public var numerator: Int {
			get { rightChopOffsSpared }
			set { rightChopOffsSpared = newValue }
		}

		public var denominator: Int {
			get { rightChopOffs }
			set { rightChopOffs = newValue }
		}

		public init() {}
		init(rightChopOffs: Int, rightChopOffsSpared: Int) {
			self.rightChopOffs = rightChopOffs
			self.rightChopOffsSpared = rightChopOffsSpared
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned

			if firstRoll.isRightChopOff {
				rightChopOffs += 1

				if bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared {
					rightChopOffsSpared += 1
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
