import FramesDataProvider
import FramesDataProviderInterface
import ComposableArchitecture
import Dependencies
import PersistenceServiceInterface
import SharedModelsLibrary
import SharedModelsMocksLibrary
import XCTest

final class FramesDataProviderTests: XCTestCase {
	func testFetchFrames() async throws {
		let id0 = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!

		let frame1: Frame = .mock(id: id0, ordinal: 0)
		let frame2: Frame = .mock(id: id0, ordinal: 1)
		let frame3: Frame = .mock(id: id0, ordinal: 2)

		try await DependencyValues.withValues {
			$0.persistenceService.fetchFrames = { request in
				XCTAssertEqual(request.game, id0)
				return [frame1, frame2, frame3]
			}
		} operation: {
			let dataProvider: FramesDataProvider = .liveValue

			let result = try await dataProvider.fetchFrames(.init(game: id0, ordering: .byOrdinal))

			XCTAssertEqual(result, [frame1, frame2, frame3])
		}
	}
}
