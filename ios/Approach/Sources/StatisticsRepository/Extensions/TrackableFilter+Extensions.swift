import DatabaseModelsLibrary
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
		case let .bowler(id, _):
			let bowler = try Bowler.Database.fetchOneGuaranteed(db, id: id)
			let leagues = Bowler.Database.trackableLeagues(filter: leagueFilter)
			let series = Bowler.Database.trackableSeries(through: leagues, filter: seriesFilter)
			let games = Bowler.Database.trackableGames(through: series, filter: gameFilter)
			let frames = Bowler.Database.trackableFrames(through: games, filter: frameFilter)
			return (bowler.request(for: series), bowler.request(for: games), bowler.request(for: frames))
		case let .league(id, _):
			let league = try League.Database.fetchOneGuaranteed(db, id: id)
			let series = League.Database.trackableSeries(filter: seriesFilter)
			let games = League.Database.trackableGames(through: series, filter: gameFilter)
			let frames = League.Database.trackableFrames(through: games, filter: frameFilter)
			return (league.request(for: series), league.request(for: games), league.request(for: frames))
		case let .series(id, _):
			let series = try Series.Database.fetchOneGuaranteed(db, id: id)
			let games = Series.Database.trackableGames(filter: gameFilter)
			let frames = Series.Database.trackableFrames(through: games, filter: frameFilter)
			return (nil, series.request(for: games), series.request(for: frames))
		case let .game(id, _):
			let game = try Game.Database.fetchOneGuaranteed(db, id: id)
			let frames = Game.Database.trackableFrames(filter: frameFilter)
			return (nil, nil, game.request(for: frames))
		}
	}
}
