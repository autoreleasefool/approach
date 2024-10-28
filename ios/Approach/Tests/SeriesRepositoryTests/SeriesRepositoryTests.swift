import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import ModelsLibrary
@testable import SeriesRepository
@testable import SeriesRepositoryInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesPackageLibrary
import XCTest

final class SeriesRepositoryTests: XCTestCase {
	@Dependency(SeriesRepository.self) var series

	// MARK: List

	func testList_ReturnsAllSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000), archivedOn: Date())

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]), withGames: .custom([game1, game2, game3, game4]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .newestFirst)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
		])
	}

	func testList_FilterByLeague_ReturnsLeagueSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2, game3, game4]))

		// Fetching the series by league
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .newestFirst)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one series
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
		])
	}

	func testList_Ordering_ByNewestFirst_ReturnsSeriesOrderedByDate() async throws {
		// Given a database with three series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 2, score: 450)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 321)
		let game7 = Game.Database.mock(seriesId: UUID(2), id: UUID(6), index: 0, score: 0)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .newestFirst)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series3, withScores: [.init(index: 0, score: 0), .init(index: 1, score: 321), .init(index: 2, score: 450)], withTotal: 771),
		])
	}

	func testList_Ordering_ByNewestFirst_WithAppliedDate_ReturnsSeriesOrderedByDate() async throws {
		// Given a database with three series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001), appliedDate: Date(timeIntervalSince1970: 123_456_003))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 2, score: 450)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 321)
		let game7 = Game.Database.mock(seriesId: UUID(2), id: UUID(6), index: 0, score: 0)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .newestFirst)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
			.init(series3, withScores: [.init(index: 0, score: 0), .init(index: 1, score: 321), .init(index: 2, score: 450)], withTotal: 771),
		])
	}

	func testList_Ordering_ByOldestFirst_ReturnsSeriesOrderedByDate() async throws {
		// Given a database with three series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 2, score: 450)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 321)
		let game7 = Game.Database.mock(seriesId: UUID(2), id: UUID(6), index: 0, score: 0)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .oldestFirst)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series3, withScores: [.init(index: 0, score: 0), .init(index: 1, score: 321), .init(index: 2, score: 450)], withTotal: 771),
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
		])
	}

	func testList_Ordering_ByLowestToHighest_ReturnsSeriesOrderedByTotal() async throws {
		// Given a database with four series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))
		let series4 = Series.Database.mock(id: UUID(3), date: Date(timeIntervalSince1970: 123_456_003))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 2, score: 450)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 321)
		let game7 = Game.Database.mock(seriesId: UUID(2), id: UUID(6), index: 0, score: 0)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 0, score: 3)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3, series4]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .lowestToHighest)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series4, withScores: [.init(index: 0, score: 3)], withTotal: 3),
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
			.init(series3, withScores: [.init(index: 0, score: 0), .init(index: 1, score: 321), .init(index: 2, score: 450)], withTotal: 771),
		])
	}

	func testList_Ordering_ByHighestToLowest_ReturnsSeriesOrderedByTotal() async throws {
		// Given a database with four series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))
		let series4 = Series.Database.mock(id: UUID(3), date: Date(timeIntervalSince1970: 123_456_003))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)
		let game5 = Game.Database.mock(seriesId: UUID(2), id: UUID(4), index: 2, score: 450)
		let game6 = Game.Database.mock(seriesId: UUID(2), id: UUID(5), index: 1, score: 321)
		let game7 = Game.Database.mock(seriesId: UUID(2), id: UUID(6), index: 0, score: 0)
		let game8 = Game.Database.mock(seriesId: UUID(3), id: UUID(7), index: 0, score: 3)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3, series4]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7, game8]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), orderedBy: .highestToLowest)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series3, withScores: [.init(index: 0, score: 0), .init(index: 1, score: 321), .init(index: 2, score: 450)], withTotal: 771),
			.init(series2, withScores: [.init(index: 0, score: 456), .init(index: 1, score: 123)], withTotal: 579),
			.init(series1, withScores: [.init(index: 0, score: 1), .init(index: 1, score: 2)], withTotal: 3),
			.init(series4, withScores: [.init(index: 0, score: 3)], withTotal: 3),
		])
	}

	// MARK: Summaries

	func testSummaries_ReturnsAllSeries() async throws {
		// Given a database with 3 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000), archivedOn: Date())

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.summaries(bowledIn: UUID(0))
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series
		XCTAssertEqual(fetched, [
			.init(series1),
			.init(series2),
		])
	}

	func testSummaries_WithAppliedDate_SortsByAppliedDate() async throws {
		// Given a database with four series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_002))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000), appliedDate: Date(timeIntervalSince1970: 123_456_005))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_006), appliedDate: Date(timeIntervalSince1970: 123_456_003))
		let series4 = Series.Database.mock(id: UUID(3), date: Date(timeIntervalSince1970: 123_456_004))

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3, series4]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.summaries(bowledIn: UUID(0))
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series in order by appliedDate
		XCTAssertEqual(fetched, [
			.init(series2),
			.init(series4),
			.init(series3),
			.init(series1),
		])
	}

	func testSummaries_FilterByLeague_ReturnsLeagueSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]))

		// Fetching the series by league
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.summaries(bowledIn: UUID(0))
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one series
		XCTAssertEqual(fetched, [.init(series1)])
	}

	func testSummaries_SortsByDate() async throws {
		// Given a database with three series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.summaries(bowledIn: UUID(0))
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series2),
			.init(series1),
			.init(series3),
		])
	}

	// MARK: Unused Pre-Bowls

	func testUnusedPreBowls_ReturnsUnusedPreBowls() async throws {
		// Given a database with series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_005))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_004), preBowl: .preBowl)
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_003), archivedOn: Date())
		let series4 = Series.Database.mock(id: UUID(3), date: Date(timeIntervalSince1970: 123_456_002), appliedDate: Date(timeIntervalSince1970: 123_456_000), preBowl: .preBowl)
		let series5 = Series.Database.mock(id: UUID(4), date: Date(timeIntervalSince1970: 123_456_001), preBowl: .preBowl)

		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2, series3, series4, series5]))

		// Fetching the series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.unusedPreBowls(bowledIn: UUID(0))
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the unused series
		XCTAssertEqual(fetched, [
			.init(series2),
			.init(series5),
		])
	}

	// MARK: Archived

	func testArchived_ReturnsArchivedSeries() async throws {
		// Given a database with series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 1))
		let series2 = Series.Database.mock(leagueId: UUID(0), id: UUID(1), date: Date(timeIntervalSince1970: 456))
		let series3 = Series.Database.mock(leagueId: UUID(0), id: UUID(2), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 3))
		let series4 = Series.Database.mock(leagueId: UUID(0), id: UUID(3), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 2))
		// 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 0)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 0)

		let db = try initializeApproachDatabase(
			withSeries: .custom([series1, series2, series3, series4]),
			withGames: .custom([game1, game2, game3, game4])
		)

		// Fetching the archived series
		let series = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			self.series.archived()
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the series
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), date: Date(timeIntervalSince1970: 123), bowlerName: "Joseph", leagueName: "Majors", totalNumberOfGames: 0, archivedOn: Date(timeIntervalSince1970: 3)),
			.init(id: UUID(3), date: Date(timeIntervalSince1970: 123), bowlerName: "Joseph", leagueName: "Majors", totalNumberOfGames: 0, archivedOn: Date(timeIntervalSince1970: 2)),
			.init(id: UUID(0), date: Date(timeIntervalSince1970: 123), bowlerName: "Joseph", leagueName: "Majors", totalNumberOfGames: 2, archivedOn: Date(timeIntervalSince1970: 1)),
		])
	}

	// MARK: Use Pre-Bowl

	func testUsePreBowl_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no existing series
		let db = try initializeApproachDatabase(withSeries: .zero)

		// Updating the series throws an error
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				try await series.usePreBowl(UUID(0), Date(timeIntervalSince1970: 123))
			}
		}

		// Does not insert any records
		let count = try await db.read { try Series.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testUsePreBowl_WhenIsExcluded_UpdatesToInclude() async throws {
		// Given a database with a pre-bowl
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), preBowl: .preBowl, excludeFromStatistics: .exclude)
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Updating the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await series.usePreBowl(UUID(0), Date(timeIntervalSince1970: 123_456))
		}

		// Updates the database
		let updated = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.excludeFromStatistics, .include)
	}

	func testUsePreBowl_WhenIsExcluded_UpdatesToIncludeGames() async throws {
		// Given a database with a pre-bowl and excluded games
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), preBowl: .preBowl, excludeFromStatistics: .exclude)
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123), preBowl: .preBowl, excludeFromStatistics: .exclude)
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, excludeFromStatistics: .exclude)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, excludeFromStatistics: .exclude)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, excludeFromStatistics: .exclude)
		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2, game3]))

		// Updating the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await series.usePreBowl(UUID(0), Date(timeIntervalSince1970: 123_456))
		}

		// Updates the database
		let updatedGame1 = try await db.read { try Game.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updatedGame1?.excludeFromStatistics, .include)
		let updatedGame2 = try await db.read { try Game.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(updatedGame2?.excludeFromStatistics, .include)

		// Does not update unrelated games
		let updatedGame3 = try await db.read { try Game.Database.fetchOne($0, id: UUID(2)) }
		XCTAssertEqual(updatedGame3?.excludeFromStatistics, .exclude)
	}

	func testUsePreBowl_UpdatesAppliedDate() async throws {
		// Given a database with a pre-bowl
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), preBowl: .preBowl, excludeFromStatistics: .exclude)
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Updating the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await series.usePreBowl(UUID(0), Date(timeIntervalSince1970: 123_456))
		}

		// Updates the database
		let updated = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.date, Date(timeIntervalSince1970: 123))
		XCTAssertEqual(updated?.appliedDate, Date(timeIntervalSince1970: 123_456))
	}

	// MARK: Game Host

	func testGameHost_WhenSeriesExists_ReturnsSeries() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Fetching the series
		let series = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.gameHost(UUID(0))
		}

		// Returns the series
		XCTAssertEqual(
			series,
			.init(
				id: UUID(0),
				date: Date(timeIntervalSince1970: 123_456_000),
				appliedDate: nil,
				preBowl: .regular
			)
		)
	}

	func testGameHost_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withSeries: .zero)

		// Fetching the series throws an error
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				_ = try await self.series.gameHost(UUID(0))
			}
		}
	}

	// MARK: Shareable

	func testShareable_WhenSeriesExists_ReturnsSeries() async throws {
		// Given a database with series and games
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 1_234))
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 225)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 300)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 225)
		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2, game3]))

		// Fetching the series
		let series = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self].shareable = SeriesRepository.liveValue.shareable
		} operation: {
			try await self.series.shareable(UUID(0))
		}

		XCTAssertEqual(
			series,
			Series.Shareable(
				id: UUID(0),
				date: Date(timeIntervalSince1970: 123),
				bowlerName: "Joseph",
				leagueName: "Majors",
				total: 525,
				scores: [
					.init(index: 0, score: 225),
					.init(index: 1, score: 300),
				]
			)
		)
	}

	func testShareable_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withSeries: .zero)

		// Fetching the series throws an error
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[SeriesRepository.self].shareable = SeriesRepository.liveValue.shareable
			} operation: {
				_ = try await self.series.shareable(UUID(0))
			}
		}
	}

	// MARK: Create

	func testCreate_WhenSeriesExists_ThrowsError() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Creating the series throws an error
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Series.Create(
				leagueId: UUID(0),
				id: UUID(0),
				date: Date(timeIntervalSince1970: 123_456_002),
				preBowl: .regular,
				excludeFromStatistics: .exclude,
				numberOfGames: 1
			)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				try await self.series.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Series.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.date, Date(timeIntervalSince1970: 123_456_001))
	}

	func testCreate_WhenSeriesNotExists_CreatesSeries() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withLeagues: .default, withSeries: nil)

		// Creating the series throws an error
		let create = Series.Create(
			leagueId: UUID(0),
			id: UUID(0),
			date: Date(timeIntervalSince1970: 123_456_001),
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			numberOfGames: 1
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.date, Date(timeIntervalSince1970: 123_456_001))
	}

	func testCreate_WhenSeriesNotExists_CreatesGames() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withLeagues: .default, withSeries: nil)

		// Creating the series throws an error
		let create = Series.Create(
			leagueId: UUID(0),
			id: UUID(0),
			date: Date(timeIntervalSince1970: 123_456_001),
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			numberOfGames: 1
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.create(create)
		}

		// Inserted the games and frames
		let numberOfGames = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(numberOfGames, 1)

		let numberOfFrames = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(numberOfFrames, 10)
	}

	func testCreate_WhenSeriesNotExists_WithPreferredGear_AddsPreferredGear() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withGear: .default, withLeagues: .default, withSeries: nil, withBowlerPreferredGear: .default)

		// Creating the series
		let create = Series.Create(
			leagueId: UUID(0),
			id: UUID(0),
			date: Date(timeIntervalSince1970: 123_456_001),
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			numberOfGames: 1
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.create(create)
		}

		// Inserts game gear
		let numberOfGameGear = try await db.read { try GameGear.Database.fetchCount($0) }
		XCTAssertEqual(numberOfGameGear, 2)
	}

	func testCreate_WhenSeriesNotExists_WithManualScores_CreatesGamesWithScores() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withLeagues: .default, withSeries: nil)

		// Creating the series
		let create = Series.Create(
			leagueId: UUID(0),
			id: UUID(0),
			date: Date(timeIntervalSince1970: 123_456_001),
			preBowl: .regular,
			excludeFromStatistics: .include,
			numberOfGames: 2,
			manualScores: [125, 400]
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.create(create)
		}

		// Inserts games with manual scores
		let games = try await db.read { try Game.Database.order(Game.Database.Columns.index.asc).fetchAll($0) }
		XCTAssertEqual(
			games,
			[
				.mock(id: UUID(0), index: 0, score: 125, locked: .locked, scoringMethod: .manual),
				.mock(id: UUID(1), index: 1, score: 400, locked: .locked, scoringMethod: .manual),
			]
		)
	}

	// MARK: Update

	func testUpdate_WhenSeriesExists_UpdatesSeries() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Editing the series
		let editable = Series.Edit(
			leagueId: UUID(0),
			id: UUID(0),
			numberOfGames: 3,
			leagueRecurrence: .repeating,
			date: Date(timeIntervalSince1970: 123_456_999),
			preBowl: .regular,
			excludeFromStatistics: series1.excludeFromStatistics,
			location: nil
		)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.date, Date(timeIntervalSince1970: 123_456_999))

		// Does not insert any records
		let count = try await db.read { try Series.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withSeries: nil)

		// Updating a series
		let editable = Series.Edit(
			leagueId: UUID(0),
			id: UUID(0),
			numberOfGames: 4,
			leagueRecurrence: .repeating,
			date: Date(timeIntervalSince1970: 123_456_999),
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			location: nil
		)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				try await self.series.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Series.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Add Games to Series

	func testAddGamesToSeries_WhenSeriesExists_AddsGames() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let game2 = Game.Database.mock(id: UUID(1), index: 1)
		let db = try initializeApproachDatabase(withSeries: .custom([series1]), withGames: .custom([game1, game2]))

		// Adding games to the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			_ = $0.uuid()
			_ = $0.uuid()
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.addGamesToSeries(UUID(0), 2)
		}

		// Inserted the games and frames
		let numberOfGames = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(numberOfGames, 4)

		let allGames = try await db.read { try Game.Database.order(Game.Database.Columns.index).fetchAll($0) }
		XCTAssertEqual(allGames, [
			.mock(id: UUID(0), index: 0),
			.mock(id: UUID(1), index: 1),
			.mock(id: UUID(2), index: 2),
			.mock(id: UUID(3), index: 3),
		])

		let numberOfFrames = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(numberOfFrames, 20)
	}

	func testAddGamesToSeries_WhenSeriesExists_HasNoGames_AddsGames() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]), withGames: .zero)

		// Adding games to the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.addGamesToSeries(UUID(0), 2)
		}

		// Inserted the games and frames
		let numberOfGames = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(numberOfGames, 2)

		let allGames = try await db.read { try Game.Database.order(Game.Database.Columns.index).fetchAll($0) }
		XCTAssertEqual(allGames, [
			.mock(id: UUID(0), index: 0),
			.mock(id: UUID(1), index: 1),
		])

		let numberOfFrames = try await db.read { try Frame.Database.fetchCount($0) }
		// Only 20 because we added 2 games which generated 10 frames each. The initial games did not include frames
		XCTAssertEqual(numberOfFrames, 20)
	}

	func testAddGamesToSeries_WhenSeriesHasOneGame_AddsGamesWithCorrectIndex() async throws {
		// Given a database with one series and one game
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let game1 = Game.Database.mock(id: UUID(0), index: 0)
		let db = try initializeApproachDatabase(withSeries: .custom([series1]), withGames: .custom([game1]))

		// Adding a game to the series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.uuid = .incrementing
			_ = $0.uuid()
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.addGamesToSeries(UUID(0), 1)
		}

		// Inserted the games and frames
		let numberOfGames = try await db.read { try Game.Database.fetchCount($0) }
		XCTAssertEqual(numberOfGames, 2)

		let allGames = try await db.read { try Game.Database.order(Game.Database.Columns.index).fetchAll($0) }
		XCTAssertEqual(allGames, [
			.mock(id: UUID(0), index: 0),
			.mock(id: UUID(1), index: 1),
		])
	}

	func testAddGamesToSeries_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withSeries: nil)

		// Editing the series
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				_ = try await self.series.addGamesToSeries(UUID(0), 3)
			}
		}
	}

	// MARK: Edit

	func testEdit_WhenSeriesExists_ReturnsSeries() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Editing the series
		let series = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.edit(UUID(0))
		}

		// Returns the series
		XCTAssertEqual(
			series,
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				numberOfGames: 0,
				leagueRecurrence: .repeating,
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				location: nil
			)
		)
	}

	func testEdit_WhenSeriesExists_WhenAlleyExists_ReturnsSeriesWithAlley() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000), alleyId: UUID(0))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Editing the series
		let series = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.edit(UUID(0))
		}

		// Returns the series
		XCTAssertEqual(
			series,
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				numberOfGames: 0,
				leagueRecurrence: .repeating,
				date: Date(timeIntervalSince1970: 123_456_000),
				preBowl: .regular,
				excludeFromStatistics: .include,
				location: .init(
					id: UUID(0),
					name: "Skyview",
					material: .wood,
					pinFall: .strings,
					mechanism: .dedicated,
					pinBase: nil,
					location: .init(
						id: UUID(0),
						title: "123 Fake Street",
						subtitle: "Grandview",
						coordinate: .init(latitude: 123.0, longitude: 123.0)
					)
				)
			)
		)
	}

	func testEdit_WhenSeriesNotExists_ThrowsError() async throws {
		// Given a database with no series
		let db = try initializeApproachDatabase(withSeries: nil)

		// Editing the series
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[SeriesRepository.self] = .liveValue
			} operation: {
				_ = try await self.series.edit(UUID(0))
			}
		}
	}

	// MARK: Archive

	func testArchive_WhenIdExists_ArchivesSeries() async throws {
		// Given a database with 2 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), archivedOn: nil)
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123), archivedOn: nil)
		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]))

		// Archiving the first series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.date = .constant(Date(timeIntervalSince1970: 123))
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.archive(UUID(0))
		}

		// Does not delete the entry
		let archiveExists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(archiveExists)

		// Marks the entry as archived
		let archived = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(archived?.archivedOn, Date(timeIntervalSince1970: 123))

		// And leaves the other series intact
		let otherExists = try await db.read { try Series.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
		let otherIsArchived = try await db.read { try Series.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertNotNil(otherIsArchived)
		XCTAssertNil(otherIsArchived?.archivedOn)
	}

	func testArchive_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), archivedOn: nil)
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Archiving a non-existent series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0.date = .constant(Date(timeIntervalSince1970: 123))
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.archive(UUID(1))
		}

		// Leaves the series
		let exists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
		let archived = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertNil(archived?.archivedOn)
	}

	// MARK: Unarchive

	func testUnarchive_WhenIdExists_UnarchivesSeries() async throws {
		// Given a database with 2 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 124))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 124))
		let db = try initializeApproachDatabase(withSeries: .custom([series1, series2]))

		// Unarchiving the first series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.unarchive(UUID(0))
		}

		// Does not delete the entry
		let archiveExists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(archiveExists)

		// Marks the entry as unarchived
		let archived = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertNil(archived?.archivedOn)

		// And leaves the other series intact
		let otherExists = try await db.read { try Series.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
		let otherIsArchived = try await db.read { try Series.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertNotNil(otherIsArchived)
		XCTAssertEqual(otherIsArchived?.archivedOn, Date(timeIntervalSince1970: 124))
	}

	func testUnarchive_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123), archivedOn: Date(timeIntervalSince1970: 123))
		let db = try initializeApproachDatabase(withSeries: .custom([series1]))

		// Unarchiving a non-existent series
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[SeriesRepository.self] = .liveValue
		} operation: {
			try await self.series.unarchive(UUID(1))
		}

		// Leaves the series
		let exists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
		let archived = try await db.read { try Series.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertNotNil(archived)
		XCTAssertEqual(archived?.archivedOn, Date(timeIntervalSince1970: 123))
	}
}
