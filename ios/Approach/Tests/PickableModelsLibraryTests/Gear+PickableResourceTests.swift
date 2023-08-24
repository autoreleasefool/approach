import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class GearPickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Gear.Summary.pickableModelName(forCount: 0), "Gear")
		XCTAssertEqual(Gear.Summary.pickableModelName(forCount: 1), "Gear")
		XCTAssertEqual(Gear.Summary.pickableModelName(forCount: 2), "Gear")
	}
}
