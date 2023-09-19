@testable import AlleysRepository
@testable import AlleysRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import LanesRepositoryInterface
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class AlleysRepositoryTests: XCTestCase {
	@Dependency(\.alleys) var alleys

	// MARK: - List

	func testList_ReturnsAllAlleys() async throws {
		// Given a database with two alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Skyview", mechanism: .dedicated)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2]))

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.list(ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the alleys
		XCTAssertEqual(fetched, [.init(alley1), .init(alley2)])
	}

	func testList_WithAlleyWithLocation_ReturnsAlleyWithLocation() async throws {
		// Given a database with alleys
		let db = try initializeDatabase(withAlleys: .default)

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.list(ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the alleys
		XCTAssertEqual(fetched, [
			.init(
				id: UUID(1),
				name: "Grandview",
				material: .synthetic,
				pinFall: .strings,
				mechanism: .interchangeable,
				pinBase: .black,
				location: .init(
					id: UUID(1),
					title: "321 Real Street",
					subtitle: "Viewgrand",
					coordinate: .init(latitude: 321, longitude: 321)
				),
				average: nil
			),
			.init(
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
					coordinate: .init(latitude: 123, longitude: 123)
				),
				average: nil
			),
		])
	}

	func testList_FilterByProperty_ReturnsOneAlley() async throws {
		// Given a database with two alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2]))

		// Fetching the alleys by wood material
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.filteredList(withMaterial: .wood, ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns one alley
		XCTAssertEqual(fetched, [.init(alley1)])
	}

	func testList_FilterByMultipleProperties_ReturnsNone() async throws {
		// Given a database with two alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Commodore", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3]))

		// Fetching the alleys by wood material and freefall
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.filteredList(withMaterial: .wood, withPinFall: .freefall, ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns no alleys
		XCTAssertEqual(fetched, [])
	}

	func testList_SortsByName() async throws {
		// Given a database with three alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2]))

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.list(ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the alleys
		XCTAssertEqual(fetched, [.init(alley2), .init(alley1)])
	}

	func testList_SortedByRecentlyUsed_SortsByRecentlyUsed() async throws {
		// Given a database with two alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(0), UUID(1)])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.list(ordered: .byRecentlyUsed)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the alleys sorted by recently used ids
		XCTAssertEqual(fetched, [.init(alley1), .init(alley2)])
	}

	func testList_WithGames_CalculatesAverages() async throws {
		// Given a database with 2 alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		// and 2 games each
		let game1 = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0, score: 100)
		let game2 = Game.Database.mock(seriesId: UUID(0), id: UUID(1), index: 1, score: 200)
		let game3 = Game.Database.mock(seriesId: UUID(2), id: UUID(2), index: 0, score: 250)
		let game4 = Game.Database.mock(seriesId: UUID(2), id: UUID(3), index: 1, score: 300)
		let db = try initializeDatabase(
			withAlleys: .custom([alley1, alley2]),
			withGames: .custom([game1, game2, game3, game4])
		)

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.list(ordered: .byName)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alleys with averages
		XCTAssertEqual(fetched, [
			.init(alley2, average: 275),
			.init(alley1, average: 150),
		])
	}

	// MARK: - Most Recently Used

	func testMostRecentlyUsed_ReturnsAlleys() async throws {
		// Given a database with four alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Homeview", pinBase: .black)
		let alley4 = Alley.Database.mock(id: UUID(3), name: "Worldview", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3, alley4]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.mostRecentlyUsed(3)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected alleys
		XCTAssertEqual(fetched, [
			.init(alley2),
			.init(alley3),
			.init(alley1),
		])
	}

	func testMostRecentlyUsed_WithRecentlyUsedAlleys_ReturnsAlleysOrderedByRecentlyUsed() async throws {
		// Given a database with four alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Homeview", pinBase: .black)
		let alley4 = Alley.Database.mock(id: UUID(3), name: "Worldview", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3, alley4]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(3), UUID(0)])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.mostRecentlyUsed(3)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected alleys
		XCTAssertEqual(fetched, [
			.init(alley4),
			.init(alley1),
			.init(alley2),
		])
	}

	func testMostRecentlyUsed_WithRecentlyUsedAlleys_WithLimit_ReturnsAlleysOrderedByRecentlyUsed() async throws {
		// Given a database with four alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Homeview", pinBase: .black)
		let alley4 = Alley.Database.mock(id: UUID(3), name: "Worldview", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3, alley4]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(3), UUID(0)])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.mostRecentlyUsed(1)
		}
		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected alleys
		XCTAssertEqual(fetched, [
			.init(alley4),
		])
	}

	// MARK: Summaries

	func testPickable_ReturnsMatchingAlleys() async throws {
		// Given a database with four alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Homeview", pinBase: .black)
		let alley4 = Alley.Database.mock(id: UUID(3), name: "Worldview", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3, alley4]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.pickable()
		}

		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected alleys
		XCTAssertEqual(fetched, [.init(alley2), .init(alley3), .init(alley1), .init(alley4)])
	}

	func testPickable_SortsByMostRecent() async throws {
		// Given a database with 4 alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Grandview", mechanism: .dedicated)
		let alley3 = Alley.Database.mock(id: UUID(2), name: "Homeview", pinBase: .black)
		let alley4 = Alley.Database.mock(id: UUID(3), name: "Worldview", pinFall: .freefall)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2, alley3, alley4]))

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(3), UUID(0)])

		// Fetching the alleys
		let alleys = withDependencies {
			$0.database.reader = { db }
			$0.recentlyUsed.observeRecentlyUsedIds = { _ in recentStream }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.pickable()
		}

		var iterator = alleys.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alleys in order
		XCTAssertEqual(fetched, [.init(alley4), .init(alley1), .init(alley2), .init(alley3)])
	}

	// MARK: - Load

	func testLoad_WhenAlleyExists_ReturnsAlley() async throws {
		// Given a database with one alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Fetching the alleys
		let alley = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.load(UUID(0))
		}
		var iterator = alley.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley
		XCTAssertEqual(fetched, .init(alley1))
	}

	func testLoad_WhenAlleyHasLocation_ReturnsAlleyWithLocation() async throws {
		// Given a database with one alley
		let alley1 = Alley.Database.mock(
			id: UUID(0),
			name: "Grandview",
			material: .wood,
			location: UUID(0)
		)
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Fetching the alleys
		let alley = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.load(UUID(0))
		}
		var iterator = alley.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the alley
		XCTAssertEqual(
			fetched,
			.init(
				id: UUID(0),
				name: "Grandview",
				material: .wood,
				pinFall: nil,
				mechanism: nil,
				pinBase: nil,
				location: .init(
					id: UUID(0),
					title: "123 Fake Street",
					subtitle: "Grandview",
					coordinate: .init(latitude: 123, longitude: 123)
				)
			)
		)
	}

	func testLoad_WhenAlleyNotExists_ReturnsNil() async throws {
		// Given a database with no alleys
		let db = try initializeDatabase(withAlleys: nil)

		// Fetching the alleys
		let alley = withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			self.alleys.load(UUID(0))
		}
		var iterator = alley.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns nil
		XCTAssertNil(fetched)
	}

	// MARK: - Create

	func testCreate_WhenAlleyExists_ThrowsError() async throws {
		// Given a database with an existing alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview")
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Creating the alley
		let new = Alley.Create(
			id: UUID(0),
			name: "Skyview Lanes",
			material: .wood,
			pinFall: nil,
			mechanism: nil,
			pinBase: nil,
			location: nil
		)
		await assertThrowsError(ofType: DatabaseError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.alleys = .liveValue
			} operation: {
				try await self.alleys.create(new)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Alley.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the existing alley
		let existing = try await db.read { try Alley.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(existing?.id, UUID(0))
		XCTAssertEqual(existing?.name, "Grandview")
		XCTAssertNil(existing?.material)
	}

	func testCreate_WhenAlleyNotExists_CreatesAlley() async throws {
		// Given a database with no alleys
		let db = try initializeDatabase()

		// Creating the alley
		let new = Alley.Create(
			id: UUID(0),
			name: "Skyview Lanes",
			material: .wood,
			pinFall: nil,
			mechanism: nil,
			pinBase: nil,
			location: nil
		)
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await AlleysRepository.liveValue.create(new)
		}

		// Inserts the alley
		let count = try await db.read { try Alley.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let created = try await db.read { try Alley.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(created?.id, UUID(0))
		XCTAssertEqual(created?.name, "Skyview Lanes")
	}

	// MARK: - Update

	func testUpdate_WhenAlleyExists_UpdatesAlley() async throws {
		// Given a database with an existing alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Skyview", location: UUID(0))
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Editing the alley
		let editable = Alley.Edit(
			id: UUID(0),
			name: "Skyview Lanes",
			material: .wood,
			pinFall: nil,
			mechanism: nil,
			pinBase: nil,
			location: nil
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Alley.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Skyview Lanes")
		XCTAssertNil(updated?.locationId)
		XCTAssertEqual(updated?.material, .wood)

		// Does not insert any records
		let count = try await db.read { try Alley.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenAlleyNotExists_ThrowsError() async throws {
		// Given a database with no alleys
		let db = try initializeDatabase(withAlleys: nil)

		// Saving an alley
		let editable = Alley.Edit(
			id: UUID(0),
			name: "Skyview Lanes",
			material: .wood,
			pinFall: nil,
			mechanism: nil,
			pinBase: nil,
			location: nil
		)
		await assertThrowsError(ofType: RecordError.self) {
			try await withDependencies {
				$0.database.writer = { db }
				$0.alleys = .liveValue
			} operation: {
				try await self.alleys.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Alley.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: - Edit

	func testEdit_WhenAlleyExists_ReturnsAlley() async throws {
		// Given a database with one alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Editing the alley
		let alley = try await withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.edit(UUID(0))
		}

		// Returns the alley
		XCTAssertEqual(
			alley,
			.init(
				alley: .init(
					id: UUID(0),
					name: "Grandview",
					material: .wood,
					pinFall: nil,
					mechanism: nil,
					pinBase: nil,
					location: nil
				),
				lanes: []
			)
		)
	}

	func testEdit_WhenAlleyExistsWithLocation_ReturnsAlleyWithLocation() async throws {
		// Given a database with one alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood, location: UUID(0))
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Editing the alley
		let alley = try await withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.edit(UUID(0))
		}

		// Returns the alley
		XCTAssertEqual(
			alley,
			.init(
				alley: .init(
					id: UUID(0),
					name: "Grandview",
					material: .wood,
					pinFall: nil,
					mechanism: nil,
					pinBase: nil,
					location: .init(
						id: UUID(0),
						title: "123 Fake Street",
						subtitle: "Grandview",
						coordinate: .init(latitude: 123, longitude: 123)
					)
				),
				lanes: []
			)
		)
	}

	func testEdit_WhenAlleyExistsWithLanes_ReturnsAlleyWithLanes() async throws {
		// Given a database with one alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let db = try initializeDatabase(
			withAlleys: .custom([alley1]),
			withLanes: .custom([
				Lane.Database(alleyId: UUID(0), id: UUID(0), label: "1", position: .leftWall),
				Lane.Database(alleyId: UUID(0), id: UUID(1), label: "10", position: .noWall),
				Lane.Database(alleyId: UUID(0), id: UUID(2), label: "2", position: .noWall),
			])
		)

		// Editing the alley
		let alley = try await withDependencies {
			$0.database.reader = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.edit(UUID(0))
		}

		// Returns the alley
		XCTAssertEqual(
			alley,
			.init(
				alley: .init(
					id: UUID(0),
					name: "Grandview",
					material: .wood,
					pinFall: nil,
					mechanism: nil,
					pinBase: nil,
					location: nil
				),
				lanes: [
					.init(id: UUID(0), label: "1", position: .leftWall),
					.init(id: UUID(2), label: "2", position: .noWall),
					.init(id: UUID(1), label: "10", position: .noWall),
				]
			)
		)
	}

	func testEdit_WhenAlleyNotExists_ThrowsError() async throws {
		// Given a database with no alleys
		let db = try initializeDatabase(withAlleys: nil)

		// Editing the alley
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0.database.reader = { db }
				$0.alleys = .liveValue
			} operation: {
				_ = try await self.alleys.edit(UUID(0))
			}
		}
	}

	// MARK: - Delete

	func testDelete_WhenIdExists_DeletesAlley() async throws {
		// Given a database with 2 alleys
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let alley2 = Alley.Database.mock(id: UUID(1), name: "Skyview", mechanism: .dedicated)
		let db = try initializeDatabase(withAlleys: .custom([alley1, alley2]))

		// Deleting the first alley
		try await withDependencies {
			$0.database.writer = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try Alley.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other alley intact
		let otherExists = try await db.read { try Alley.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 alley
		let alley1 = Alley.Database.mock(id: UUID(0), name: "Grandview", material: .wood)
		let db = try initializeDatabase(withAlleys: .custom([alley1]))

		// Deleting a non-existent alley
		try await withDependencies {
			$0.database.writer = { db }
			$0.alleys = .liveValue
		} operation: {
			try await self.alleys.delete(UUID(1))
		}

		// Leaves the alley
		let exists = try await db.read { try Alley.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}
