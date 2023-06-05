import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct HeadPins: Statistic, TrackablePerFrame {
		public static let title = Strings.Statistics.Title.headPins
		public static let category: StatisticCategory = .onFirstRoll

		private var headPins: Int
		public var value: String { String(headPins) }

		public init() {
			self.init(headPins: 0)
		}

		public init(headPins: Int) {
			self.headPins = headPins
		}

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			for roll in byFrame.firstRolls {
				if roll.roll.pinsDowned.isHeadPin || (configuration.countHeadPin2AsHeadPin && roll.roll.pinsDowned.isHeadPin2) {
					headPins += 1
				}
			}
		}
	}
}
