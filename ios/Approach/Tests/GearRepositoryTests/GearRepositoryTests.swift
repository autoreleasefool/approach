import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
@testable import GearRepository
@testable import GearRepositoryInterface
import GRDB
@testable import ModelsLibrary
import RecentlyUsedServiceInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesPackageLibrary

@Suite("GearRepository Tests")
struct GearRepositoryTests {

	// MARK: - List

	@Suite("list")
	struct ListTests {
		@Dependency(GearRepository.self) var gear

		@Test("Returns all gear")
		func returnsAllGear() async throws {
			// Given a database with two gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue")
			let db = try initializeApproachDatabase(withAvatars: .default, withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

			// Fetching the gear
			let list = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.list(ordered: .byName)
			}
			var iterator = list.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .init(id: UUID(0), value: .mock)),
			]

			// Returns all the gear
			#expect(fetched == expectedResults)
		}

		@Test("Filtering by Kind returns matching gear")
		func filteringByKindReturnsMatchingGear() async throws {
			// Given a database with two gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall)
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel)
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

			// Fetching the gear
			let list = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.list(ofKind: .bowlingBall, ordered: .byName)
			}
			var iterator = list.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			]

			// Returns the matching gear
			#expect(fetched == expectedResults)
		}

		@Test("Filtering by owner returns matching gear")
		func filteringByOwnerReturnsMatchingGear() async throws {
			// Given a database with two gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

			// Fetching the gear
			let list = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.list(ownedBy: UUID(0), ordered: .byName)
			}
			var iterator = list.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			]

			// Returns the matching gear
			#expect(fetched == expectedResults)
		}

		@Test("Order by recently used returns ordered list")
		func orderByRecentlyUsedReturnsOrderedList() async throws {
			// Given a database with two gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .towel, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2, gear3]), withBowlerPreferredGear: .zero)

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([UUID(2), UUID(0), UUID(1)])

			// Fetching the gear
			let list = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.list(ordered: .byRecentlyUsed)
			}
			var iterator = list.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(2), name: "Green", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
				.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			]

			// Returns the gear ordered by ids
			#expect(fetched == expectedResults)
		}
	}

	// MARK: - Preferred

	@Suite("preferredGear")
	struct PreferredGearTests {
		@Dependency(GearRepository.self) var gear

		@Test("Returns gear for bowler")
		func returnsGearForBowler() async throws {
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
			let db = try initializeApproachDatabase(withBowlers: .custom([bowler1, bowler2]), withGear: .custom([gear1, gear2, gear3]), withBowlerPreferredGear: .custom([bowlerGear1, bowlerGear2, bowlerGear3, bowlerGear4]))

			// Fetching the gear
			let preferred = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.preferredGear(forBowler: UUID(0))
			}

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(1), name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			]

			// Returns all the gear
			#expect(preferred == expectedResults)
		}
	}

	// MARK: - Most Recently Used

	@Suite("mostRecentlyUsed")
	struct MostRecentlyUsedTests {
		@Dependency(GearRepository.self) var gear

		@Test("Returns gear")
		func returnsGear() async throws {
			// Given a database with four gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
			let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([])

			// Fetching the gear
			let mostRecentlyUsed = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.mostRecentlyUsed(limit: 3)
			}
			var iterator = mostRecentlyUsed.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
				.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
				.init(id: UUID(3), name: "Red", kind: .shoes, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			]

			// Returns the expected gear
			#expect(fetched == expectedResults)
		}

		@Test("Returns gear sorted by recently used")
		func returnsGearSortedByRecentlyUsed() async throws {
			// Given a database with four gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
			let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([UUID(2), UUID(3)])

			// Fetching the gear
			let mostRecentlyUsed = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.mostRecentlyUsed(limit: 3)
			}
			var iterator = mostRecentlyUsed.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
				.init(id: UUID(3), name: "Red", kind: .shoes, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
				.init(id: UUID(1), name: "Blue", kind: .towel, ownerName: "Sarah", avatar: .mock(id: UUID(0))),
			]

			// Returns the expected gear
			#expect(fetched == expectedResults)
		}

		@Test("Returns limited number of gear when limit provided")
		func returnsLimitedNumberOfGearWhenLimitProvided() async throws {
			// Given a database with four gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
			let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([UUID(2), UUID(3)])

			// Fetching the gear
			let mostRecentlyUsed = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.mostRecentlyUsed(limit: 1)
			}
			var iterator = mostRecentlyUsed.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(2), name: "Green", kind: .other, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			]

			// Returns the expected gear
			#expect(fetched == expectedResults)
		}

		@Test("Returns gear of the correct kind when filtering by kind")
		func returnsGearOfTheCorrectKindWhenFilteringByKind() async throws {
			// Given a database with four gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0))
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue", kind: .towel, bowlerId: UUID(1))
			let gear3 = Gear.Database.mock(id: UUID(2), name: "Green", kind: .other, bowlerId: UUID(0))
			let gear4 = Gear.Database.mock(id: UUID(3), name: "Red", kind: .shoes, bowlerId: UUID(1))
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2, gear3, gear4]), withBowlerPreferredGear: .zero)

			// Given an ordering of ids
			let (recentStream, recentContinuation) = AsyncStream<[UUID]>.makeStream()
			recentContinuation.yield([UUID(2), UUID(3)])

			// Fetching the gear
			let mostRecentlyUsed = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[RecentlyUsedService.self].observeRecentlyUsedIds = { @Sendable _ in recentStream }
				$0[GearRepository.self] = .liveValue
			} operation: {
				gear.mostRecentlyUsed(ofKind: .bowlingBall)
			}
			var iterator = mostRecentlyUsed.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expectedResults: [Gear.Summary] = [
				.init(id: UUID(0), name: "Yellow", kind: .bowlingBall, ownerName: "Joseph", avatar: .mock(id: UUID(0))),
			]

			// Returns the expected gear
			#expect(fetched == expectedResults)
		}
	}

	// MARK: - Create

	@Suite("create")
	struct CreateTests {
		@Dependency(GearRepository.self) var gear

		@Test("Throws error when Gear ID exists")
		func throwsErrorWhenGearIDExists() async throws {
			// Given a database with an existing gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
			let db = try initializeApproachDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

			// Create the gear
			await assertThrowsError(ofType: DatabaseError.self) {
				let create = Gear.Create(id: UUID(0), name: "Blue", kind: .towel, owner: .init(id: UUID(1), name: "Sarah"), avatar: .mock(id: UUID(0)))
				try await withDependencies {
					$0[DatabaseService.self].writer = { @Sendable in db }
					$0[GearRepository.self] = .liveValue
				} operation: {
					try await gear.create(create)
				}
			}

			// Does not insert any records
			let count = try await db.read { try Gear.Database.fetchCount($0) }
			#expect(count == 1)

			// Does not update the database
			let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.name == "Yellow")
			#expect(updated?.kind == .bowlingBall)
			#expect(updated?.bowlerId == UUID(0))
		}

		@Test("Creates a new gear when Gear ID does not exist")
		func createsANewGearWhenGearIDDoesNotExist() async throws {
			// Given a database with no gear
			let db = try initializeApproachDatabase(withGear: nil)

			// Creating a gear
			let create = Gear.Create(id: UUID(0), name: "Yellow", kind: .bowlingBall, owner: nil, avatar: .mock(id: UUID(0)))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.create(create)
			}

			// Inserted the record
			let exists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
			#expect(exists)

			// Updates the database
			let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.name == "Yellow")
			#expect(updated?.kind == .bowlingBall)
		}

		@Test("Creates Avatar DB record when Gear has attached Avatar")
		func createsAvatarDBRecordWhenGearHasAttachedAvatar() async throws {
			// Given a database with no gear
			let db = try initializeApproachDatabase(withGear: nil)

			// Creating a gear
			let create = Gear.Create(id: UUID(0), name: "Yellow", kind: .bowlingBall, owner: nil, avatar: .init(id: UUID(0), value: .mock))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.create(create)
			}

			// Inserted the record
			let exists = try await db.read { try Avatar.Database.exists($0, id: UUID(0)) }
			#expect(exists)

			// Updates the database
			let updated = try await db.read { try Avatar.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.value == .mock)
		}
	}

	// MARK: - Update

	@Suite("update")
	struct UpdateTests {
		@Dependency(GearRepository.self) var gear

		@Test("Updates gear when Gear ID exists")
		func updatesGearWhenGearIDExists() async throws {
			// Given a database with an existing gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: nil)
			let db = try initializeApproachDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

			// Editing the gear
			let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: .init(id: UUID(0), name: "Sarah"), avatar: .mock(id: UUID(0)))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.update(editable)
			}

			// Updates the database
			let updated = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.name == "Blue")
			#expect(updated?.kind == .bowlingBall)
			#expect(updated?.bowlerId == UUID(0))
			#expect(updated?.avatarId == UUID(0))

			// Does not insert any records
			let count = try await db.read { try Gear.Database.fetchCount($0) }
			#expect(count == 1)
		}

		@Test("Throws error when Gear ID does not exist")
		func throwsErrorWhenGearIDDoesNotExist() async throws {
			// Given a database with no gear
			let db = try initializeApproachDatabase(withGear: nil)

			// Updating a gear
			await assertThrowsError(ofType: RecordError.self) {
				let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: nil, avatar: .mock(id: UUID(0)))
				try await withDependencies {
					$0[DatabaseService.self].writer = { @Sendable in db }
					$0[GearRepository.self] = .liveValue
				} operation: {
					try await gear.update(editable)
				}
			}

			// Does not insert any records
			let numberOfRecords = try await db.read { try Gear.Database.fetchCount($0) }
			#expect(numberOfRecords == 0)
		}

		@Test("Updates Avatar DB entry when Gear has Avatar")
		func updatesAvatarDBEntryWhenGearHasAvatar() async throws {
			// Given a database with an existing gear
			let avatar1 = Avatar.Database.mock(id: UUID(0))
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", avatarId: UUID(0))
			let db = try initializeApproachDatabase(withAvatars: .custom([avatar1]), withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

			// Editing the gear
			let editable = Gear.Edit(id: UUID(0), kind: .bowlingBall, name: "Blue", owner: .init(id: UUID(0), name: "Sarah"), avatar: .init(id: UUID(0), value: .text("Bl", .rgb(.init(0, 0, 1)))))
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.update(editable)
			}

			// Updates the avatar
			let updatedAvatar = try await db.read { try Avatar.Database.fetchOne($0, id: UUID(0)) }
			#expect(updatedAvatar?.value == .text("Bl", .rgb(.init(0, 0, 1))))

			// Does not insert any records
			let count = try await db.read { try Avatar.Database.fetchCount($0) }
			#expect(count == 1)

			let updatedGear = try await db.read { try Gear.Database.fetchOne($0, id: UUID(0)) }
			#expect(updatedGear?.avatarId == UUID(0))
		}
	}

	// MARK: - Edit
	@Suite("edit")
	struct EditTest {
		@Dependency(GearRepository.self) var gear

		@Test("Returns gear when gear exists")
		func returnsGearWhenGearExists() async throws {
			// Given a database with a gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow", kind: .bowlingBall, bowlerId: UUID(0), avatarId: UUID(0))
			let db = try initializeApproachDatabase(withAvatars: .default, withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

			// Editing the gear
			let editable = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.edit(UUID(0))
			}

			let expectedResult: Gear.Edit = .init(
				id: UUID(0),
				kind: .bowlingBall,
				name: "Yellow",
				owner: .init(id: UUID(0), name: "Joseph"),
				avatar: .init(id: UUID(0), value: .mock)
			)

			// Returns the gear
			#expect(editable == expectedResult)
		}

		@Test("Throws error when Gear ID does not exist")
		func throwsErrorWhenGearIDDoesNotExist() async throws {
			// Given a database with no gear
			let db = try initializeApproachDatabase(withGear: nil)

			// Editing a gear
			await assertThrowsError(ofType: FetchableError.self) {
				try await withDependencies {
					$0[DatabaseService.self].reader = { @Sendable in db }
					$0[GearRepository.self] = .liveValue
				} operation: {
					_ = try await gear.edit(UUID(0))
				}
			}
		}
	}

	// MARK: - Delete

	@Suite("delete")
	struct DeleteTests {
		@Dependency(GearRepository.self) var gear

		@Test("Deletes gear when ID exists")
		func deletesGearWhenIDExists() async throws {
			// Given a database with 2 gear
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
			let gear2 = Gear.Database.mock(id: UUID(1), name: "Blue")
			let db = try initializeApproachDatabase(withGear: .custom([gear1, gear2]), withBowlerPreferredGear: .zero)

			// Deleting the first gear
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.delete(UUID(0))
			}

			// Updates the database
			let deletedExists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
			#expect(!deletedExists)

			// And leaves the other gear intact
			let otherExists = try await db.read { try Gear.Database.exists($0, id: UUID(1)) }
			#expect(otherExists)
		}

		@Test("Does nothing when ID does not exist")
		func doesNothingWhenIDDoesNotExist() async throws {
			// Given a database with 1
			let gear1 = Gear.Database.mock(id: UUID(0), name: "Yellow")
			let db = try initializeApproachDatabase(withGear: .custom([gear1]), withBowlerPreferredGear: .zero)

			// Deleting a non-existent gear
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[GearRepository.self] = .liveValue
			} operation: {
				try await gear.delete(UUID(1))
			}

			// Leaves the gear
			let exists = try await db.read { try Gear.Database.exists($0, id: UUID(0)) }
			#expect(exists)
		}
	}
}
