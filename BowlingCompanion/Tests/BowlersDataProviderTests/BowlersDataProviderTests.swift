import BowlersDataProvider
import BowlersDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class BowlersDataProviderTests: XCTestCase {
	func testSaveBowler() async throws {
		let expectation = self.expectation(description: "written")
		PersistenceService.testValue.write = { _, onComplete in
			expectation.fulfill()
			onComplete?(nil)
		}

		let dataProvider = BowlersDataProvider.liveValue

		try await dataProvider.save(Bowler(id: UUID(), name: "Bowler"))

		wait(for: [expectation], timeout: 1)
	}
}
