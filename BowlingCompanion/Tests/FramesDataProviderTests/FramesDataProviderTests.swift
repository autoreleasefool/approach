import FramesDataProvider
import FramesDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class FramesDataProviderTests: XCTestCase {
	func testFetchFrames() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let firstFrame = Frame(gameId: id0, ordinal: 0, firstBall: nil, secondBall: nil, thirdBall: nil)
		let secondFrame = Frame(gameId: id0, ordinal: 1, firstBall: nil, secondBall: nil, thirdBall: nil)
		let thirdFrame = Frame(gameId: id0, ordinal: 2, firstBall: nil, secondBall: nil, thirdBall: nil)

		let (frames, framesContinuation) = AsyncThrowingStream<[Frame], Error>.streamWithContinuation()

		try await DependencyValues.withValues {
			$0.persistenceService.fetchFrames = { request in
				XCTAssertEqual(request.game, id0)
				return frames
			}
		} operation: {
			let dataProvider: FramesDataProvider = .liveValue

			var iterator = dataProvider.fetchFrames(.init(game: id0, ordering: .byOrdinal)).makeAsyncIterator()

			framesContinuation.yield([firstFrame, secondFrame, thirdFrame])

			let result = try await iterator.next()

			XCTAssertEqual(result, [firstFrame, secondFrame, thirdFrame])
		}
	}
}
