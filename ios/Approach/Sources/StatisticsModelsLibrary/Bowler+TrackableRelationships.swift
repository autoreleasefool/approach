import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Bowler.Database {
	public static func trackableLeagues(
		filter: TrackableFilter.LeagueFilter?
	) -> HasManyAssociation<Self, League.Database> {
		var association = hasMany(League.Database.self)
			.filter(League.Database.Columns.excludeFromStatistics == League.ExcludeFromStatistics.include)

		if let recurrence = filter?.recurrence {
			association = association
				.filter(League.Database.Columns.recurrence == recurrence)
		}

		return association
	}

	public static func trackableSeries(
		through leagues: HasManyAssociation<Self, League.Database>,
		filter: TrackableFilter.SeriesFilter?
	) -> HasManyThroughAssociation<Self, Series.Database> {
		hasMany(
			Series.Database.self,
			through: leagues,
			using: League.Database.trackableSeries(filter: filter)
		)
	}

	public static func trackableGames(
		through series: HasManyThroughAssociation<Self, Series.Database>,
		filter: TrackableFilter.GameFilter?
	) -> HasManyThroughAssociation<Self, Game.Database> {
		hasMany(
			Game.Database.self,
			through: series,
			using: Series.Database.trackableGames(filter: filter)
		)
	}

	public static func trackableFrames(
		through games: HasManyThroughAssociation<Self, Game.Database>,
		filter: TrackableFilter.FrameFilter?
	) -> HasManyThroughAssociation<Self, Frame.Database> {
		hasMany(
			Frame.Database.self,
			through: games.filter(Game.Database.Columns.scoringMethod == Game.ScoringMethod.byFrame),
			using: Game.Database.trackableFrames(filter: filter)
		)
	}
}
