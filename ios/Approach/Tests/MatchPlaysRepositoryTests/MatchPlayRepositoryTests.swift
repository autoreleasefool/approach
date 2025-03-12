import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import MatchPlaysRepository
@testable import MatchPlaysRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("MatchPlaysRepository", .tags(.grdb))
struct MatchPlaysRepositoryTests {

	// MARK: Create

	@Suite("create")
	struct CreateTests {
		@Dependency(MatchPlaysRepository.self) var matchPlays

		@Test("Throws error when match play ID exists")
		func throwsErrorWhenMatchPlayIDExists() async throws {
			// Given a database with an existing match play
			let matchPlay = MatchPlay.Database(
				gameId: UUID(0),
				id: UUID(1),
				opponentId: UUID(0),
				opponentScore: 123,
				result: .lost
			)
			let db = try initializeApproachDatabase(withMatchPlays: .custom([matchPlay]))

			// Creating the match play
			await #expect(throws: DatabaseError.self) {
				let create = MatchPlay.Create(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
				try await withDependencies {
					$0[DatabaseService.self].writer = { @Sendable in db }
					$0[MatchPlaysRepository.self] = .liveValue
				} operation: {
					try await matchPlays.create(create)
				}
			}

			// Does not insert any records
			let count = try await db.read { try MatchPlay.Database.fetchCount($0) }
			#expect(count == 1)

			// Does not update the database
			let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
			#expect(updated?.id == UUID(1))
			#expect(updated?.opponentId == UUID(0))
			#expect(updated?.result == .lost)
		}

		@Test("Creates match play when match play ID does not exist")
		func createsMatchPlayWhenMatchPlayIDDoesNotExist() async throws {
			// Given a database with no match plays
			let db = try initializeApproachDatabase(withGames: .default, withMatchPlays: nil)

			// Creating a match play
			let create = MatchPlay.Create(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await matchPlays.create(create)
			}

			// Inserts the record
			let exists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(1)) }
			#expect(exists)

			// Updates the database
			let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
			#expect(updated?.id == UUID(1))
			#expect(updated?.opponentId == nil)
			#expect(updated?.result == .tied)
		}
	}

	// MARK: Update

	@Suite("update")
	struct UpdateTests {
		@Dependency(MatchPlaysRepository.self) var matchPlays

		@Test("Updates match play when match play ID exists")
		func updatesMatchPlayWhenIDExists() async throws {
			// Given a database with an existing match play
			let matchPlay = MatchPlay.Database(
				gameId: UUID(0),
				id: UUID(1),
				opponentId: UUID(0),
				opponentScore: 123,
				result: .lost
			)
			let db = try initializeApproachDatabase(withMatchPlays: .custom([matchPlay]))

			// Updating the match play
			let edit = MatchPlay.Edit(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await matchPlays.update(edit)
			}

			// Does not insert any records
			let count = try await db.read { try MatchPlay.Database.fetchCount($0) }
			#expect(count == 1)

			// Updates the database
			let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
			#expect(updated?.id == UUID(1))
			#expect(updated?.opponentId == nil)
			#expect(updated?.result == .tied)
		}

		@Test("Throws error when match play ID does not exist")
		func throwsErrorWhenIDDoesNotExist() async throws {
			// Given a database with no match plays
			let db = try initializeApproachDatabase(withMatchPlays: nil)

			// Updating a match play
			await #expect(throws: RecordError.self) {
				let edit = MatchPlay.Edit(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
				try await withDependencies {
					$0[DatabaseService.self].writer = { @Sendable in db }
					$0[MatchPlaysRepository.self] = .liveValue
				} operation: {
					try await matchPlays.update(edit)
				}
			}

			// Does not insert any records
			let matchPlayCount = try await db.read { try MatchPlay.Database.fetchCount($0) }
			#expect(matchPlayCount == 0)
		}
	}

	// MARK: Delete

	@Suite("Delete")
	struct DeleteTests {
		@Dependency(MatchPlaysRepository.self) var matchPlays

		@Test("Deletes match play when ID exists")
		func deletesMatchPlayWhenIDExists() async throws {
			// Given a database with 2 match plays
			let matchPlay1 = MatchPlay.Database(
				gameId: UUID(0),
				id: UUID(0),
				opponentId: UUID(0),
				opponentScore: nil,
				result: .tied
			)
			let matchPlay2 = MatchPlay.Database(
				gameId: UUID(1),
				id: UUID(1),
				opponentId: UUID(1),
				opponentScore: 123,
				result: .lost
			)
			let db = try initializeApproachDatabase(withMatchPlays: .custom([matchPlay1, matchPlay2]))

			// Deleting the first match play
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await matchPlays.delete(UUID(0))
			}

			// Updates the database
			let deletedExists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(0)) }
			#expect(!deletedExists)

			// And leaves the other match plau intact
			let otherExists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(1)) }
			#expect(otherExists)
		}

		@Test("Does nothing when ID does not exist")
		func doesNothingWhenIDDoesNotExist() async throws {
			// Given a database with 1 match play
			let matchPlay1 = MatchPlay.Database(
				gameId: UUID(0),
				id: UUID(0),
				opponentId: UUID(0),
				opponentScore: nil,
				result: .tied
			)
			let db = try initializeApproachDatabase(withMatchPlays: .custom([matchPlay1]))

			// Deleting a non-existent match play
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await self.matchPlays.delete(UUID(1))
			}

			// Leaves the match play
			let exists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(0)) }
			#expect(exists)
		}
	}
}
