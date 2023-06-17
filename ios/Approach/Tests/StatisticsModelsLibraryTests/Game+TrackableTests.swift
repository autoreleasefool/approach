@testable import DatabaseModelsLibrary
import GRDB
@testable import ModelsLibrary
@testable import StatisticsModelsLibrary
import TestDatabaseUtilitiesLibrary
import XCTest

final class GameTrackableTests: XCTestCase {

	// MARK: Frames

	func testTrackableFrames_ReturnsFrames() async throws {
		let game = Game.Database.mock(seriesId: UUID(0), id: UUID(0), index: 0)

		let frame = Frame.Database.mock(gameId: UUID(0), index: 0, roll0: nil, roll1: nil, roll2: nil, ball0: nil, ball1: nil, ball2: nil)

		let database = try initializeDatabase(
			withGames: .custom([game]),
			withGameGear: .zero,
			withFrames: .custom([frame])
		)

		let result = try await database.read {
			try game
				.request(for: Game.Database.trackableFrames(filter: .init()))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [frame])
	}

	func testTrackableFrames_FilteredByGear_ReturnsFrames() async throws {
		let game = Game.Database.mock(id: UUID(0), index: 0)

		let ball1 = Gear.Database.mock(id: UUID(0), name: "Red", kind: .bowlingBall)
		let ball2 = Gear.Database.mock(id: UUID(1), name: "Green", kind: .bowlingBall)
		let ball3 = Gear.Database.mock(id: UUID(2), name: "Yellow", kind: .bowlingBall)

		let frame1 = Frame.Database.mock(index: 0, ball0: UUID(0))
		let frame2 = Frame.Database.mock(index: 1, ball1: UUID(1))
		let frame3 = Frame.Database.mock(index: 2, ball0: UUID(2), ball1: UUID(2), ball2: UUID(2))

		let database = try initializeDatabase(
			withGear: .custom([ball1, ball2, ball3]),
			withGames: .custom([game]),
			withGameLanes: .zero,
			withGameGear: .zero,
			withFrames: .custom([frame1, frame2, frame3])
		)

		let result = try await database.read {
			try game
				.request(for: Game.Database.trackableFrames(
					filter: .init(bowlingBallsUsed: [.init(id: UUID(0), name: "Red"), .init(id: UUID(1), name: "Green")])
				))
				.fetchAll($0)
		}

		XCTAssertEqual(result, [frame1, frame2])
	}
}
