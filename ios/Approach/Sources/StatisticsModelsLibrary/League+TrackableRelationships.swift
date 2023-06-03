import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension League.Database {
	public static func trackableSeries(filter: TrackableFilter.SeriesFilter?) -> HasManyAssociation<Self, Series.Database> {
		var association = hasMany(Series.Database.self)
			.filter(Series.Database.Columns.excludeFromStatistics == Series.ExcludeFromStatistics.include)

		if let startDate = filter?.startDate {
			association = association
				.filter(Series.Database.Columns.date >= startDate)
		}

		if let endDate = filter?.endDate {
			association = association
				.filter(Series.Database.Columns.date <= endDate)
		}

		if let alley = filter?.alley {
			association = association
				.filter(Series.Database.Columns.alleyId == alley)
		}

		return association
			.order(Series.Database.Columns.date.asc)
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
			through: games,
			using: Game.Database.trackableFrames(filter: filter)
		)
	}
}
