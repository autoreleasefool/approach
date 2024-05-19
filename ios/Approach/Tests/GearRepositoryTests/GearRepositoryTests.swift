import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
@testable import GearRepository
@testable import GearRepositoryInterface
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import TestUtilitiesPackageLibrary
import XCTest

final class GearRepositoryTests: XCTestCase {
	@Dependency(GearRepository.self) var gear

	// MARK: - List

	func testList_ReturnsAllGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue")
		let db = try initializeDatabase(withAvatars: .default, withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.list(ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all the gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .init(id: UUID(0), value: .mock)),
		])
	}

	func testList_FilterByKind_ReturnsMatchingGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall)
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel)
		let db = try initializeDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.list(ofKind: .bowlingBall, ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the matching gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
		])
	}

	func testList_FilterByBowler_ReturnsMatchingGear() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.list(ownedBy: UUID(0), ordered: .byName)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the matching gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
		])
	}

	func testList_OrderByRecentlyUsed_ReturnsOrderedList() async throws {
		// Given a database with two gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .towel, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3]), withBowlerPreferredGear: .zero)

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(2), UUID(0), UUID(1)])

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.list(ordered: .byRecentlyUsed)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the gear ordered by ids
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), name: "Green", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
		])
	}

	// MARK: - Preferred

	func testPreferred_ReturnsGearForBowler() async throws {
		// Given a database with two gear preferred by bowler
		let bowler1 = Bowler.Database.mock(id: UUID(0), name: "Joseph")
		let bowler2 = Bowler.Database.mock(id: UUID(1), name: "Sarah")
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue")
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Red")
		let bowlerGear1 = BowlerPreferredGear.Database(bowlerId: UUID(0), gearId: UUID(0))
		let bowlerGear2 = BowlerPreferredGear.Database(bowlerId: UUID(0), gearId: UUID(1))
		let bowlerGear3 = BowlerPreferredGear.Database(bowlerId: UUID(1), gearId: UUID(1))
		let bowlerGear4 = BowlerPreferredGear.Database(bowlerId: UUID(1), gearId: UUID(2))
		let db = try initializeDatabase(withBowlers: .custom([bowler1, bowler2]), withGear: .custom([gear1, gear2, gear3]), withBowlerPreferredGear: .custom([bowlerGear1, bowlerGear2, bowlerGear3, bowlerGear4]))

		// Fetching the gear
		let gear = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.preferredGear(forBowler: UUID(0))
		}

		// Returns all the gear
		XCTAssertEqual(gear, [
			.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
		])
	}

	// MARK: - Most Recently Used

	func testMostRecentlyUsed_ReturnsGear() async throws {
		// Given a database with four gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
		let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([])

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.mostRecentlyUsed(limit: 3)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			.init(id: UUID(3), name: "Red", kind: .shoes, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
		])
	}

	func testMostRecentlyUsed_WithRecentlyUsedGear_ReturnsGearOrderedByRecentlyUsed() async throws {
		// Given a database with four gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
		let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(2), UUID(3)])

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.mostRecentlyUsed(limit: 3)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			.init(id: UUID(3), name: "Red", kind: .shoes, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
		])
	}

	func testMostRecentlyUsed_WithLimit_ReturnsLimitedNumber() async throws {
		// Given a database with four gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
		let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(2), UUID(3)])

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.mostRecentlyUsed(limit: 1)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
		])
	}

	func testMostRecentlyUsed_FilteredByKind_ReturnsGearOfKind() async throws {
		// Given a database with four gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
		let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
		let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
		let db = try initializeDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

		// Given an ordering of ids
		let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
		recentContinuation.yield([UUID(2), UUID(3)])

		// Fetching the gear
		let gear = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
			$0[GearRepository.self] = .liveValue
		} operation: {
			self.gear.mostRecentlyUsed(ofKind: .bowlingBall)
		}
		var iterator = gear.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the expected gear
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
		])
	}

	// MARK: - Create

	func testCreate_WhenGearExists_ThrowsError() async throws {
		// Given a database with an existing gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
		let db = try initializeDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

		// Create the gear
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = Gear.Create(id: UUID(0), name: "Blue", kind: .towel, owner: .init(id: UUID(1), name: "Sarah"), avatar: .mock(id: UUID(0)))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await self.gear.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Yellow")
		XCTAssertEqual(updated?.kind, .bowlingBall)
		XCTAssertEqual(updated?.bowlerId, UUID(0))
	}

	func testCreate_WhenGearNotExists_CreatesGear() async throws {
		// Given a database with no gear
		let db = try initializeDatabase(withGear: nil)

		// Creating a gear
		let create = Gear.Create(id: UUID(0), name: "Yellow", kind: .bowlingBall, owner: nil, avatar: .mock(id: UUID(0)))
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Yellow")
		XCTAssertEqual(updated?.kind, .bowlingBall)
	}

	func testCreate_WhenGearHasAvatar_CreatesAvatar() async throws {
		// Given a database with no gear
		let db = try initializeDatabase(withGear: nil)

		// Creating a gear
		let create = Gear.Create(id: UUID(0), name: "Yellow", kind: .bowlingBall, owner: nil, avatar: .init(id: UUID(0), value: .mock))
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try Avatar.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try Avatar.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.value, .mock)
	}

	// MARK: - Update

	func testUpdate_WhenGearExists_UpdatesGear() async throws {
		// Given a database with an existing gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: nil)
		let db = try initializeDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

		// Editing the gear
		let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: .init(id: UUID(0), name: "Sarah"), avatar: .mock(id: UUID(0)))
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.update(editable)
		}

		// Updates the database
		let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.name, "Blue")
		XCTAssertEqual(updated?.kind, .bowlingBall)
		XCTAssertEqual(updated?.bowlerId, UUID(0))
		XCTAssertEqual(updated?.avatarId, UUID(0))

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenGearNotExists_ThrowError() async throws {
		// Given a database with no gear
		let db = try initializeDatabase(withGear: nil)

		// Updating a gear
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: nil, avatar: .mock(id: UUID(0)))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await self.gear.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Gear.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	func testUpdate_WhenGearHasAvatar_UpdatesAvatar() async throws {
		// Given a database with an existing gear
		let avatar1 = Avatar.Database.mock(id: UUID(0))
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", avatarId: UUID(0))
		let db = try initializeDatabase(withAvatars: .custom([avatar1]), withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

		// Editing the gear
		let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: .init(id: UUID(0), name: "Sarah"), avatar: .init(id: UUID(0), value: .text("Bl", .rgb(.init(0, 0, 1)))))
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.update(editable)
		}

		// Updates the avatar
		let updatedAvatar = try await db.read { try Avatar.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updatedAvatar?.value, .text("Bl", .rgb(.init(0, 0, 1))))

		// Does not insert any records
		let count = try await db.read { try Avatar.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		let updatedGear = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updatedGear?.avatarId, UUID(0))
	}

	// MARK: - Edit

	func testEdit_WhenGearExists_ReturnsGear() async throws {
		// Given a database with a gear
		let gear = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0), avatarId: UUID(0))
		let db = try initializeDatabase(withAvatars: .default, withGear: .custom([gear]), withBowlerPreferredGear: .zero)

		// Editing the gear
		let editable = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.edit(UUID(0))
		}

		// Returns the gear
		XCTAssertEqual(
			editable,
			.init(id: UUID(0), kind: .bowlingBall, name: "Yellow", owner: .init(id: UUID(0), name: "Joseph"), avatar: .init(id: UUID(0), value: .mock))
		)
	}

	func testEdit_WhenGearNotExists_ThrowsError() async throws {
		// Given a database with no gear
		let db = try initializeDatabase(withGear: nil)

		// Editing a gear
		await assertThrowsError(ofType: FetchableError.self) {
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				_ = try await self.gear.edit(UUID(0))
			}
		}
	}

	// MARK: - Delete

	func testDelete_WhenIdExists_DeletesGear() async throws {
		// Given a database with 2 gear
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
		let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue")
		let db = try initializeDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

		// Deleting the first gear
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other gear intact
		let otherExists = try await db.read { try Gear.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1
		let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
		let db = try initializeDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

		// Deleting a non-existent gear
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[GearRepository.self] = .liveValue
		} operation: {
			try await self.gear.delete(UUID(1))
		}

		// Leaves the gear
		let exists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}
