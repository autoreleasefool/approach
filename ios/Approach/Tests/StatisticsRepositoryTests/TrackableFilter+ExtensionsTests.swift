import DatabaseServiceInterface
import Foundation
import GRDB
import ModelsLibrary
@testable import StatisticsLibrary
@testable import StatisticsRepository
@testable import StatisticsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("TrackableFilters", .tags(.repository, .grdb, .dependencies))
struct TrackableFiltersTests {

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

	let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "000100", roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)
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
		try initializeApproachDatabase(
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

	@Test("buildInitialQueries with bowler source returns expected series", .tags(.unit))
	func buildInitialQueries_withBowlerSource_returnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == [series1])
	}

	@Test("buildInitialQueries with bowler source returns expected games", .tags(.unit))
	func buildInitialQueries_withBowlerSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [game1])
	}

	@Test("buildInitialQueries with bowler source returns expected frames", .tags(.unit))
	func buildInitialQueries_withBowlerSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [frame1])
	}

	@Test("buildInitialQueries with bowler source throws error when bowler missing", .tags(.unit))
	func buildInitialQueries_withBowlerSource_rhrowsErrorWhenBowlerMissing() async throws {
		let database = try initializeApproachDatabase(withBowlers: .zero)

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: League

	@Test("buildInitialQueries with league source returns expected series", .tags(.unit))
	func buildInitialQueries_withLeagueSource_returnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == [series1])
	}

	@Test("buildInitialQueries with league source returns expected games", .tags(.unit))
	func buildInitialQueries_withLeagueSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [game1])
	}

	@Test("buildInitialQueries with league source returns expected frames", .tags(.unit))
	func buildInitialQueries_withLeagueSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [frame1])
	}

	@Test("buildInitialQueries with league source throws error when league missing", .tags(.unit))
	func buildInitialQueries_withLeagueSource_throwsErrorWhenLeagueMissing() async throws {
		let database = try initializeApproachDatabase(withLeagues: .zero)

		let filter = TrackableFilter(source: .league(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Series

	@Test("buildInitialQueries with series source returns nil series", .tags(.unit))
	func buildInitialQueries_withSeriesSource_returnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == nil)
	}

	@Test("buildInitialQueries with series source returns expected games", .tags(.unit))
	func buildInitialQueries_withSeriesSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [game1])
	}

	@Test("buildInitialQueries with series source returns expected frames", .tags(.unit))
	func buildInitialQueries_withSeriesSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [frame1])
	}

	@Test("buildInitialQueries with series source throws error when series missing", .tags(.unit))
	func buildInitialQueries_withSeriesSource_throwsErrorWhenSeriesMissing() async throws {
		let database = try initializeApproachDatabase(withSeries: .zero)

		let filter = TrackableFilter(source: .series(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Game

	@Test("buildInitialQueries with game source returns nil series", .tags(.unit))
	func buildInitialQueries_withGameSource_returnsNilSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == nil)
	}

	@Test("buildInitialQueries with game source returns nil games", .tags(.unit))
	func buildInitialQueries_withGameSource_returnsNilGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildInitialQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == nil)
	}

	@Test("buildInitialQueries with game source returns expected frames", .tags(.unit))
	func buildInitialQueries_withGameSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildInitialQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [frame1])
	}

	@Test("buildInitialQueries with game source throws error when game missing", .tags(.unit))
	func buildInitialQueries_withGameSource_throwsErrorWhenGameMissing() async throws {
		let database = try initializeApproachDatabase(withGames: .zero)

		let filter = TrackableFilter(source: .game(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildInitialQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: - Build Trackable Queries

	// MARK: Bowler

	@Test("buildTrackableQueries with bowler source returns expected series", .tags(.unit))
	func buildTrackableQueries_withBowlerSource_returnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == [.init(id: UUID(0), numberOfGames: 1, total: 123, date: Date(timeIntervalSince1970: 123))])
	}

	@Test("buildTrackableQueries with bowler source returns expected games", .tags(.unit))
	func buildTrackableQueries_withBowlerSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [
			.init(
				seriesId: UUID(0),
				id: UUID(0),
				index: 0,
				score: 123,
				date: Date(timeIntervalSince1970: 123),
				matchPlay: .init(id: UUID(0), result: .won)
			),
		])
	}

	@Test("buildTrackableQueries with bowler source returns expected frames", .tags(.unit))
	func buildTrackableQueries_withBowlerSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [
			.init(
				seriesId: UUID(0),
				gameId: UUID(0),
				gameIndex: 0,
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)],
				date: Date(timeIntervalSince1970: 123)
			),
		])
	}

	@Test("buildTrackableQueries with bowler source throws error when bowler missing", .tags(.unit))
	func buildTrackableQueries_withBowlerSource_whenBowlerMissing_throwsError() async throws {
		let database = try initializeApproachDatabase(withBowlers: .zero)

		let filter = TrackableFilter(source: .bowler(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: League

	@Test("buildTrackableQueries with league source returns expected series", .tags(.unit))
	func buildTrackableQueries_withLeagueSource_returnsExpectedSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == [.init(id: UUID(0), numberOfGames: 1, total: 123, date: Date(timeIntervalSince1970: 123))])
	}

	@Test("buildTrackableQueries with league source returns expected games", .tags(.unit))
	func buildTrackableQueries_withLeagueSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [
			.init(
				seriesId: UUID(0),
				id: UUID(0),
				index: 0,
				score: 123,
				date: Date(timeIntervalSince1970: 123),
				matchPlay: .init(id: UUID(0), result: .won)
			),
		])
	}

	@Test("buildTrackableQueries with league source returns expected frames", .tags(.unit))
	func buildTrackableQueries_withLeagueSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .league(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [
			.init(
				seriesId: UUID(0),
				gameId: UUID(0),
				gameIndex: 0,
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)],
				date: Date(timeIntervalSince1970: 123)
			),
		])
	}

	@Test("buildTrackableQueries with league source throws error when league missing", .tags(.unit))
	func buildTrackableQueries_withLeagueSource_whenLeagueMissing_throwsError() async throws {
		let database = try initializeApproachDatabase(withLeagues: .zero)

		let filter = TrackableFilter(source: .league(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Series

	@Test("buildTrackableQueries with series source returns nil series", .tags(.unit))
	func buildTrackableQueries_withSeriesSource_returnsNilSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == nil)
	}

	@Test("buildTrackableQueries with series source returns expected games", .tags(.unit))
	func buildTrackableQueries_withSeriesSource_returnsExpectedGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == [
			.init(
				seriesId: UUID(0),
				id: UUID(0),
				index: 0,
				score: 123,
				date: Date(timeIntervalSince1970: 123),
				matchPlay: .init(id: UUID(0), result: .won)
			),
		])
	}

	@Test("buildTrackableQueries with series source returns expected frames", .tags(.unit))
	func buildTrackableQueries_withSeriesSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .series(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [
			.init(
				seriesId: UUID(0),
				gameId: UUID(0),
				gameIndex: 0,
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)],
				date: Date(timeIntervalSince1970: 123)
			),
		])
	}

	@Test("buildTrackableQueries with series source throws error when series missing", .tags(.unit))
	func buildTrackableQueries_withSeriesSource_whenSeriesMissing_throwsError() async throws {
		let database = try initializeApproachDatabase(withSeries: .zero)

		let filter = TrackableFilter(source: .series(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}

	// MARK: Game

	@Test("buildTrackableQueries with game source returns nil series", .tags(.unit))
	func buildTrackableQueries_withGameSource_returnsNilSeries() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let series = try await database.read {
			let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
			return try seriesRequest?.fetchAll($0)
		}

		#expect(series == nil)
	}

	@Test("buildTrackableQueries with game source returns nil games", .tags(.unit))
	func buildTrackableQueries_withGameSource_returnsNilGames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let games = try await database.read {
			let (_, gamesRequest, _) = try filter.buildTrackableQueries(db: $0)
			return try gamesRequest?.fetchAll($0)
		}

		#expect(games == nil)
	}

	@Test("buildTrackableQueries with game source returns expected frames", .tags(.unit))
	func buildTrackableQueries_withGameSource_returnsExpectedFrames() async throws {
		let database = try getDatabase()

		let filter = TrackableFilter(source: .game(UUID(0)))

		let frames = try await database.read {
			let (_, _, framesRequest) = try filter.buildTrackableQueries(db: $0)
			return try framesRequest?.fetchAll($0)
		}

		#expect(frames == [
			.init(
				seriesId: UUID(0),
				gameId: UUID(0),
				gameIndex: 0,
				index: 0,
				rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin], didFoul: false), bowlingBall: nil)],
				date: Date(timeIntervalSince1970: 123)
			),
		])
	}

	@Test("buildTrackableQueries with game source throws error when game missing", .tags(.unit))
	func buildTrackableQueries_withGameSource_whenGameMissing_throwsError() async throws {
		let database = try initializeApproachDatabase(withGames: .zero)

		let filter = TrackableFilter(source: .game(UUID(0)))

		await #expect(throws: FetchableError.self) {
			_ = try await database.read {
				let (seriesRequest, _, _) = try filter.buildTrackableQueries(db: $0)
				return try seriesRequest?.fetchAll($0)
			}
		}
	}
}
