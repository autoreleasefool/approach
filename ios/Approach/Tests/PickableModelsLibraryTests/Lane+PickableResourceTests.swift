import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class LanePickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Lane.Summary.pickableModelName(forCount: 0), "Lanes")
		XCTAssertEqual(Lane.Summary.pickableModelName(forCount: 1), "Lane")
		XCTAssertEqual(Lane.Summary.pickableModelName(forCount: 2), "Lanes")
	}
}
