import GamesDataProvider
import GamesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class GamesDataProviderTests: XCTestCase {
	func testSaveGame() async throws {
		let expectation = self.expectation(description: "written")
		PersistenceService.testValue.write = { _, onComplete in
			expectation.fulfill()
			onComplete?(nil)
		}

		let dataProvider = GamesDataProvider.liveValue

		try await dataProvider.save(
			Series(id: UUID(), date: Date()),
			Game(id: UUID(), ordinal: 1, locked: .locked, manualScore: nil)
		)

		wait(for: [expectation], timeout: 1)
	}
}
