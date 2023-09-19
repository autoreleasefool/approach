import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class BowlerPickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 0), "Bowlers")
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 1), "Bowler")
		XCTAssertEqual(Bowler.Summary.pickableModelName(forCount: 2), "Bowlers")
	}

	func testOpponentModelName() {
		XCTAssertEqual(Bowler.Opponent.pickableModelName(forCount: 0), "Opponents")
		XCTAssertEqual(Bowler.Opponent.pickableModelName(forCount: 1), "Opponent")
		XCTAssertEqual(Bowler.Opponent.pickableModelName(forCount: 2), "Opponents")
	}
}
