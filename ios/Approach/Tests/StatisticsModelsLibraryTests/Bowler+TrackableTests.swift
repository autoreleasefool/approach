@testable import DatabaseModelsLibrary
import GRDB
@testable import ModelsLibrary
@testable import StatisticsModelsLibrary
import TestDatabaseUtilitiesLibrary
import XCTest

final class BowlerTrackableTests: XCTestCase {

	// MARK: Leagues

	func testTrackableLeagues_ReturnsLeagues() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa", isArchived: true)

		let database = try initializeDatabase(withLeagues: .custom([league1, league2, league3]))

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableLeagues(filter: .init()))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [league1])
	}

	func testTrackableLeagues_FilteredByRecurrence_ReturnsResults() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league1 = League.Database.mock(id: UUID(0), name: "Majors", recurrence: .once)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", recurrence: .repeating)

		let database = try initializeDatabase(withLeagues: .custom([league1, league2]))

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableLeagues(filter: .init(recurrence: .once)))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [league1])
	}

	// MARK: Series

	func testTrackableSeries_ReturnsSeries() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa", isArchived: true)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(0), id: UUID(2), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series5 = Series.Database.mock(leagueId: UUID(1), id: UUID(4), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series6 = Series.Database.mock(leagueId: UUID(1), id: UUID(5), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series7 = Series.Database.mock(leagueId: UUID(2), id: UUID(6), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series8 = Series.Database.mock(leagueId: UUID(2), id: UUID(7), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series9 = Series.Database.mock(leagueId: UUID(2), id: UUID(8), date: Date(timeIntervalSince1970: 123), isArchived: true)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2, league3]),
			withSeries: .custom([series1, series2, series3, series4, series5, series6, series7, series8, series9])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableSeries(
					through: Bowler.Database.trackableLeagues(filter: .init()),
					filter: .init()
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [series1])
	}

	func testTrackableSeries_FilteredByStartDate_ReturnsSeries() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 1))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 0))

		let database = try initializeDatabase(withSeries: .custom([series1, series2]))

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableSeries(
					through: Bowler.Database.trackableLeagues(filter: .init()),
					filter: .init(startDate: Date(timeIntervalSince1970: 1))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [series1])
	}

	func testTrackableSeries_FilteredByEndDate_ReturnsSeries() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 0))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1))

		let database = try initializeDatabase(withSeries: .custom([series1, series2]))

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableSeries(
					through: Bowler.Database.trackableLeagues(filter: .init()),
					filter: .init(endDate: Date(timeIntervalSince1970: 0))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [series1])
	}

	func testTrackableSeries_FilteredByAlley_ReturnsSeries() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let alley = Alley.Database.mock(id: UUID(0), name: "Skyview")

		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 0), alleyId: UUID(0))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1), alleyId: nil)

		let database = try initializeDatabase(withAlleys: .custom([alley]), withSeries: .custom([series1, series2]))

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableSeries(
					through: Bowler.Database.trackableLeagues(filter: .init()),
					filter: .init(alley: .alley(.init(id: UUID(0), name: "Skyview")))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [series1])
	}

	// MARK: Games

	func testTrackableGames_ReturnsGames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa", isArchived: true)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(0), id: UUID(2), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series5 = Series.Database.mock(leagueId: UUID(1), id: UUID(4), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series6 = Series.Database.mock(leagueId: UUID(1), id: UUID(5), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series7 = Series.Database.mock(leagueId: UUID(2), id: UUID(6), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series8 = Series.Database.mock(leagueId: UUID(2), id: UUID(7), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series9 = Series.Database.mock(leagueId: UUID(2), id: UUID(8), date: Date(timeIntervalSince1970: 123), isArchived: true)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 123, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, score: 123, excludeFromStatistics: .include)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, score: 123, excludeFromStatistics: .include)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game9 = Game.Database.mock(seriesId: UUID(4), id: UUID(8), index: 0, score: 123, excludeFromStatistics: .include)
		let game10 = Game.Database.mock(seriesId: UUID(4), id: UUID(9), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game11 = Game.Database.mock(seriesId: UUID(5), id: UUID(10), index: 0, score: 123, excludeFromStatistics: .include)
		let game12 = Game.Database.mock(seriesId: UUID(5), id: UUID(11), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game13 = Game.Database.mock(seriesId: UUID(6), id: UUID(12), index: 0, score: 123, excludeFromStatistics: .include)
		let game14 = Game.Database.mock(seriesId: UUID(6), id: UUID(13), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game15 = Game.Database.mock(seriesId: UUID(7), id: UUID(14), index: 0, score: 123, excludeFromStatistics: .include)
		let game16 = Game.Database.mock(seriesId: UUID(7), id: UUID(15), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game17 = Game.Database.mock(seriesId: UUID(8), id: UUID(16), index: 0, score: 123, excludeFromStatistics: .include)
		let game18 = Game.Database.mock(seriesId: UUID(8), id: UUID(17), index: 1, score: 123, excludeFromStatistics: .exclude)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2, league3]),
			withSeries: .custom([series1, series2, series3, series4, series5, series6, series7, series8, series9]),
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8, game9, game10, game11, game12, game13, game14, game15, game16, game17, game18])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableGames(
					through: Bowler.Database.trackableSeries(
						through: Bowler.Database.trackableLeagues(filter: .init()),
						filter: .init()
					),
					filter: .init()
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [game1])
	}

	func testTrackableGames_FilteredByOpponent_ReturnsGames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let opponent = Bowler.Database.mock(id: UUID(1), name: "Sarah")

		let league = League.Database.mock(id: UUID(0), name: "Majors")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))

		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, score: 123)

		let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(1))
		let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), opponentId: nil)

		let database = try initializeDatabase(
			withBowlers: .custom([bowler, opponent]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game1, game2]),
			withMatchPlays: .custom([matchPlay1, matchPlay2])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableGames(
					through: Bowler.Database.trackableSeries(
						through: Bowler.Database.trackableLeagues(filter: .init()),
						filter: .init()
					),
					filter: .init(opponent: .init(id: UUID(1), name: "Sarah"))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [game1])
	}

	func testTrackableGames_FilteredByGear_ReturnsGames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let opponent = Bowler.Database.mock(id: UUID(1), name: "Sarah")

		let league = League.Database.mock(id: UUID(0), name: "Majors")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))

		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, score: 123)

		let gear1 = Gear.Database.mock(id: UUID(0), name: "Shoes", kind: .shoes)
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Towel", kind: .towel)

		let gameGear1 = GameGear.Database(gameId: UUID(0), gearId: UUID(0))
		let gameGear2 = GameGear.Database(gameId: UUID(1), gearId: UUID(1))

		let database = try initializeDatabase(
			withBowlers: .custom([bowler, opponent]),
			withGear: .custom([gear1, gear2]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game1, game2]),
			withGameGear: .custom([gameGear1, gameGear2]),
			withBowlerPreferredGear: .zero
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableGames(
					through: Bowler.Database.trackableSeries(
						through: Bowler.Database.trackableLeagues(filter: .init()),
						filter: .init()
					),
					filter: .init(gearUsed: [.init(id: UUID(0), name: "Shoes", kind: .shoes, ownerName: nil, avatar: .mock(id: UUID(0)))])
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [game1])
	}

	func testTrackableGames_FilteredByLaneIds_ReturnsGames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league = League.Database.mock(id: UUID(0), name: "Majors")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))

		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, score: 123)
		let game3 = Game.Database.mock(id: UUID(2), index: 2, score: 123)

		let alley = Alley.Database.mock(id: UUID(0), name: "Skyview")

		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .rightWall)
		let lane3 = Lane.Database(alleyId: UUID(0), id: UUID(2), label: "3", position: .noWall)

		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let gameLane2 = GameLane.Database(gameId: UUID(1), laneId: UUID(1))
		let gameLane3 = GameLane.Database(gameId: UUID(2), laneId: UUID(2))

		let database = try initializeDatabase(
			withAlleys: .custom([alley]),
			withLanes: .custom([lane1, lane2, lane3]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game1, game2, game3]),
			withGameLanes: .custom([gameLane1, gameLane2, gameLane3])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableGames(
					through: Bowler.Database.trackableSeries(
						through: Bowler.Database.trackableLeagues(filter: .init()),
						filter: .init()
					),
					filter: .init(lanes: .lanes([.init(id: UUID(0), label: "1", position: .leftWall), .init(id: UUID(1), label: "2", position: .rightWall)]))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [game1, game2])
	}

	func testTrackableGames_FilteredByLanePositions_ReturnsGames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league = League.Database.mock(id: UUID(0), name: "Majors")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))

		let game1 = Game.Database.mock(id: UUID(0), index: 0, score: 123)
		let game2 = Game.Database.mock(id: UUID(1), index: 1, score: 123)
		let game3 = Game.Database.mock(id: UUID(2), index: 2, score: 123)

		let alley = Alley.Database.mock(id: UUID(0), name: "Skyview")

		let lane1 = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall)
		let lane2 = Lane.Database(alleyId: UUID(0), id: UUID(1), label: "2", position: .rightWall)
		let lane3 = Lane.Database(alleyId: UUID(0), id: UUID(2), label: "3", position: .noWall)

		let gameLane1 = GameLane.Database(gameId: UUID(0), laneId: UUID(0))
		let gameLane2 = GameLane.Database(gameId: UUID(1), laneId: UUID(1))
		let gameLane3 = GameLane.Database(gameId: UUID(2), laneId: UUID(2))

		let database = try initializeDatabase(
			withAlleys: .custom([alley]),
			withLanes: .custom([lane1, lane2, lane3]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game1, game2, game3]),
			withGameLanes: .custom([gameLane1, gameLane2, gameLane3])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableGames(
					through: Bowler.Database.trackableSeries(
						through: Bowler.Database.trackableLeagues(filter: .init()),
						filter: .init()
					),
					filter: .init(lanes: .positions([.leftWall, .rightWall]))
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [game1, game2])
	}

	// MARK: Frames

	func testTrackableFrames_ReturnsFrames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa", isArchived: true)

		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(0), id: UUID(2), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series5 = Series.Database.mock(leagueId: UUID(1), id: UUID(4), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series6 = Series.Database.mock(leagueId: UUID(1), id: UUID(5), date: Date(timeIntervalSince1970: 123), isArchived: true)
		let series7 = Series.Database.mock(leagueId: UUID(2), id: UUID(6), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series8 = Series.Database.mock(leagueId: UUID(2), id: UUID(7), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series9 = Series.Database.mock(leagueId: UUID(2), id: UUID(8), date: Date(timeIntervalSince1970: 123), isArchived: true)

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 123, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, score: 123, excludeFromStatistics: .include)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, score: 123, excludeFromStatistics: .include)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game9 = Game.Database.mock(seriesId: UUID(4), id: UUID(8), index: 0, score: 123, excludeFromStatistics: .include)
		let game10 = Game.Database.mock(seriesId: UUID(4), id: UUID(9), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game11 = Game.Database.mock(seriesId: UUID(5), id: UUID(10), index: 0, score: 123, excludeFromStatistics: .include)
		let game12 = Game.Database.mock(seriesId: UUID(5), id: UUID(11), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game13 = Game.Database.mock(seriesId: UUID(6), id: UUID(12), index: 0, score: 123, excludeFromStatistics: .include)
		let game14 = Game.Database.mock(seriesId: UUID(6), id: UUID(13), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game15 = Game.Database.mock(seriesId: UUID(7), id: UUID(14), index: 0, score: 123, excludeFromStatistics: .include)
		let game16 = Game.Database.mock(seriesId: UUID(7), id: UUID(15), index: 1, score: 123, excludeFromStatistics: .exclude)
		let game17 = Game.Database.mock(seriesId: UUID(8), id: UUID(16), index: 0, score: 123, excludeFromStatistics: .include)
		let game18 = Game.Database.mock(seriesId: UUID(8), id: UUID(17), index: 1, score: 123, excludeFromStatistics: .exclude)

		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame3 = Frame.Database.mock(gameId: UUID(2), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame4 = Frame.Database.mock(gameId: UUID(3), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame5 = Frame.Database.mock(gameId: UUID(4), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame6 = Frame.Database.mock(gameId: UUID(5), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame7 = Frame.Database.mock(gameId: UUID(6), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame8 = Frame.Database.mock(gameId: UUID(7), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame9 = Frame.Database.mock(gameId: UUID(8), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame10 = Frame.Database.mock(gameId: UUID(9), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame11 = Frame.Database.mock(gameId: UUID(10), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame12 = Frame.Database.mock(gameId: UUID(11), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame13 = Frame.Database.mock(gameId: UUID(12), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame14 = Frame.Database.mock(gameId: UUID(13), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame15 = Frame.Database.mock(gameId: UUID(14), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame16 = Frame.Database.mock(gameId: UUID(15), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame17 = Frame.Database.mock(gameId: UUID(16), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
		let frame18 = Frame.Database.mock(gameId: UUID(17), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

		let database = try initializeDatabase(
			withLeagues: .custom([league1, league2, league3]),
			withSeries: .custom([series1, series2, series3, series4, series5, series6, series7, series8, series9]),
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8, game9, game10, game11, game12, game13, game14, game15, game16, game17, game18]),
			withFrames: .custom([frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8, frame9, frame10, frame11, frame12, frame13, frame14, frame15, frame16, frame17, frame18])
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableFrames(
					through: Bowler.Database.trackableGames(
						through: Bowler.Database.trackableSeries(
							through: Bowler.Database.trackableLeagues(filter: .init()),
							filter: .init()
						),
						filter: .init()
					),
					filter: .init()
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [frame1])
	}

	func testTrackableFrames_FilteredByGear_ReturnsFrames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let league = League.Database.mock(id: UUID(0), name: "Majors")

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))

		let game = Game.Database.mock(id: UUID(0), index: 0, score: 123)

		let ball1 = Gear.Database.mock(id: UUID(0), name: "Red", kind: .bowlingBall)
		let ball2 = Gear.Database.mock(id: UUID(1), name: "Green", kind: .bowlingBall)
		let ball3 = Gear.Database.mock(id: UUID(2), name: "Yellow", kind: .bowlingBall)

		let frame1 = Frame.Database.mock(index: 0, ball0: UUID(0))
		let frame2 = Frame.Database.mock(index: 1, ball1: UUID(1))
		let frame3 = Frame.Database.mock(index: 2, ball0: UUID(2), ball1: UUID(2), ball2: UUID(2))

		let database = try initializeDatabase(
			withGear: .custom([ball1, ball2, ball3]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game]),
			withGameLanes: .zero,
			withGameGear: .zero,
			withFrames: .custom([frame1, frame2, frame3]),
			withBowlerPreferredGear: .zero
		)

		let result = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableFrames(
					through: Bowler.Database.trackableGames(
						through: Bowler.Database.trackableSeries(
							through: Bowler.Database.trackableLeagues(filter: .init()),
							filter: .init()
						),
						filter: .init()
					),
					filter: .init(bowlingBallsUsed: [
						.init(id: UUID(0), name: "Red", kind: .bowlingBall, ownerName: nil, avatar: .mock(id: UUID(0))),
						.init(id: UUID(1), name: "Green", kind: .bowlingBall, ownerName: nil, avatar: .mock(id: UUID(0))),
					])
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [frame1, frame2])
	}

	func testTrackableFrames_WithAllFilters_ReturnsFrames() async throws {
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

		let opponent = Bowler.Database.mock(id: UUID(1), name: "Sarah", kind: .opponent)

		let league = League.Database.mock(id: UUID(0), name: "Majors", recurrence: .once)

		let alley = Alley.Database.mock(id: UUID(0), name: "Skyview")

		let lane = Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .noWall)

		let series = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), alleyId: UUID(0))

		let game = Game.Database.mock(id: UUID(0), index: 0, score: 123)

		let ball = Gear.Database.mock(id: UUID(0), name: "Ball", kind: .bowlingBall)
		let towel = Gear.Database.mock(id: UUID(1), name: "Towel", kind: .towel)

		let gameLane = GameLane.Database(gameId: UUID(0), laneId: UUID(0))

		let gameGear = GameGear.Database(gameId: UUID(0), gearId: UUID(1))

		let frame = Frame.Database.mock(index: 0, ball0: UUID(0))

		let matchPlay = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(1))

		let database = try initializeDatabase(
			withAlleys: .custom([alley]),
			withLanes: .custom([lane]),
			withBowlers: .custom([bowler, opponent]),
			withGear: .custom([ball, towel]),
			withLeagues: .custom([league]),
			withSeries: .custom([series]),
			withGames: .custom([game]),
			withGameLanes: .custom([gameLane]),
			withGameGear: .custom([gameGear]),
			withFrames: .custom([frame]),
			withMatchPlays: .custom([matchPlay]),
			withBowlerPreferredGear: .zero
		)

		let result1 = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableFrames(
					through: Bowler.Database.trackableGames(
						through: Bowler.Database.trackableSeries(
							through: Bowler.Database.trackableLeagues(filter: .init(recurrence: .once)),
							filter: .init(startDate: Date(timeIntervalSince1970: 123), endDate: Date(timeIntervalSince1970: 123), alley: .alley(.init(id: UUID(0), name: "Skyview")))
						),
						filter: .init(
							lanes: .lanes([.init(id: UUID(0), label: "1", position: .noWall)]),
							gearUsed: [.init(id: UUID(1), name: "Towel", kind: .towel, ownerName: nil, avatar: .mock(id: UUID(0)))],
							opponent: .init(id: UUID(1), name: "Sarah")
						)
					),
					filter: .init(bowlingBallsUsed: [.init(id: UUID(0), name: "Ball", kind: .bowlingBall, ownerName: nil, avatar: .mock(id: UUID(0)))])
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result1, [frame])

		let result2 = try await database.read {
			try bowler
				.request(for: Bowler.Database.trackableFrames(
					through: Bowler.Database.trackableGames(
						through: Bowler.Database.trackableSeries(
							through: Bowler.Database.trackableLeagues(filter: .init(recurrence: .once)),
							filter: .init(startDate: Date(timeIntervalSince1970: 123), endDate: Date(timeIntervalSince1970: 123), alley: .alley(.init(id: UUID(0), name: "Skyview")))
						),
						filter: .init(
							lanes: .positions([.noWall]),
							gearUsed: [.init(id: UUID(1), name: "Towel", kind: .towel, ownerName: nil, avatar: .mock(id: UUID(0)))],
							opponent: .init(id: UUID(1), name: "Sarah")
						)
					),
					filter: .init(bowlingBallsUsed: [.init(id: UUID(0), name: "Ball", kind: .bowlingBall, ownerName: nil, avatar: .mock(id: UUID(0)))])
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result2, [frame])
	}
}
