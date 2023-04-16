import DatabaseModelsLibrary
@testable import DatabaseService
import Dependencies
@testable import FramesRepository
@testable import FramesRepositoryInterface
import GRDB
import ModelsLibrary
import TestUtilitiesLibrary
import XCTest

@MainActor
final class FramesRepositoryTests: XCTestCase {
	let bowlerId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000001A")!
	let leagueId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000001B")!
	let seriesId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000001C")!

	let gameId1 = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!
	let gameId2 = UUID(uuidString: "00000000-0000-0000-0000-00000000000B")!

	func testEdit_ReturnsFramesForOneGame() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(game: gameId1, ordinal: 1)
		let frame2 = Frame.Database.mock(game: gameId2, ordinal: 1)
		let db = try await initializeDatabase(inserting: [frame1, frame2])

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await FramesRepository.liveValue.edit(self.gameId1)
		}

		// Returns one frame
		XCTAssertEqual(
			frames,
			[.init(game: gameId1, ordinal: 1, rolls: [])]
		)
	}

	func testEdit_WhenGameExists_ReturnsFrames() async throws {
		// Given a database with frames
		let frame1 = Frame.Database.mock(game: gameId1, ordinal: 1)
		let frame2 = Frame.Database.mock(game: gameId1, ordinal: 2)
		let db = try await initializeDatabase(inserting: [frame1, frame2])

		// Editing the frames
		let frames = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await FramesRepository.liveValue.edit(self.gameId1)
		}

		// Returns the game
		XCTAssertEqual(
			frames,
			[.init(game: gameId1, ordinal: 1, rolls: []), .init(game: gameId1, ordinal: 2, rolls: [])]
		)
	}

	func testEdit_WhenGameNotExists_ReturnsNil() async throws {
		// Given a database with no frames
		let db = try await initializeDatabase(inserting: [])

		// Editing the game
		let frames = try await withDependencies {
			$0.database.reader = { db }
		} operation: {
			try await FramesRepository.liveValue.edit(self.gameId1)
		}

		// Returns nil
		XCTAssertNil(frames)
	}

	func testUpdate_WhenFrameExists_UpdatesFrame() async throws {
		// Given a database with a frame
		let frame1 = Frame.Database.mock(game: gameId1, ordinal: 1)
		let db = try await initializeDatabase(inserting: [frame1])

		// Editing the frame
		let editable = Frame.Edit(game: gameId1, ordinal: 1, rolls: [.default, .init(pinsDowned: [.headPin], didFoul: true)])
		try await withDependencies {
			$0.database.writer = { db }
		} operation: {
			try await FramesRepository.liveValue.update(editable)
		}

		// Updates the database
		let updated = try await db.read {
			try Frame.Database
				.all()
				.filter(Frame.Database.Columns.game == self.gameId1)
				.filter(Frame.Database.Columns.ordinal == 1)
				.fetchOne($0)
		}
		XCTAssertEqual(updated?.id, "\(gameId1)-1")
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
		let db = try await initializeDatabase(inserting: [])

		// Updating a frame
		await assertThrowsError(ofType: RecordError.self) {
			let editable = Frame.Edit(
				game: gameId1,
				ordinal: 1,
				rolls: [.default, .init(pinsDowned: [.headPin], didFoul: true)]
			)
			try await withDependencies {
				$0.database.writer = { db }
			} operation: {
				try await FramesRepository.liveValue.update(editable)
			}
		}

		// Does not insert any records
		let count = try await db.read { try Frame.Database.fetchCount($0) }
		XCTAssertEqual(count, 0)
	}

	private func initializeDatabase(
		inserting frames: [Frame.Database] = []
	) async throws -> any DatabaseWriter {
		let dbQueue = try DatabaseQueue()
		var migrator = DatabaseMigrator()
		try migrator.prepare(dbQueue)

		let bowler = Bowler.Database(id: bowlerId1, name: "Joseph", status: .playable)
		let league = League.Database(
			bowler: bowlerId1,
			id: leagueId1,
			name: "Majors",
			recurrence: .repeating,
			numberOfGames: 4,
			additionalPinfall: nil,
			additionalGames: nil,
			excludeFromStatistics: .include,
			alley: nil
		)
		let series = Series.Database(
			league: leagueId1,
			id: seriesId1,
			date: Date(),
			numberOfGames: 4,
			preBowl: .regular,
			excludeFromStatistics: .include,
			alley: nil
		)

		let games = [
			Game.Database(
				series: seriesId1,
				id: gameId1,
				ordinal: 1,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			),
			Game.Database(
				series: seriesId1,
				id: gameId2,
				ordinal: 2,
				locked: .open,
				manualScore: nil,
				excludeFromStatistics: .include
			)
		]

		try await dbQueue.write {
			try bowler.insert($0)
			try league.insert($0)
			try series.insert($0)
			for game in games {
				try game.insert($0)
			}
			for frame in frames {
				try frame.insert($0)
			}
		}

		return dbQueue
	}
}

extension Frame.Database {
	static func mock(
		game: Game.ID = UUID(uuidString: "00000000-0000-0000-0000-00000000000A")!,
		ordinal: Int,
		roll0: String? = nil,
		roll1: String? = nil,
		roll2: String? = nil
	) -> Self {
		.init(
			game: game,
			ordinal: ordinal,
			roll0: roll0,
			roll1: roll1,
			roll2: roll2
		)
	}
}
