import ComposableArchitecture
import Dependencies
import FramesDataProvider
import FramesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class FramesDataProviderTests: XCTestCase {
	func testFetchFrames() async throws {
		let gameId = UUID(uuidString: "00000000-0000-0000-0000-000000000001")!
		let frame1: Frame = .mock(id: gameId, ordinal: 0)
		let frame2: Frame = .mock(id: gameId, ordinal: 1)
		let frame3: Frame = .mock(id: gameId, ordinal: 2)

		try await withDependencies {
			$0.persistenceService.fetchFrames = { _ in
				return [frame1, frame2, frame3]
			}
		} operation: {
			let dataProvider: FramesDataProvider = .liveValue

			let result = try await dataProvider.fetchFrames(.init(filter: nil, ordering: .byOrdinal))

			XCTAssertEqual(result, [frame1, frame2, frame3])
		}
	}
}
