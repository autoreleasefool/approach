@testable import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import XCTest

final class TrackableTests: XCTestCase {
	func testBowler_TrackableLeagues_ReturnsTrackableLeagues() async throws {
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)

		let database = try initializeDatabase(withLeagues: .custom([league1, league2]))

		let result = try await database.read {
			try Bowler.Database
				.filter(id: UUID(0))
				.including(all: Bowler.Database.trackableLeagues)
				.asRequest(of: BowlerTrackableLeagueInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.leagues, [league1])
	}

	func testBowler_TrackableSeries_ReturnsTrackableSeries() async throws {
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2, series3, series4]),
			withSeriesLanes: .zero
		)

		let result = try await database.read {
			try Bowler.Database
				.filter(id: UUID(0))
				.including(all: Bowler.Database.trackableSeries)
				.asRequest(of: BowlerTrackableSeriesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.series, [series1])
	}

	func testBowler_TrackableGames_ReturnsTrackableGames() async throws {
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, excludeFromStatistics: .exclude)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, excludeFromStatistics: .include)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, excludeFromStatistics: .exclude)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, excludeFromStatistics: .include)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 1, excludeFromStatistics: .exclude)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2, series3, series4]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8])
		)

		let result = try await database.read {
			try Bowler.Database
				.filter(id: UUID(0))
				.including(all: Bowler.Database.trackableGames)
				.asRequest(of: BowlerTrackableGamesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.games, [game1])
	}

	func testBowler_TrackableFrames_ReturnsTrackableFrames() async throws {
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, excludeFromStatistics: .exclude)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, excludeFromStatistics: .include)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, excludeFromStatistics: .exclude)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, excludeFromStatistics: .include)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 1, excludeFromStatistics: .exclude)

		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame3 = Frame.Database.mock(gameId: UUID(2), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame4 = Frame.Database.mock(gameId: UUID(3), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame5 = Frame.Database.mock(gameId: UUID(4), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame6 = Frame.Database.mock(gameId: UUID(5), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame7 = Frame.Database.mock(gameId: UUID(6), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame8 = Frame.Database.mock(gameId: UUID(7), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2, series3, series4]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8]),
			withFrames: .custom([frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8])
		)

		let result = try await database.read {
			try Bowler.Database
				.filter(id: UUID(0))
				.including(all: Bowler.Database.trackableFrames)
				.asRequest(of: BowlerTrackableFramesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.frames, [frame1])
	}

	func testLeague_TrackableSeries_ReturnsTrackableSeries() async throws {
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let database = try initializeDatabase(withLeagues: .default, withSeries: .custom([series1, series2]), withSeriesLanes: .zero)

		let result = try await database.read {
			try League.Database
				.filter(id: UUID(0))
				.including(all: League.Database.trackableSeries)
				.asRequest(of: LeagueTrackableSeriesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.series, [series1])
	}

	func testLeague_TrackableGames_ReturnsTrackableGames() async throws {
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, excludeFromStatistics: .exclude)

		let database = try initializeDatabase(
			withLeagues: .default,
			withSeries: .custom([series1, series2]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2, game3, game4])
		)

		let result = try await database.read {
			try League.Database
				.filter(id: UUID(0))
				.including(all: League.Database.trackableGames)
				.asRequest(of: LeagueTrackableGamesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.games, [game1])
	}

	func testLeague_TrackableFrames_ReturnsTrackableFrames() async throws {
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_789), excludeFromStatistics: .exclude)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, excludeFromStatistics: .exclude)

		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame3 = Frame.Database.mock(gameId: UUID(2), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame4 = Frame.Database.mock(gameId: UUID(3), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

		let database = try initializeDatabase(
			withLeagues: .default,
			withSeries: .custom([series1, series2]),
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2, game3, game4]),
			withFrames: .custom([frame1, frame2, frame3, frame4])
		)

		let result = try await database.read {
			try League.Database
				.filter(id: UUID(0))
				.including(all: League.Database.trackableFrames)
				.asRequest(of: LeagueTrackableFramesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.frames, [frame1])
	}

	func testSeries_TrackableGames_ReturnsTrackableGames() async throws {
		let game1 = Game.Database.mock(id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, excludeFromStatistics: .exclude)

		let database = try initializeDatabase(
			withLeagues: .default,
			withSeries: .default,
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2])
		)

		let result = try await database.read {
			try Series.Database
				.filter(id: UUID(0))
				.including(all: Series.Database.trackableGames)
				.asRequest(of: SeriesTrackableGamesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.games, [game1])
	}

	func testSeries_TrackableFrames_ReturnsTrackableFrames() async throws {
		let game1 = Game.Database.mock(id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, excludeFromStatistics: .exclude)

		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

		let database = try initializeDatabase(
			withLeagues: .default,
			withSeries: .default,
			withSeriesLanes: .zero,
			withGames: .custom([game1, game2]),
			withFrames: .custom([frame1, frame2])
		)

		let result = try await database.read {
			try Series.Database
				.filter(id: UUID(0))
				.including(all: Series.Database.trackableFrames)
				.asRequest(of: SeriesTrackableFramesInfo.self)
				.fetchOne($0)
		}

		XCTAssertEqual(result?.frames, [frame1])
	}
}

private struct BowlerTrackableLeagueInfo: FetchableRecord, Decodable {
	let bowler: Bowler.Database
	let leagues: [League.Database]
}

private struct BowlerTrackableSeriesInfo: FetchableRecord, Decodable {
	let bowler: Bowler.Database
	let series: [Series.Database]
}

private struct BowlerTrackableGamesInfo: FetchableRecord, Decodable {
	let bowler: Bowler.Database
	let games: [Game.Database]
}

private struct BowlerTrackableFramesInfo: FetchableRecord, Decodable {
	let bowler: Bowler.Database
	let frames: [Frame.Database]
}

private struct LeagueTrackableSeriesInfo: FetchableRecord, Decodable {
	let league: League.Database
	let series: [Series.Database]
}

private struct LeagueTrackableGamesInfo: FetchableRecord, Decodable {
	let league: League.Database
	let games: [Game.Database]
}

private struct LeagueTrackableFramesInfo: FetchableRecord, Decodable {
	let league: League.Database
	let frames: [Frame.Database]
}

private struct SeriesTrackableGamesInfo: FetchableRecord, Decodable {
	let series: Series.Database
	let games: [Game.Database]
}

private struct SeriesTrackableFramesInfo: FetchableRecord, Decodable {
	let series: Series.Database
	let frames: [Frame.Database]
}
