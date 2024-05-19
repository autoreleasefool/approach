import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
@testable import MatchPlaysRepository
@testable import MatchPlaysRepositoryInterface
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesPackageLibrary
import XCTest

final class MatchPlaysRepositoryTests: XCTestCase {
	@Dependency(MatchPlaysRepository.self) var matchPlays

	// MARK: Create

	func testCreate_WhenMatchPlayExists_ThrowsError() async throws {
		// Given a database with an existing match play
		let matchPlay = MatchPlay.Database(
			gameId: UUID(0),
			id: UUID(1),
			opponentId: UUID(0),
			opponentScore: 123,
			result: .lost
		)
		let db = try initializeDatabase(withMatchPlays: .custom([matchPlay]))

		// Creating the match play
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = MatchPlay.Create(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await self.matchPlays.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try MatchPlay.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(updated?.id, UUID(1))
		XCTAssertEqual(updated?.opponentId, UUID(0))
		XCTAssertEqual(updated?.result, .lost)
	}

	func testCreate_WhenMatchPlayNotExists_CreatesMatchPlay() async throws {
		// Given a database with no match plays
		let db = try initializeDatabase(withGames: .default, withMatchPlays: nil)

		// Creating a match play
		let create = MatchPlay.Create(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[MatchPlaysRepository.self] = .liveValue
		} operation: {
			try await self.matchPlays.create(create)
		}

		// Inserts the record
		let exists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(updated?.id, UUID(1))
		XCTAssertEqual(updated?.opponentId, nil)
		XCTAssertEqual(updated?.result, .tied)
	}

	// MARK: Update

	func testUpdate_WhenMatchPlayExists_UpdatesMatchPlay() async throws {
		// Given a database with an existing match play
		let matchPlay = MatchPlay.Database(
			gameId: UUID(0),
			id: UUID(1),
			opponentId: UUID(0),
			opponentScore: 123,
			result: .lost
		)
		let db = try initializeDatabase(withMatchPlays: .custom([matchPlay]))

		// Updating the match play
		let edit = MatchPlay.Edit(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[MatchPlaysRepository.self] = .liveValue
		} operation: {
			try await self.matchPlays.update(edit)
		}

		// Does not insert any records
		let count = try await db.read { try MatchPlay.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Updates the database
		let updated = try await db.read { try MatchPlay.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(updated?.id, UUID(1))
		XCTAssertEqual(updated?.opponentId, nil)
		XCTAssertEqual(updated?.result, .tied)
	}

	func testUpdate_WhenMatchPlayNotExists_ThrowsError() async throws {
		// Given a database with no match plays
		let db = try initializeDatabase(withMatchPlays: nil)

		// Updating a match play
		await assertThrowsError(ofType: RecordError.self) {
			let edit = MatchPlay.Edit(gameId: UUID(0), id: UUID(1), opponent: nil, opponentScore: 456, result: .tied)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[MatchPlaysRepository.self] = .liveValue
			} operation: {
				try await self.matchPlays.update(edit)
			}
		}

		// Does not insert any records
		let count = try await db.read { try MatchPlay.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesMatchPlay() async throws {
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
		let db = try initializeDatabase(withMatchPlays: .custom([matchPlay1, matchPlay2]))

		// Deleting the first match play
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[MatchPlaysRepository.self] = .liveValue
		} operation: {
			try await self.matchPlays.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other match plau intact
		let otherExists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 match play
		let matchPlay1 = MatchPlay.Database(
			gameId: UUID(0),
			id: UUID(0),
			opponentId: UUID(0),
			opponentScore: nil,
			result: .tied
		)
		let db = try initializeDatabase(withMatchPlays: .custom([matchPlay1]))

		// Deleting a non-existent match play
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[MatchPlaysRepository.self] = .liveValue
		} operation: {
			try await self.matchPlays.delete(UUID(1))
		}

		// Leaves the match play
		let exists = try await db.read { try MatchPlay.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}
