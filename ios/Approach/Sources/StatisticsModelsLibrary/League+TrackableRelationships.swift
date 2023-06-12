import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension League.Database {
	public static func trackableSeries(
		filter: TrackableFilter.SeriesFilter?
	) -> HasManyAssociation<Self, Series.Database> {
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

		if let alleyFilter = filter?.alley {
			switch alleyFilter {
			case let .alley(alley):
				association = association
					.filter(Series.Database.Columns.alleyId == alley.id)
			case let .properties(properties):
				var filter: BelongsToAssociation<Series.Database, Alley.Database>?
				if let material = properties.material {
					if filter == nil { filter = Series.Database.alley }
					filter = filter?.filter(Alley.Database.Columns.material == material)
				}
				if let mechanism = properties.mechanism {
					if filter == nil { filter = Series.Database.alley }
					filter = filter?.filter(Alley.Database.Columns.mechanism == mechanism)
				}
				if let pinBase = properties.pinBase {
					if filter == nil { filter = Series.Database.alley }
					filter = filter?.filter(Alley.Database.Columns.pinBase == pinBase)
				}
				if let pinFall = properties.pinFall {
					if filter == nil { filter = Series.Database.alley }
					filter = filter?.filter(Alley.Database.Columns.pinFall == pinFall)
				}
				if let filter {
					association = association
						.joining(required: filter)
				}
			}
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
			through: games.filter(Game.Database.Columns.scoringMethod == Game.ScoringMethod.byFrame),
			using: Game.Database.trackableFrames(filter: filter)
		)
	}
}
