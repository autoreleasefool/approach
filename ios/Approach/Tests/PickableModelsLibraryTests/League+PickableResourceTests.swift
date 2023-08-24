import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class LeaguePickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(League.Summary.pickableModelName(forCount: 0), "Leagues")
		XCTAssertEqual(League.Summary.pickableModelName(forCount: 1), "League")
		XCTAssertEqual(League.Summary.pickableModelName(forCount: 2), "Leagues")
	}
}
