import SeriesDataProvider
import SeriesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary
import XCTest

final class SeriesDataProviderTests: XCTestCase {
	func testSaveSeries() async throws {
		let expectation = self.expectation(description: "written")
		PersistenceService.testValue.write = { _, onComplete in
			expectation.fulfill()
			onComplete?(nil)
		}

		let dataProvider = SeriesDataProvider.liveValue

		try await dataProvider.create(
			League(id: UUID(), name: "League", recurrence: .oneTimeEvent, numberOfGames: 3, additionalPinfall: 0, additionalGames: 0),
			Series(id: UUID(), date: Date())
		)

		wait(for: [expectation], timeout: 1)
	}
}
