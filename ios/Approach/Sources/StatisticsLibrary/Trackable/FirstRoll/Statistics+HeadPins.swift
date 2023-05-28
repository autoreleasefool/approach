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
			guard let pinsDowned = byFrame.rolls.first?.roll.pinsDowned else { return }
			if pinsDowned.isHeadPin || (configuration.countHeadPin2AsHeadPin && pinsDowned.isHeadPin2) {
				headPins += 1
			}
		}
	}
}
