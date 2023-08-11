import DatabaseModelsLibrary
import Dependencies
import GRDB
@testable import ModelsLibrary
@testable import SeriesRepository
@testable import SeriesRepositoryInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class SeriesRepositoryTests: XCTestCase {
	@Dependency(\.series) var series

	// MARK: List

	func testList_ReturnsAllSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))

		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 1)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 2)
		let game3 = Game.Database.mock(seriesId: UUID(1), id: UUID(2), index: 0, score: 456)
		let game4 = Game.Database.mock(seriesId: UUID(1), id: UUID(3), index: 1, score: 123)

		let db = try initializeDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2, game3, game4]))

		// Fetching the series
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), ordering: .byDate)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [1, 2], withTotal: 3),
			.init(series2, withScores: [456, 123], withTotal: 579),
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

		let db = try initializeDatabase(withSeries: .custom([series1, series2]), withGames: .custom([game1, game2, game3, game4]))

		// Fetching the series by league
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), ordering: .byDate)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one series
		XCTAssertEqual(fetched, [
			.init(series1, withScores: [1, 2], withTotal: 3),
		])
	}

	func testList_SortsByDate() async throws {
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

		let db = try initializeDatabase(withSeries: .custom([series1, series2, series3]), withGames: .custom([game1, game2, game3, game4, game5, game6, game7]))

		// Fetching the series
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			self.series.list(bowledIn: UUID(0), ordering: .byDate)
		}
		var iterator = series.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the series sorted by date
		XCTAssertEqual(fetched, [
			.init(series2, withScores: [456, 123], withTotal: 579),
			.init(series1, withScores: [1, 2], withTotal: 3),
			.init(series3, withScores: [0, 321, 450], withTotal: 771),
		])
	}

	// MARK: Summaries

	func testSummaries_ReturnsAllSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))

		let db = try initializeDatabase(withSeries: .custom([series1, series2]))

		// Fetching the series
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
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

	func testSummaries_FilterByLeague_ReturnsLeagueSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))

		let db = try initializeDatabase(withSeries: .custom([series1, series2]))

		// Fetching the series by league
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
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

		let db = try initializeDatabase(withSeries: .custom([series1, series2, series3]))

		// Fetching the series
		let series = withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
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

	// MARK: Create

	func testCreate_WhenSeriesExists_ThrowsError() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try initializeDatabase(withSeries: .custom([series1]))

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
				$0.database.writer = { db }
				$0.series = .liveValue
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
		let db = try initializeDatabase(withLeagues: .default, withSeries: nil)

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
			$0.database.writer = { db }
			$0.uuid = .incrementing
			$0.series = .liveValue
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

	// MARK: Update

	func testUpdate_WhenSeriesExists_UpdatesSeries() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try initializeDatabase(withSeries: .custom([series1]))

		// Editing the series
		let editable = Series.Edit(
			leagueId: UUID(0),
			id: UUID(0),
			numberOfGames: series1.numberOfGames,
			date: Date(timeIntervalSince1970: 123_456_999),
			preBowl: .regular,
			excludeFromStatistics: series1.excludeFromStatistics,
			location: nil
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.series = .liveValue
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
		let db = try initializeDatabase(withSeries: nil)

		// Updating a series
		let editable = Series.Edit(
			leagueId: UUID(0),
			id: UUID(0),
			numberOfGames: 4,
			date: Date(timeIntervalSince1970: 123_456_999),
			preBowl: .regular,
			excludeFromStatistics: .exclude,
			location: nil
		)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.series = .liveValue
			} operation: {
				try await self.series.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Series.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Edit

	func testEdit_WhenSeriesExists_ReturnsSeries() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try initializeDatabase(withSeries: .custom([series1]))

		// Editing the series
		let series = try await withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			try await self.series.edit(UUID(0))
		}

		// Returns the series
		XCTAssertEqual(
			series,
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				numberOfGames: 3,
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
		let db = try initializeDatabase(withSeries: .custom([series1]))

		// Editing the series
		let series = try await withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			try await self.series.edit(UUID(0))
		}

		// Returns the series
		XCTAssertEqual(
			series,
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				numberOfGames: 3,
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
					location: nil
				)
			)
		)
	}

	func testEdit_WhenSeriesNotExists_ReturnsNil() async throws {
		// Given a database with no series
		let db = try initializeDatabase(withSeries: nil)

		// Editing the series
		let series = try await withDependencies {
			$0.database.reader = { db }
			$0.series = .liveValue
		} operation: {
			try await self.series.edit(UUID(0))
		}

		// Returns nil
		XCTAssertNil(series)
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesSeries() async throws {
		// Given a database with 2 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try initializeDatabase(withSeries: .custom([series1, series2]))

		// Deleting the first series
		try await withDependencies {
			$0.database.writer = { db }
			$0.series = .liveValue
		} operation: {
			try await self.series.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other series intact
		let otherExists = try await db.read { try Series.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try initializeDatabase(withSeries: .custom([series1]))

		// Deleting a non-existent series
		try await withDependencies {
			$0.database.writer = { db }
			$0.series = .liveValue
		} operation: {
			try await self.series.delete(UUID(1))
		}

		// Leaves the series
		let exists = try await db.read { try Series.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}
