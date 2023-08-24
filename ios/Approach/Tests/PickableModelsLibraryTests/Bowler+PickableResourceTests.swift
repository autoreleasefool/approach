import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class BowlerPickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 0), "Bowlers")
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 1), "Bowler")
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 2), "Bowlers")
	}
}
