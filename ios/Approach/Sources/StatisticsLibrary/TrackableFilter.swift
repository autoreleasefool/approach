import Foundation
import IdentifiedCollections
import ModelsLibrary

public struct TrackableFilter: Codable, Hashable, Sendable {
	public var source: Source
	public var leagueFilter: LeagueFilter
	public var seriesFilter: SeriesFilter
	public var gameFilter: GameFilter
	public var frameFilter: FrameFilter
	public var aggregation: Aggregation

	public init(
		source: Source,
		leagueFilter: LeagueFilter = .init(),
		seriesFilter: SeriesFilter = .init(),
		gameFilter: GameFilter = .init(),
		frameFilter: FrameFilter = .init(),
		aggregation: Aggregation = .accumulate
	) {
		self.source = source
		self.leagueFilter = leagueFilter
		self.seriesFilter = seriesFilter
		self.gameFilter = gameFilter
		self.frameFilter = frameFilter
		self.aggregation = aggregation
	}

	public var isTooNarrowForCharts: Bool {
		switch source {
		case .series, .game:
			return true
		case .bowler, .league:
			return false
		}
	}
}

// MARK: - Source

extension TrackableFilter {
	public enum Source: Codable, Hashable, Sendable {
		case bowler(Bowler.ID)
		case league(League.ID)
		case series(Series.ID)
		case game(Game.ID)

		public var id: UUID {
			switch self {
			case let .bowler(id), let .league(id), let .series(id), let .game(id):
				return id
			}
		}
	}
}

extension TrackableFilter {
	public struct Sources: Equatable, Sendable {
		public var bowler: Bowler.Summary
		public var league: League.Summary?
		public var series: Series.Summary?
		public var game: Game.Summary?

		public init(bowler: Bowler.Summary, league: League.Summary?, series: Series.Summary?, game: Game.Summary?) {
			self.bowler = bowler
			self.league = league
			self.series = series
			self.game = game
		}

		public var primaryId: UUID {
			game?.id ?? series?.id ?? league?.id ?? bowler.id
		}

		public static let placeholder = Sources(
			bowler: Bowler.Summary(id: Bowler.ID(), name: ""),
			league: nil,
			series: nil,
			game: nil
		)
	}
}

extension TrackableFilter.Source {
	public var widgetSource: StatisticsWidget.Source? {
		switch self {
		case let .bowler(id): .bowler(id)
		case let .league(id): .league(id)
		case .game, .series: nil
		}
	}
}

extension StatisticsWidget.Source {
	public var trackableSource: TrackableFilter.Source {
		switch self {
		case let .bowler(id): .bowler(id)
		case let .league(id): .league(id)
		}
	}
}

// MARK: - Leagues

extension TrackableFilter {
	public struct LeagueFilter: Codable, Hashable, Sendable {
		public var recurrence: League.Recurrence?

		public init(recurrence: League.Recurrence? = nil) {
			self.recurrence = recurrence
		}
	}
}

// MARK: - Series

extension TrackableFilter {
	public struct SeriesFilter: Codable, Hashable, Sendable {
		public var startDate: Date?
		public var endDate: Date?
		public var alley: AlleyFilter?

		public init(startDate: Date? = nil, endDate: Date? = nil, alley: AlleyFilter? = nil) {
			self.startDate = startDate
			self.endDate = endDate
			self.alley = alley
		}
	}
}

extension TrackableFilter.SeriesFilter {
	public enum AlleyFilter: Codable, Hashable, Sendable {
		case alley(Alley.Named)
		case properties(Properties)
	}
}

extension TrackableFilter.SeriesFilter.AlleyFilter {
	public struct Properties: Codable, Hashable, Sendable {
		public var material: Alley.Material?
		public var pinFall: Alley.PinFall?
		public var pinBase: Alley.PinBase?
		public var mechanism: Alley.Mechanism?

		public init(
			material: Alley.Material? = nil,
			pinFall: Alley.PinFall? = nil,
			mechanism: Alley.Mechanism? = nil,
			pinBase: Alley.PinBase? = nil
		) {
			self.material = material
			self.pinFall = pinFall
			self.pinBase = pinBase
			self.mechanism = mechanism
		}
	}
}

// MARK: - Games

extension TrackableFilter {
	public struct GameFilter: Codable, Hashable, Sendable {
		public var lanes: LaneFilter?
		public var gearUsed: IdentifiedArrayOf<Gear.Summary>
		public var opponent: Bowler.Summary?
		public var includingExcluded: Bool

		public init(
			lanes: LaneFilter? = nil,
			gearUsed: IdentifiedArrayOf<Gear.Summary> = [],
			opponent: Bowler.Summary? = nil,
			includingExcluded: Bool = false
		) {
			self.lanes = lanes
			self.gearUsed = gearUsed
			self.opponent = opponent
			self.includingExcluded = includingExcluded
		}
	}
}

extension TrackableFilter.GameFilter {
	public enum LaneFilter: Codable, Hashable, Sendable {
		case lanes(IdentifiedArrayOf<Lane.Summary>)
		case positions([Lane.Position])
	}
}

// MARK: - Frames

extension TrackableFilter {
	public struct FrameFilter: Codable, Hashable, Sendable {
		public var bowlingBallsUsed: IdentifiedArrayOf<Gear.Summary>

		public init(bowlingBallsUsed: IdentifiedArrayOf<Gear.Summary> = []) {
			self.bowlingBallsUsed = bowlingBallsUsed
		}
	}
}

// MARK: - TimePrecision

extension TrackableFilter {
	public enum Aggregation: Int, Codable, Hashable, Identifiable, CaseIterable, Sendable {
		case accumulate
		case periodic

		public var id: Int { rawValue }
	}
}
