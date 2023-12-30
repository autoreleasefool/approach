import Foundation
import IdentifiedCollections
import ModelsLibrary

public struct TrackableFilter: Equatable {
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
	public enum Source: Equatable, Codable {
		case bowler(Bowler.ID)
		case league(League.ID)
		case series(Series.ID)
		case game(Game.ID)
	}
}

extension TrackableFilter {
	public struct Sources: Equatable {
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
	}
}

// MARK: - Leagues

extension TrackableFilter {
	public struct LeagueFilter: Equatable {
		public var recurrence: League.Recurrence?

		public init(recurrence: League.Recurrence? = nil) {
			self.recurrence = recurrence
		}
	}
}

// MARK: - Series

extension TrackableFilter {
	public struct SeriesFilter: Equatable {
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
	public enum AlleyFilter: Equatable {
		case alley(Alley.Named)
		case properties(Properties)
	}
}

extension TrackableFilter.SeriesFilter.AlleyFilter {
	public struct Properties: Equatable {
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
	public struct GameFilter: Equatable {
		public var lanes: LaneFilter?
		public var gearUsed: IdentifiedArrayOf<Gear.Summary>
		public var opponent: Bowler.Summary?

		public init(
			lanes: LaneFilter? = nil,
			gearUsed: IdentifiedArrayOf<Gear.Summary> = [],
			opponent: Bowler.Summary? = nil
		) {
			self.lanes = lanes
			self.gearUsed = gearUsed
			self.opponent = opponent
		}
	}
}

extension TrackableFilter.GameFilter {
	public enum LaneFilter: Equatable {
		case lanes(IdentifiedArrayOf<Lane.Summary>)
		case positions([Lane.Position])
	}
}

// MARK: - Frames

extension TrackableFilter {
	public struct FrameFilter: Equatable {
		public var bowlingBallsUsed: IdentifiedArrayOf<Gear.Summary>

		public init(bowlingBallsUsed: IdentifiedArrayOf<Gear.Summary> = []) {
			self.bowlingBallsUsed = bowlingBallsUsed
		}
	}
}

// MARK: - TimePrecision

extension TrackableFilter {
	public enum Aggregation: Int, Equatable, Identifiable, CaseIterable {
		case accumulate
		case periodic

		public var id: Int { rawValue }
	}
}
