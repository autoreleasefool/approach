import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct SpareConversions: Statistic, TrackablePerSecondRoll, SecondRollStatistic {
		public static var title: String { Strings.Statistics.Title.spareConversions }
		public static var category: StatisticCategory { .overall }

		public static var denominatorTitle: String { Strings.Statistics.Title.spareChances }

		private var spareChances = 0
		private var spares = 0

		public var numerator: Int {
			get { spares }
			set { spares = newValue }
		}

		public var denominator: Int {
			get { spareChances }
			set { spareChances = newValue }
		}

		public init() {}
		init(spares: Int, spareChances: Int) {
			self.spares = spares
			self.spareChances = spareChances
		}

		public mutating func adjust(
			bySecondRoll: Frame.OrderedRoll,
			afterFirstRoll: Frame.OrderedRoll,
			configuration: TrackablePerFrameConfiguration
		) {
			let firstRoll = afterFirstRoll.roll.pinsDowned
			let didSpare = bySecondRoll.roll.pinsDowned.union(firstRoll).arePinsCleared

			// Don't add a spare chance if the first ball was a split / head pin / aces, unless the second shot was a spare
			guard didSpare || !(firstRoll.isAce || firstRoll.isSplit || firstRoll.isHeadPin || firstRoll.isHeadPin2) else {
				return
			}

			spareChances += 1
			if didSpare {
				spares += 1
			}
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series, .game: return true
			}
		}
	}
}
