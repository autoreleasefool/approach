import Foundation
import ModelsLibrary

public struct TrackableFilter {
	public var source: Source
	public var leagueFilter: LeagueFilter
	public var seriesFilter: SeriesFilter
	public var gameFilter: GameFilter
	public var frameFilter: FrameFilter

	public init(
		source: Source,
		leagueFilter: LeagueFilter = .init(),
		seriesFilter: SeriesFilter = .init(),
		gameFilter: GameFilter = .init(),
		frameFilter: FrameFilter = .init()
	) {
		self.source = source
		self.leagueFilter = leagueFilter
		self.seriesFilter = seriesFilter
		self.gameFilter = gameFilter
		self.frameFilter = frameFilter
	}
}

extension TrackableFilter {
	public enum Source {
		case bowler(Bowler.ID)
		case league(League.ID)
		case series(Series.ID)
		case game(Game.ID)
	}
}

// MARK: - Leagues

extension TrackableFilter {
	public struct LeagueFilter {
		public var recurrence: League.Recurrence?

		public init(recurrence: League.Recurrence? = nil) {
			self.recurrence = recurrence
		}
	}
}

// MARK: - Series

extension TrackableFilter {
	public struct SeriesFilter {
		public var startDate: Date?
		public var endDate: Date?
		public var alley: Alley.ID?

		public init(startDate: Date? = nil, endDate: Date? = nil, alley: Alley.ID? = nil) {
			self.startDate = startDate
			self.endDate = endDate
			self.alley = alley
		}
	}
}

extension TrackableFilter.SeriesFilter {
	public enum AlleyFilter {
		case alley(Alley.ID)
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
	public struct GameFilter {
		public var lanes: LaneFilter?
		public var gearUsed: Set<Gear.ID>
		public var opponent: Bowler.ID?

		public init(lanes: LaneFilter? = nil, gearUsed: Set<Gear.ID> = [], opponent: Bowler.ID? = nil) {
			self.lanes = lanes
			self.gearUsed = gearUsed
			self.opponent = opponent
		}
	}
}

extension TrackableFilter.GameFilter {
	public enum LaneFilter {
		case lanes(Set<Lane.ID>)
		case positions(Set<Lane.Position>)
	}
}

// MARK: - Frames

extension TrackableFilter {
	public struct FrameFilter {
		public var bowlingBallsUsed: Set<Gear.ID>

		public init(bowlingBallsUsed: Set<Gear.ID> = []) {
			self.bowlingBallsUsed = bowlingBallsUsed
		}
	}
}
