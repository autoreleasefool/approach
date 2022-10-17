import LeaguesDataProvider
import LeaguesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class LeaguesDataProviderTests: XCTestCase {
	func testSaveLeague() async throws {
		let expectation = self.expectation(description: "written")
		PersistenceService.testValue.write = { _, onComplete in
			expectation.fulfill()
			onComplete?(nil)
		}

		let dataProvider = LeaguesDataProvider.liveValue

		try await dataProvider.create(
			Bowler(id: UUID(), name: "Bowler"),
			League(id: UUID(), name: "League", recurrence: .oneTimeEvent, numberOfGames: 3, additionalPinfall: 0, additionalGames: 0)
		)

		wait(for: [expectation], timeout: 1)
	}
}
