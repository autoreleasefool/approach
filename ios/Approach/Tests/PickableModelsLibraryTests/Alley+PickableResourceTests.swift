import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class AlleyPickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Alley.Summary.pickableModelName(forCount: 0), "Alleys")
		XCTAssertEqual(Alley.Summary.pickableModelName(forCount: 1), "Alley")
		XCTAssertEqual(Alley.Summary.pickableModelName(forCount: 2), "Alleys")
	}
}
