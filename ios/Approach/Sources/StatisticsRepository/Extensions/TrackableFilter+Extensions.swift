import DatabaseModelsLibrary
import DatabaseServiceInterface
import GRDB
import ModelsLibrary
import StatisticsLibrary
import StatisticsModelsLibrary

extension TrackableFilter {
	func buildInitialQueries(db: Database) throws -> (
		QueryInterfaceRequest<Series.Database>?,
		QueryInterfaceRequest<Game.Database>?,
		QueryInterfaceRequest<Frame.Database>?
	) {
		switch source {
		case let .bowler(id):
			let bowler = try Bowler.Database.fetchOneGuaranteed(db, id: id)
			let leagues = Bowler.Database.trackableLeagues(filter: leagueFilter)
			let series = Bowler.Database.trackableSeries(through: leagues, filter: seriesFilter)
			let games = Bowler.Database.trackableGames(through: series, filter: gameFilter)
			let frames = Bowler.Database.trackableFrames(through: games, filter: frameFilter)
			return (bowler.request(for: series), bowler.request(for: games), bowler.request(for: frames))
		case let .league(id):
			let league = try League.Database.fetchOneGuaranteed(db, id: id)
			let series = League.Database.trackableSeries(filter: seriesFilter)
			let games = League.Database.trackableGames(through: series, filter: gameFilter)
			let frames = League.Database.trackableFrames(through: games, filter: frameFilter)
			return (league.request(for: series), league.request(for: games), league.request(for: frames))
		case let .series(id):
			let series = try Series.Database.fetchOneGuaranteed(db, id: id)
			let games: HasManyAssociation<Series.Database, Game.Database>
			switch series.preBowl {
			case .preBowl:
				var gameFilterWithExcluded = gameFilter
				gameFilterWithExcluded.includingExcluded = true
				games = Series.Database.trackableGames(filter: gameFilterWithExcluded)
			case .regular:
				games = Series.Database.trackableGames(filter: gameFilter)
			}
			let frames = Series.Database.trackableFrames(through: games, filter: frameFilter)
			return (nil, series.request(for: games), series.request(for: frames))
		case let .game(id):
			let game = try Game.Database.fetchOneGuaranteed(db, id: id)
			let frames = Game.Database.trackableFrames(filter: frameFilter)
			return (nil, nil, game.request(for: frames))
		}
	}

	func buildTrackableQueries(db: Database) throws -> (
		QueryInterfaceRequest<Series.TrackableEntry>?,
		QueryInterfaceRequest<Game.TrackableEntry>?,
		QueryInterfaceRequest<Frame.TrackableEntry>?
	) {
		let (series, games, frames) = try buildInitialQueries(db: db)

		return (
			refineTrackableSeriesQuery(series),
			refineTrackableGamesQuery(games),
			refineTrackableFramesQuery(frames)
		)
	}

	private func refineTrackableSeriesQuery(
		_ query: QueryInterfaceRequest<Series.Database>?
	) -> QueryInterfaceRequest<Series.TrackableEntry>? {
		query?
			.annotated(
				with: Series.Database.trackableGames(filter: .init()).count.forKey("numberOfGames") ?? 0
			)
			.annotated(
				with: Series.Database.trackableGames(filter: .init()).sum(Game.Database.Columns.score).forKey("total") ?? 0
			)
			.asRequest(of: Series.TrackableEntry.self)
	}

	private func refineTrackableGamesQuery(
		_ query: QueryInterfaceRequest<Game.Database>?
	) -> QueryInterfaceRequest<Game.TrackableEntry>? {
		query?
			.annotated(withRequired: Game.Database.series.select(
				Series.Database.Columns.id.forKey("seriesid"),
				Series.Database.Columns.date
			))
			.including(optional: Game.Database.matchPlay)
			.asRequest(of: Game.TrackableEntry.self)
	}

	private func refineTrackableFramesQuery(
		_ query: QueryInterfaceRequest<Frame.Database>?
	) -> QueryInterfaceRequest<Frame.TrackableEntry>? {
		query?
			.annotated(withRequired: Frame.Database.series.select(
				Series.Database.Columns.id.forKey("seriesId"),
				Series.Database.Columns.date
			))
			.annotated(withRequired: Frame.Database.game.select(
				Game.Database.Columns.index.forKey("gameIndex")
			))
			.filter(
				!(Frame.Database.Columns.roll1 == nil &&
					Frame.Database.Columns.roll2 == nil &&
					(Frame.Database.Columns.roll0 == nil || Frame.Database.Columns.roll0 == "000000")
					)
			)
			.asRequest(of: Frame.TrackableEntry.self)
	}
}
