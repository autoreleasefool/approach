import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Series.Database {
	public static func trackableGames(filter: TrackableFilter.GameFilter?) -> HasManyAssociation<Self, Game.Database> {
		var association = hasMany(Game.Database.self)
			.filter(Game.Database.Columns.excludeFromStatistics == Game.ExcludeFromStatistics.include)

		if let filter {
			if let opponent = filter.opponent {
				association = association
					.joining(required: Game.Database.matchPlay
						.filter(MatchPlay.Database.Columns.opponentId == opponent))
			}

			if !filter.gearUsed.isEmpty {
				association = association
					.having(Game.Database.gear.filter(ids: filter.gearUsed).count > 0)
			}

			switch filter.lanes {
			case let .lanes(lanes):
				association = association
					.having(Game.Database.lanes.filter(ids: lanes).count > 0)
			case let .positions(positions):
				association = association
					.having(Game.Database.lanes.filter(positions.contains(Lane.Database.Columns.position)).count > 0)
			case .none:
				break
			}
		}

		return association
			.order(Game.Database.Columns.index.asc)
	}

	public static func frames(
		through games: HasManyAssociation<Self, Game.Database>,
		filter: TrackableFilter.FrameFilter?
	) -> HasManyThroughAssociation<Self, Frame.Database> {
		hasMany(
			Frame.Database.self,
			through: games,
			using: Game.Database.trackableFrames(filter: filter)
		)
	}
}
