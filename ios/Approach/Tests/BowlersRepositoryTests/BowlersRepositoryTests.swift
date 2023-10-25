@testable import BowlersRepository
@testable import BowlersRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class BowlersRepositoryTests: XCTestCase {
	@Dependency(\.bowlers) var bowlers

	// MARK: List

	func testList_ReturnsOnlyPlayable() async throws {
		// Given a database with 3 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", kind: .opponent, archivedOn: nil)
		let bowler3 = Bowler.Database(id: UUID(2), name: "Audriana", kind: .opponent, archivedOn: Date())
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2, bowler3]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns only the playable bowler
		XCTAssertEqual(fetched, [.init(id: UUID(0), name: "Joseph", average: nil)])
	}

	func testList_SortsByName() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by name
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Audriana", average: nil),
			.init(id: UUID(0), name: "Joseph", average: nil),
		])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byRecentlyUsed)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers sorted by recently used ids
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: nil),
			.init(id: UUID(1), name: "Audriana", average: nil),
		])
	}

	func testList_WithGames_CalculatesAverages() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .playable, archivedOn: nil)
		// and 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 0, score: 250)
		let game4 = Game.Database.mock(seriesId: UUID(2), id: UUID(3), index: 1, score: 300)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1, bowler2]),
			withGames: .custom([game1, game2, game3, game4])
		)

		// Fetching the bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with averages
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Audriana", average: 275),
			.init(id: UUID(0), name: "Joseph", average: 150),
		])
	}

	func testList_WhenLeagueExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa", archivedOn: Date())
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date())
		let series3 = Series.Database.mock(leagueId: UUID(2), id: UUID(2), date: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 2, score: 300)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2, league3]),
			withSeries: .custom([series1, series2, series3]),
			withGames: .custom([game1, game2, game3])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	func testList_WhenSeriesExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		let league3 = League.Database.mock(id: UUID(2), name: "Ursa")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(2), id: UUID(2), date: Date(), archivedOn: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 2, score: 300)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2, league3]),
			withSeries: .custom([series1, series2, series3]),
			withGames: .custom([game1, game2, game3])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	func testList_WhenGameExcludedFromStatistics_DoesNotIncludeInStatistics() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		// 2 leagues
		let league1 = League.Database.mock(id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(id: UUID(1), name: "Minors")
		// with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date())
		// and 1 game each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 1, score: 200, excludeFromStatistics: .exclude)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler1]),
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2]),
			withGames: .custom([game1, game2])
		)

		// Fetching the bowler
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.list(ordered: .byName)
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowlers with only one score accounted for in the average
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", average: 100),
		])
	}

	// MARK: Pickable

	func testPickable_DoesNotIncludeOpponents() async throws {
		// Given a database with a bowler and an opponent
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the pickable bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.pickable()
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowler
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Sarah"),
		])
	}

	// MARK: Archived

	func testArchived_ReturnsArchivedBowlers() async throws {
		// Given a database with bowlers
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph", archivedOn: Date(timeIntervalSince1970: 1))
		let bowler2 = Bowler.Database.mock(id: UUID(2), name: "Audriana", archivedOn: Date(timeIntervalSince1970: 3))
		let bowler3 = Bowler.Database.mock(id: UUID(3), name: "Jordan", archivedOn: Date(timeIntervalSince1970: 2))
		let bowler4 = Bowler.Database.mock(id: UUID(1), name: "Sarah", archivedOn: nil)
		// 2 leagues each
		let league1 = League.Database.mock(bowlerId: UUID(0), id: UUID(0), name: "Majors")
		let league2 = League.Database.mock(bowlerId: UUID(0), id: UUID(1), name: "Minors")
		let league3 = League.Database.mock(bowlerId: UUID(1), id: UUID(2), name: "Majors")
		let league4 = League.Database.mock(bowlerId: UUID(1), id: UUID(3), name: "Minors")
		// 2 series each
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date())
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date())
		let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date())
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date())
		let series5 = Series.Database.mock(leagueId: UUID(2), id: UUID(4), date: Date())
		let series6 = Series.Database.mock(leagueId: UUID(2), id: UUID(5), date: Date())
		let series7 = Series.Database.mock(leagueId: UUID(3), id: UUID(6), date: Date())
		let series8 = Series.Database.mock(leagueId: UUID(3), id: UUID(7), date: Date())
		// 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 0)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 0)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 0)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 0)
		let game9 = Game.Database.mock(seriesId: UUID(4), id: UUID(8), index: 0)
		let game10 = Game.Database.mock(seriesId: UUID(4), id: UUID(9), index: 0)
		let game11 = Game.Database.mock(seriesId: UUID(5), id: UUID(10), index: 0)
		let game12 = Game.Database.mock(seriesId: UUID(5), id: UUID(11), index: 0)
		let game13 = Game.Database.mock(seriesId: UUID(6), id: UUID(12), index: 0)
		let game14 = Game.Database.mock(seriesId: UUID(6), id: UUID(13), index: 0)
		let game15 = Game.Database.mock(seriesId: UUID(7), id: UUID(14), index: 0)
		let game16 = Game.Database.mock(seriesId: UUID(7), id: UUID(15), index: 0)

		let db = try initializeDatabase(
			withBowlers: .custom([bowler1, bowler2, bowler3, bowler4]),
			withLeagues: .custom([league1, league2, league3, league4]),
			withSeries: .custom([series1, series2, series3, series4, series5, series6, series7, series8]),
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8, game9, game10, game11, game12, game13, game14, game15, game16])
		)

		// Fetching the archived bowlers
		let bowlers = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.archived()
		}
		var iterator = bowlers.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the bowler
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), name: "Audriana", totalNumberOfLeagues: 0, totalNumberOfSeries: 0, totalNumberOfGames: 0, archivedOn: Date(timeIntervalSince1970: 3)),
			.init(id: UUID(3), name: "Jordan", totalNumberOfLeagues: 0, totalNumberOfSeries: 0, totalNumberOfGames: 0, archivedOn: Date(timeIntervalSince1970: 2)),
			.init(id: UUID(0), name: "Joseph", totalNumberOfLeagues: 2, totalNumberOfSeries: 4, totalNumberOfGames: 8, archivedOn: Date(timeIntervalSince1970: 1)),
		])
	}

	// MARK: Opponents

	func testOpponents_ReturnsPlayablesAndOpponents() async throws {
		// Given a database with a bowler and an opponent
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.opponents(ordering: .byName)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns both bowlers
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", kind: .opponent),
			.init(id: UUID(1), name: "Sarah", kind: .playable),
		])
	}

	func testOpponents_SortsByName() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .opponent, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			self.bowlers.opponents(ordering: .byName)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by name
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Audriana", kind: .opponent),
			.init(id: UUID(0), name: "Joseph", kind: .opponent),
		])
	}

	func testOpponents_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with 2 opponents
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .opponent, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the opponents
		let opponents = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.bowlers = .liveValue
		} operation: {
			// with `byRecentlyUsed` ordering
			self.bowlers.opponents(ordering: .byRecentlyUsed)
		}
		var iterator = opponents.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the opponents sorted by recently used
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Joseph", kind: .opponent),
			.init(id: UUID(1), name: "Audriana", kind: .opponent),
		])
	}

	// MARK: Opponent Record

	func testOpponentRecord_ReturnsOpponentRecord() async throws {
		// Given a database with an opponent
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Sarah")
		let opponent = Bowler.Database(id: UUID(1), name: "Joseph", kind: .opponent, archivedOn: nil)
		let league = League.Database.mock(id: UUID(0), name: "Majors")
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 2))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 3))
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(1), id: UUID(1), index: 0, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 0, score: 3)
		let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(1), result: .won)
		let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), opponentId: UUID(1), result: .lost)
		let matchPlay3 = MatchPlay.Database.mock(gameId: UUID(2), id: UUID(2), opponentId: UUID(1), result: .tied)
		let db = try initializeDatabase(
			withBowlers: .custom([bowler, opponent]),
			withLeagues: .custom([league]),
			withSeries: .custom([series1, series2, series3]),
			withGames: .custom([game1, game2, game3]),
			withMatchPlays: .custom([matchPlay1, matchPlay2, matchPlay3])
		)

		// Fetching the opponent
		let record = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.record(againstOpponent: UUID(1))
		}

		XCTAssertEqual(
			record,
			.init(
				id: UUID(1),
				name: "Joseph",
				matchesAgainst: [
					.init(id: UUID(2), score: 3, opponentScore: 123, result: .tied),
					.init(id: UUID(0), score: 1, opponentScore: 123, result: .won),
					.init(id: UUID(1), score: 2, opponentScore: 123, result: .lost),
				],
				gamesPlayed: 3,
				gamesWon: 1,
				gamesLost: 1,
				gamesTied: 1
			)
		)
	}

	func testOpponentRecord_WhenGameExcluded_DoesNotIncludeInMatches() async throws {
		// Given a database with an opponent and some excluded leagues
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Sarah")
		let opponent = Bowler.Database(id: UUID(1), name: "Joseph", kind: .opponent, archivedOn: nil)
		let league1 = League.Database.mock(id: UUID(0), name: "Majors", excludeFromStatistics: .include)
		let league2 = League.Database.mock(id: UUID(1), name: "Minors", excludeFromStatistics: .exclude)
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let series3 = Series.Database.mock(leagueId: UUID(1), id: UUID(2), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .include)
		let series4 = Series.Database.mock(leagueId: UUID(1), id: UUID(3), date: Date(timeIntervalSince1970: 123), excludeFromStatistics: .exclude)
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .include)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 0, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .include)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 0, excludeFromStatistics: .exclude)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 0, excludeFromStatistics: .include)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 0, excludeFromStatistics: .exclude)
		let game7 = Game.Database.mock(seriesId: UUID(3), id: UUID(6), index: 0, excludeFromStatistics: .include)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 0, excludeFromStatistics: .exclude)
		let matchPlay1 = MatchPlay.Database.mock(gameId: UUID(0), id: UUID(0), opponentId: UUID(1), result: .won)
		let matchPlay2 = MatchPlay.Database.mock(gameId: UUID(1), id: UUID(1), opponentId: UUID(1), result: .won)
		let matchPlay3 = MatchPlay.Database.mock(gameId: UUID(2), id: UUID(2), opponentId: UUID(1), result: .won)
		let matchPlay4 = MatchPlay.Database.mock(gameId: UUID(3), id: UUID(3), opponentId: UUID(1), result: .won)
		let matchPlay5 = MatchPlay.Database.mock(gameId: UUID(4), id: UUID(4), opponentId: UUID(1), result: .won)
		let matchPlay6 = MatchPlay.Database.mock(gameId: UUID(5), id: UUID(5), opponentId: UUID(1), result: .won)
		let matchPlay7 = MatchPlay.Database.mock(gameId: UUID(6), id: UUID(6), opponentId: UUID(1), result: .won)
		let matchPlay8 = MatchPlay.Database.mock(gameId: UUID(7), id: UUID(7), opponentId: UUID(1), result: .won)

		let db = try initializeDatabase(
			withBowlers: .custom([bowler, opponent]),
			withLeagues: .custom([league1, league2]),
			withSeries: .custom([series1, series2, series3, series4]),
			withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8]),
			withMatchPlays: .custom([matchPlay1, matchPlay2, matchPlay3, matchPlay4, matchPlay5, matchPlay6, matchPlay7, matchPlay8])
		)

		// Fetching the opponent
		let record = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.record(againstOpponent: UUID(1))
		}

		// FIXME: test failing due to gamesPlayed not being filtered by excludeFromStatistics
		XCTAssertEqual(
			record,
			.init(
				id: UUID(1),
				name: "Joseph",
				matchesAgainst: [
					.init(id: UUID(0), score: 1, opponentScore: 123, result: .won),
				],
				gamesPlayed: 1,
				gamesWon: 1,
				gamesLost: 0,
				gamesTied: 0
			)
		)
	}

	func testOpponentRecord_WhenOpponentNotExists_ThrowsError() async throws {
		// Given a database without an opponent
		let bowler = Bowler.Database.mock(id: UUID(0), name: "Sarah")
		let db = try initializeDatabase(withBowlers: .custom([bowler]))

		// Fetching the opponent
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0.database.reader = { db }
				$0.bowlers = .liveValue
			} operation: {
				_ = try await self.bowlers.record(againstOpponent: UUID(1))
			}
		}
	}

	// MARK: Summaries

	func testSummaries_ReturnsMatchingBowlers() async throws {
		// Given a database with 3 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .opponent, archivedOn: nil)
		let bowler3 = Bowler.Database(id: UUID(2), name: "Sarah", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2, bowler3]))

		// Fetching the bowlers
		let bowlers = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.summaries(forIds: [UUID(0), UUID(1)])
		}

		// Returns the expected bowlers
		XCTAssertEqual(bowlers, [.init(id: UUID(0), name: "Joseph"), .init(id: UUID(1), name: "Audriana")])
	}

	func testSummaries_SortsByIDs() async throws {
		// Given a database with 3 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Audriana", kind: .opponent, archivedOn: nil)
		let bowler3 = Bowler.Database(id: UUID(2), name: "Sarah", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2, bowler3]))

		// Fetching the bowlers
		let bowlers = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			// With a specific ID ordering
			try await self.bowlers.summaries(forIds: [UUID(2), UUID(0), UUID(1)])
		}

		// Returns the bowlers in order
		XCTAssertEqual(
			bowlers,
			[
				.init(id: UUID(2), name: "Sarah"),
				.init(id: UUID(0), name: "Joseph"),
				.init(id: UUID(1), name: "Audriana"),
			]
		)
	}

	// MARK: Create

	func testCreate_WhenBowlerExists_ThrowsError() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Create the bowler
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Bowler.Create(id: UUID(0), name: "Joe", kind: .playable)
			try await withDependencies {
				$0.database.writer = { db }
				$0.bowlers = .liveValue
			} operation: {
				try await self.bowlers.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joseph")
		XCTAssertEqual(updated?.kind, .opponent)
	}

	func testCreate_WhenBowlerNotExists_CreatesBowler() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Creating a bowler
		let create = Bowler.Create(id: UUID(0), name: "Joe", kind: .playable)
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.kind, .playable)
	}

	// MARK: Update

	func testUpdate_WhenBowlerExists_UpdatesBowler() async throws {
		// Given a database with an existing bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .opponent, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Editing the bowler
		let editable = Bowler.Edit(id: UUID(0), name: "Joe")
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Joe")
		XCTAssertEqual(updated?.kind, .opponent)

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenBowlerNotExists_ThrowError() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Updating a bowler
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Bowler.Edit(id: UUID(0), name: "Joe")
			try await withDependencies {
				$0.database.writer = { db }
				$0.bowlers = .liveValue
			} operation: {
				try await self.bowlers.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Bowler.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Edit

	func testEdit_WhenBowlerExists_ReturnsBowler() async throws {
		// Given a database with a bowler
		let bowler = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler]))

		// Editing the bowler
		let editable = try await withDependencies {
			$0.database.reader = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.edit(UUID(0))
		}

		// Returns the bowler
		XCTAssertEqual(editable, .init(id: UUID(0), name: "Joseph"))
	}

	func testEdit_WhenBowlerNotExists_ThrowsError() async throws {
		// Given a database with no bowlers
		let db = try initializeDatabase(withBowlers: nil)

		// Editing a bowler
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0.database.reader = { db }
				$0.bowlers = .liveValue
			} operation: {
				_ = try await self.bowlers.edit(UUID(0))
			}
		}
	}

	// MARK: Archive

	func testArchive_WhenIdExists_ArchivesBowler() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", kind: .opponent, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Archiving the first bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.date = .constant(Date(timeIntervalSince1970: 123))
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.archive(UUID(0))
		}

		// Does not delete the entry
		let archiveExists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(archiveExists)

		// Marks the entry as archived
		let archived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(archived?.archivedOn, Date(timeIntervalSince1970: 123))

		// And leaves the other bowler intact
		let otherExists = try await db.read { try Bowler.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
		let otherIsArchived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertNotNil(otherIsArchived)
		XCTAssertNil(otherIsArchived?.archivedOn)
	}

	func testArchive_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Archiving a non-existent bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.date = .constant(Date(timeIntervalSince1970: 123))
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.archive(UUID(1))
		}

		// Leaves the bowler
		let exists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
		let archived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertNil(archived?.archivedOn)
	}

	// MARK: Unarchive

	func testUnarchive_WhenIdExists_UnarchivesBowler() async throws {
		// Given a database with 2 bowlers
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: Date(timeIntervalSince1970: 123))
		let bowler2 = Bowler.Database(id: UUID(1), name: "Sarah", kind: .opponent, archivedOn: Date(timeIntervalSince1970: 123))
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]))

		// Unarchiving the first bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.unarchive(UUID(0))
		}

		// Does not delete the entry
		let archiveExists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(archiveExists)

		// Marks the entry as unarchived
		let archived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertNil(archived?.archivedOn)

		// And leaves the other bowler intact
		let otherExists = try await db.read { try Bowler.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
		let otherIsArchived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertNotNil(otherIsArchived)
		XCTAssertEqual(otherIsArchived?.archivedOn, Date(timeIntervalSince1970: 123))
	}

	func testUnarchive_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 bowler
		let bowler1 = Bowler.Database(id: UUID(0), name: "Joseph", kind: .playable, archivedOn: nil)
		let db = try initializeDatabase(withBowlers: .custom([bowler1]))

		// Unarchiving a non-existent bowler
		try await withDependencies {
			$0.database.writer = { db }
			$0.bowlers = .liveValue
		} operation: {
			try await self.bowlers.unarchive(UUID(1))
		}

		// Leaves the bowler
		let exists = try await db.read { try Bowler.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
		let archived = try await db.read { try Bowler.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertNil(archived?.archivedOn)
	}
}
