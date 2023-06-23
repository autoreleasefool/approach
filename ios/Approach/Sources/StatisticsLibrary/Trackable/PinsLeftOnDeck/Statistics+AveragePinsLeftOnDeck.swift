import ModelsLibrary
import StringsLibrary

extension Statistics {
	public struct AveragePinsLeftOnDeck: Statistic, TrackablePerFrame, AveragingStatistic {
		public static var title: String { Strings.Statistics.Title.averagePinsLeftOnDeck }
		public static var category: StatisticCategory { .pinsLeftOnDeck }

		private var totalPinsLeftOnDeck = 0
		private var gamesPlayed: Set<Game.ID> = []

		public var total: Int {
			get { totalPinsLeftOnDeck }
			set { totalPinsLeftOnDeck = newValue }
		}

		public var divisor: Int {
			get { gamesPlayed.count }
			set { fatalError("Cannot set divisor for \(Self.self)") }
		}

		public init() {}
		init(totalPinsLeftOnDeck: Int, gamesPlayed: Set<Game.ID>) {
			self.totalPinsLeftOnDeck = totalPinsLeftOnDeck
			self.gamesPlayed = gamesPlayed
		}

		public mutating func adjust(byFrame: Frame.TrackableEntry, configuration: TrackablePerFrameConfiguration) {
			totalPinsLeftOnDeck += byFrame.pinsLeftOnDeck.value
			gamesPlayed.insert(byFrame.gameId)
		}

		public mutating func aggregate(with: Statistic) {
			guard let with = with as? Self else { return }
			self.totalPinsLeftOnDeck += with.totalPinsLeftOnDeck
			self.gamesPlayed.formUnion(with.gamesPlayed)
		}

		public static func supports(trackableSource: TrackableFilter.Source) -> Bool {
			switch trackableSource {
			case .bowler, .league, .series: return true
			case .game: return false
			}
		}
	}
}
