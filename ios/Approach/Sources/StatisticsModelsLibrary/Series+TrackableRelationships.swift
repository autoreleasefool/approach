import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Series.Database {
	public static func trackableGames(filter: TrackableFilter.GameFilter?) -> HasManyAssociation<Self, Game.Database> {
		var association = hasMany(Game.Database.self)
			.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)
			.filter(Game.Database.Columns.score > 0)

		if let filter {
			if let opponent = filter.opponent {
				association = association
					.joining(required: Game.Database.matchPlay
						.filter(MatchPlay.Database.Columns.opponentId == opponent.id))
			}

			if !filter.gearUsed.isEmpty {
				association = association
					.joining(required: Game.Database.gear.filter(ids: filter.gearUsed.map(\.id)))
			}

			switch filter.lanes {
			case let .lanes(lanes):
				association = association
					.joining(required: Game.Database.lanes.filter(ids: lanes.map(\.id)))
			case let .positions(positions):
				association = association
					.joining(required: Game.Database.lanes.filter(positions.contains(Lane.Database.Columns.position)))
			case .none:
				break
			}
		}

		return association
			.order(Game.Database.Columns.index.asc)
	}

	public static func trackableFrames(
		through games: HasManyAssociation<Self, Game.Database>,
		filter: TrackableFilter.FrameFilter?
	) -> HasManyThroughAssociation<Self, Frame.Database> {
		hasMany(
			Frame.Database.self,
			through: games.filter(Game.Database.Columns.scoringMethod == Game.ScoringMethod.byFrame),
			using: Game.Database.trackableFrames(filter: filter)
		)
	}

	public static func applyFilter<T>(
		_ filter: TrackableFilter.SeriesFilter?,
		toAssociation: HasManyAssociation<T, Series.Database>
	) -> HasManyAssociation<T, Series.Database> {
		var association = toAssociation
			.isIncludedInStatistics()

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
}
