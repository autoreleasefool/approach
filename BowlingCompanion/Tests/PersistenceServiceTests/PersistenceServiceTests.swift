import PersistenceServiceInterface
import RealmSwift
import XCTest
@testable import PersistenceService

final class PersistenceServiceTests: XCTestCase {
	override func setUp() {
		super.setUp()
		Realm.Configuration.defaultConfiguration.inMemoryIdentifier = self.name
	}

	func testWrites() {
		let queue = DispatchQueue(label: "test")
		let persistenceService = PersistenceService.live(queue: queue)

		let mockObject = MockObject()
		mockObject.name = "Test"

		let expectation = self.expectation(description: "written")
		persistenceService.write({
			$0.add(mockObject)
		}, { error in
			XCTAssertNil(error)
			expectation.fulfill()
		})

		wait(for: [expectation], timeout: 1)
	}

	func testReads() {
		let queue = DispatchQueue(label: "test")
		let persistenceService = PersistenceService.live(queue: queue)

		let expectation = self.expectation(description: "read")
		persistenceService.read { _ in
			expectation.fulfill()
		}

		wait(for: [expectation], timeout: 1)
	}
}

class MockObject: Object {
	@Persisted(primaryKey: true) var _id: UUID
	@Persisted var name = ""
}
