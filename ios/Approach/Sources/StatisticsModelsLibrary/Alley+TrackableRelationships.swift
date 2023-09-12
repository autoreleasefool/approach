import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Alley.Database {
	public static func trackableSeries(
		filter: TrackableFilter.SeriesFilter?
	) -> HasManyAssociation<Self, Series.Database> {
		Series.Database.applyFilter(filter, toAssociation: hasMany(Series.Database.self))
	}

	public static func trackableGames(
		through series: HasManyAssociation<Self, Series.Database>,
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
