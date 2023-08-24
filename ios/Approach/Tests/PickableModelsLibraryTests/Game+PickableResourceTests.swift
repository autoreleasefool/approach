import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class GamePickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Game.Summary.pickableModelName(forCount: 0), "Games")
		XCTAssertEqual(Game.Summary.pickableModelName(forCount: 1), "Game")
		XCTAssertEqual(Game.Summary.pickableModelName(forCount: 2), "Games")
	}
}
