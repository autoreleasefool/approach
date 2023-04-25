import DatabaseModelsLibrary
import Dependencies
@testable import FramesRepository
@testable import FramesRepositoryInterface
import GRDB
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class FramesRepositoryTests: XCTestCase {
	@Dependency(\.frames) var frames

	// MARK: - Load

	func testLoad_ReturnsFramesForOneGame() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), ordinal: 1)
		let frame2 = Frame.Database.mock(gameId: UUID(1), ordinal: 1)
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: .custom([frame1, frame2])
		)

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.load(UUID(0))
		}

		// Returns one frame
		XCTAssertEqual(
			frames,
			[.init(gameId: UUID(0), ordinal: 1, rolls: [])]
		)
	}

	func testLoad_WhenGameExists_ReturnsFrames() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), ordinal: 1)
		let frame2 = Frame.Database.mock(gameId: UUID(0), ordinal: 2)
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: .custom([frame1, frame2])
		)

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.load(UUID(0))
		}

		// Returns the game
		XCTAssertEqual(
			frames,
			[.init(gameId: UUID(0), ordinal: 1, rolls: []), .init(gameId: UUID(0), ordinal: 2, rolls: [])]
		)
	}

	func testLoad_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no frames
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: nil
		)

		// Editing the game
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.load(UUID(0))
		}

		// Returns nil
		XCTAssertNil(frames)
	}

	// MARK: - Edit

	func testEdit_ReturnsFramesForOneGame() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), ordinal: 1)
		let frame2 = Frame.Database.mock(gameId: UUID(1), ordinal: 1)
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: .custom([frame1, frame2])
		)

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.edit(UUID(0))
		}

		// Returns one frame
		XCTAssertEqual(
			frames,
			[.init(gameId: UUID(0), ordinal: 1, rolls: [])]
		)
	}

	func testEdit_WhenGameExists_ReturnsFrames() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), ordinal: 1)
		let frame2 = Frame.Database.mock(gameId: UUID(0), ordinal: 2)
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: .custom([frame1, frame2])
		)

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.edit(UUID(0))
		}

		// Returns the game
		XCTAssertEqual(
			frames,
			[.init(gameId: UUID(0), ordinal: 1, rolls: []), .init(gameId: UUID(0), ordinal: 2, rolls: [])]
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no frames
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: nil
		)

		// Editing the game
		let frames = try await withDependencies {
			$0.database.reader = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.edit(UUID(0))
		}

		// Returns nil
		XCTAssertNil(frames)
	}

	// MARK: - Update

	func testUpdate_WhenFrameExists_UpdatesFrame() async throws {
		// Given a database with a frame
		let frame1 = Frame.Database.mock(gameId: UUID(0), ordinal: 1)
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: .custom([frame1])
		)

		// Editing the frame
		let editable = Frame.Edit(
			gameId: UUID(0),
			ordinal: 1,
			rolls: [.default, .init(pinsDowned: [.headPin], didFoul: true)]
		)
		try await withDependencies {
			$0.database.writer = { db }
			$0.frames = .liveValue
		} operation: {
			try await self.frames.update(editable)
		}

		// Updates the database
		let updated = try await db.read {
			try Frame.Database
				.all()
				.filter(Frame.Database.Columns.gameId == UUID(0))
				.filter(Frame.Database.Columns.ordinal == 1)
				.fetchOne($0)
		}
		XCTAssertEqual(updated?.id, "\(UUID(0))-1")
		XCTAssertEqual(updated?.ordinal, 1)
		XCTAssertEqual(updated?.roll0, "000000")
		XCTAssertEqual(updated?.roll1, "100100")
		XCTAssertNil(updated?.roll2)

		// Does not insert any records
		let count = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenFrameNotExists_ThrowError() async throws {
		// Given a database with no frames
		let db = try await initializeDatabase(
			withAlleys: .default,
			withLanes: .default,
			withBowlers: .default,
			withLeagues: .default,
			withSeries: .default,
			withGames: .default,
			withFrames: nil
		)

		// Updating a frame
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Frame.Edit(
				gameId: UUID(0),
				ordinal: 1,
				rolls: [.default, .init(pinsDowned: [.headPin], didFoul: true)]
			)
			try await withDependencies {
				$0.database.writer = { db }
				$0.frames = .liveValue
			} operation: {
				try await self.frames.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}
}

extension Frame.Database {
	static func mock(
		gameId: Game.ID = UUID(0),
		ordinal: Int,
		roll0: String? = nil,
		roll1: String? = nil,
		roll2: String? = nil
	) -> Self {
		.init(
			gameId: gameId,
			ordinal: ordinal,
			roll0: roll0,
			roll1: roll1,
			roll2: roll2
		)
	}
}
