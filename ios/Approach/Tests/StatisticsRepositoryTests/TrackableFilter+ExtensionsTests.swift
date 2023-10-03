import DatabaseServiceInterface
import GRDB
import ModelsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

final class TrackableFiltersTests: XCTestCase {

	let bowler = Bowler.Database.mock(id: UUID(0), name: "Joseph")

	let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
	let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)

	let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
	let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
	let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
	let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)

	let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, excludeFromStatistics: .include)
	let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 123, excludeFromStatistics: .exclude)
	let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 123, excludeFromStatistics: .include)
	let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123, excludeFromStatistics: .exclude)
	let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, score: 123, excludeFromStatistics: .include)
	let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 123, excludeFromStatistics: .exclude)
	let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, score: 123, excludeFromStatistics: .include)
	let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 1, score: 123, excludeFromStatistics: .exclude)

	let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame3 = Frame.Database.mock(gameId: UUID(2), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame4 = Frame.Database.mock(gameId: UUID(3), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame5 = Frame.Database.mock(gameId: UUID(4), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame6 = Frame.Database.mock(gameId: UUID(5), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame7 = Frame.Database.mock(gameId: UUID(6), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
	let frame8 = Frame.Database.mock(gameId: UUID(7), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

	let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), result: .won)
	let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), result: .won)
	let matchPlay3 = MatchPlay.Database.mock(gameId: UUID(2), id: UUID(2), result: .won)
	let matchPlay4 = MatchPlay.Database.mock(gameId: UUID(3), id: UUID(3), result: .won)
	let matchPlay5 = MatchPlay.Database.mock(gameId: UUID(4), id: UUID(4), result: .won)
	let matchPlay6 = MatchPlay.Database.mock(gameId: UUID(5), id: UUID(5), result: .won)
	let matchPlay7 = MatchPlay.Database.mock(gameId: UUID(6), id: UUID(6), result: .won)
	let matchPlay8 = MatchPlay.Database.mock(gameId: UUID(7), id: UUID(7), result: .won)

	func getDatabase() throws -> any DatabaseWriter {
		try initializeDatabase(
			withBowlers: .custom([bowler]),
			withGear: .zero,
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2, series3, series4]),
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8]),
			withGameGear: .zero,
			withFrames: .custom([frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8]),
			withMatchPlays: .custom([matchPlay1, matchPlay2, matchPlay3, matchPlay4, matchPlay5, matchPlay6, matchPlay7, matchPlay8]),
			withBowlerPreferredGear: .zero
		)
	}

	// MARK: - Build Initial Queries

	// MARK: Bowler

	func testBuildInitialQueries_WithBowlerSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertEqual(series, [series1])
	}

	func testBuildInitialQueries_WithBowlerSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [game1])
	}

	func testBuildInitialQueries_WithBowlerSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [frame1])
	}

	func testBuildInitialQueries_WithBowlerSource_WhenBowlerMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withBowlers: .zero)

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: League

	func testBuildInitialQueries_WithLeagueSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertEqual(series, [series1])
	}

	func testBuildInitialQueries_WithLeagueSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [game1])
	}

	func testBuildInitialQueries_WithLeagueSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [frame1])
	}

	func testBuildInitialQueries_WithLeagueSource_WhenLeagueMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withLeagues: .zero)

		let filter = TrackableFilter(source: .league(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Series

	func testBuildInitialQueries_WithSeriesSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertNil(series)
	}

	func testBuildInitialQueries_WithSeriesSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [game1])
	}

	func testBuildInitialQueries_WithSeriesSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [frame1])
	}

	func testBuildInitialQueries_WithSeriesSource_WhenSeriesMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withSeries: .zero)

		let filter = TrackableFilter(source: .series(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Game

	func testBuildInitialQueries_WithGameSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertNil(series)
	}

	func testBuildInitialQueries_WithGameSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertNil(games)
	}

	func testBuildInitialQueries_WithGameSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [frame1])
	}

	func testBuildInitialQueries_WithGameSource_WhenGameMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withGames: .zero)

		let filter = TrackableFilter(source: .game(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: - Build Trackable Queries

	// MARK: Bowler

	func testBuildTrackableQueries_WithBowlerSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertEqual(series, [.init(id: UUID(0), numberOfGames: 3, total: 123, date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithBowlerSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [.init(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, date: Date(timeIntervalSince1970: 123), matchPlay: .init(id: UUID(0), result: .won))])
	}

	func testBuildTrackableQueries_WithBowlerSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [.init(seriesId: UUID(0), gameId: UUID(0), gameIndex: 0, index: 0, rolls: [], date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithBowlerSource_WhenBowlerMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withBowlers: .zero)

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: League

	func testBuildTrackableQueries_WithLeagueSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertEqual(series, [.init(id: UUID(0), numberOfGames: 3, total: 123, date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithLeagueSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [.init(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, date: Date(timeIntervalSince1970: 123), matchPlay: .init(id: UUID(0), result: .won))])
	}

	func testBuildTrackableQueries_WithLeagueSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [.init(seriesId: UUID(0), gameId: UUID(0), gameIndex: 0, index: 0, rolls: [], date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithLeagueSource_WhenLeagueMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withLeagues: .zero)

		let filter = TrackableFilter(source: .league(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Series

	func testBuildTrackableQueries_WithSeriesSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertNil(series)
	}

	func testBuildTrackableQueries_WithSeriesSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertEqual(games, [.init(seriesId: UUID(0), id: UUID(0), index: 0, score: 123, date: Date(timeIntervalSince1970: 123), matchPlay: .init(id: UUID(0), result: .won))])
	}

	func testBuildTrackableQueries_WithSeriesSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [.init(seriesId: UUID(0), gameId: UUID(0), gameIndex: 0, index: 0, rolls: [], date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithSeriesSource_WhenSeriesMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withSeries: .zero)

		let filter = TrackableFilter(source: .series(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Game

	func testBuildTrackableQueries_WithGameSource_ReturnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		XCTAssertNil(series)
	}

	func testBuildTrackableQueries_WithGameSource_ReturnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		XCTAssertNil(games)
	}

	func testBuildTrackableQueries_WithGameSource_ReturnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		XCTAssertEqual(frames, [.init(seriesId: UUID(0), gameId: UUID(0), gameIndex: 0, index: 0, rolls: [], date: Date(timeIntervalSince1970: 123))])
	}

	func testBuildTrackableQueries_WithGameSource_WhenGameMissing_ThrowsError() async throws {
		let database = try initializeDatabase(withGames: .zero)

		let filter = TrackableFilter(source: .game(UUID(0)))

		await assertThrowsError(ofType: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}
}
