import PersistenceModelsLibrary
import SharedModelsLibrary
import XCTest

final class PersistentBowlerTests: XCTestCase {
	func testRetainsBowlerProperties() {
		let bowler = Bowler(id: UUID(), name: "name")
		let persisted = PersistentBowler(from: bowler)

		XCTAssertEqual(bowler, persisted.bowler)
	}
}
