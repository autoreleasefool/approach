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
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "000100", ball0: UUID(0))
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0)
		let db = try initializeDatabase(withFrames: .custom([frame1, frame2]))

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
			[
				.init(
					gameId: UUID(0),
					index: 0,
					rolls: [
						.init(
							index: 0,
							roll: .init(pinsDowned: [.headPin], didFoul: false),
							bowlingBall: .init(id: UUID(0), name: "Yellow")
						),
					]
				),
			]
		)
	}

	func testLoad_WhenGameExists_ReturnsFrames() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "100110", ball0: UUID(1))
		let frame2 = Frame.Database.mock(gameId: UUID(0), index: 1)
		let db = try initializeDatabase(withFrames: .custom([frame1, frame2]))

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
			[
				.init(
					gameId: UUID(0),
					index: 0,
					rolls: [
						.init(
							index: 0,
							roll: .init(pinsDowned: [.headPin, .rightThreePin], didFoul: true),
							bowlingBall: .init(id: UUID(1), name: "Blue")
						),
					]
				),
				.init(
					gameId: UUID(0),
					index: 1,
					rolls: []
				),
			]
		)
	}

	func testLoad_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no frames
		let db = try initializeDatabase(withFrames: nil)

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
		let frame1 = Frame.Database
			.mock(gameId: UUID(0), index: 0, roll0: "110100", roll1: "011000", ball0: UUID(0), ball1: UUID(1))
		let frame2 = Frame.Database.mock(gameId: UUID(1), index: 0)
		let db = try initializeDatabase(withFrames: .custom([frame1, frame2]))

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
			[
				.init(
					gameId: UUID(0),
					index: 0,
					rolls: [
						.init(
							index: 0,
							roll: .init(pinsDowned: [.leftTwoPin, .headPin], didFoul: true),
							bowlingBall: .init(id: UUID(0), name: "Yellow")
						),
						.init(
							index: 1,
							roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin], didFoul: false),
							bowlingBall: .init(id: UUID(1), name: "Blue")
						),
					]
				),
			]
		)
	}

	func testEdit_WhenGameExists_ReturnsFrames() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: "000101")
		let frame2 = Frame.Database.mock(gameId: UUID(0), index: 1)
		let db = try initializeDatabase(withFrames: .custom([frame1, frame2]))

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
			[
				.init(
					gameId: UUID(0),
					index: 0,
					rolls: [.init(index: 0, roll: .init(pinsDowned: [.headPin, .rightTwoPin], didFoul: false), bowlingBall: nil)]
				),
				.init(gameId: UUID(0), index: 1, rolls: []),
			]
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no frames
		let db = try initializeDatabase(withFrames: nil)

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
		let frame1 = Frame.Database.mock(gameId: UUID(0), index: 0)
		let db = try initializeDatabase(withFrames: .custom([frame1]))

		// Editing the frame
		let editable = Frame.Edit(
			gameId: UUID(0),
			index: 0,
			rolls: [
				.init(index: 0, roll: .default, bowlingBall: nil),
				.init(index: 1, roll: .init(pinsDowned: [.headPin], didFoul: true), bowlingBall: nil),
			]
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
				.filter(Frame.Database.Columns.index == 0)
				.fetchOne($0)
		}
		XCTAssertEqual(updated?.id, "\(UUID(0))-0")
		XCTAssertEqual(updated?.index, 0)
		XCTAssertEqual(updated?.roll0, "000000")
		XCTAssertEqual(updated?.roll1, "100100")
		XCTAssertNil(updated?.roll2)

		// Does not insert any records
		let count = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)
	}

	func testUpdate_WhenFrameNotExists_ThrowError() async throws {
		// Given a database with no frames
		let db = try initializeDatabase(withFrames: nil)

		// Updating a frame
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Frame.Edit(
				gameId: UUID(0),
				index: 0,
				rolls: [
					.init(index: 0, roll: .default, bowlingBall: nil),
					.init(index: 1, roll: .init(pinsDowned: [.headPin], didFoul: true), bowlingBall: nil),
				]
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
