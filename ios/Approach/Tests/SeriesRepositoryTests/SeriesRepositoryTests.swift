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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1, series2])
		)

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
		XCTAssertEqual(fetched, [.init(series1), .init(series2)])
	}

	func testList_FilterByLeague_ReturnsLeagueSeries() async throws {
		// Given a database with two series
		let series1 = Series.Database.mock(leagueId: UUID(0), id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(leagueId: UUID(1), id: UUID(1), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1, series2])
		)

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
		XCTAssertEqual(fetched, [.init(series1)])
	}

	func testList_SortsByDate() async throws {
		// Given a database with three series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let series2 = Series.Database.mock(id: UUID(1), date: Date(timeIntervalSince1970: 123_456_002))
		let series3 = Series.Database.mock(id: UUID(2), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1, series2, series3])
		)

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
		XCTAssertEqual(fetched, [.init(series2), .init(series1), .init(series3)])
	}

	// MARK: Create

	func testCreate_WhenSeriesExists_ThrowsError() async throws {
		// Given a database with an existing series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_001))
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: nil
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: nil
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

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
				series: .init(
					leagueId: UUID(0),
					id: UUID(0),
					numberOfGames: 4,
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					location: nil
				),
				lanes: []
			)
		)
	}

	func testEdit_WhenSeriesExists_WhenAlleyExists_ReturnsSeriesWithAlley() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000), alleyId: UUID(0))
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

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
				series: .init(
					leagueId: UUID(0),
					id: UUID(0),
					numberOfGames: 4,
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					location: .init(
						id: UUID(0),
						name: "Skyview",
						address: nil,
						material: .wood,
						pinFall: .strings,
						mechanism: .dedicated,
						pinBase: nil
					)
				),
				lanes: []
			)
		)
	}

	func testEdit_WhenSeriesExists_WhenLanesExist_ReturnsSeriesWithLanes() async throws {
		// Given a database with one series
		let series1 = Series.Database.mock(id: UUID(0), date: Date(timeIntervalSince1970: 123_456_000))
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

		// And many lanes
		let seriesLane1 = SeriesLane.Database(seriesId: UUID(0), laneId: UUID(1))
		let seriesLane2 = SeriesLane.Database(seriesId: UUID(0), laneId: UUID(0))
		try await db.write {
			try seriesLane1.insert($0)
			try seriesLane2.insert($0)
		}

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
				series: .init(
					leagueId: UUID(0),
					id: UUID(0),
					numberOfGames: 4,
					date: Date(timeIntervalSince1970: 123_456_000),
					preBowl: .regular,
					excludeFromStatistics: .include,
					location: nil
				),
				lanes: [
					.init(id: UUID(0), label: "1", position: .leftWall),
					.init(id: UUID(1), label: "2", position: .noWall),
				]
			)
		)
	}

	func testEdit_WhenSeriesNotExists_ReturnsNil() async throws {
		// Given a database with no series
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: nil
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1, series2])
		)

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
		let db = try await initializeDatabase(
			withAlleys: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .custom([series1])
		)

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

extension Series.Database {
	static func mock(
		leagueId: League.ID = UUID(0),
		id: ID,
		date: Date,
		numberOfGames: Int = 4,
		preBowl: Series.PreBowl = .regular,
		excludeFromStatistics: Series.ExcludeFromStatistics = .include,
		alleyId: Alley.ID? = nil
	) -> Self {
		.init(
			leagueId: leagueId,
			id: id,
			date: date,
			numberOfGames: numberOfGames,
			preBowl: preBowl,
			excludeFromStatistics: excludeFromStatistics,
			alleyId: alleyId
		)
	}
}

extension Series.Summary {
	init(_ from: Series.Database) {
		self.init(
			id: from.id,
			date: from.date
		)
	}
}
