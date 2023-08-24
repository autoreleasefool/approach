import ModelsLibrary
@testable import PickableModelsLibrary
import XCTest

final class SeriesPickableResourceTests: XCTestCase {
	func testModelName() {
		XCTAssertEqual(Series.Summary.pickableModelName(forCount: 0), "Series")
		XCTAssertEqual(Series.Summary.pickableModelName(forCount: 1), "Series")
		XCTAssertEqual(Series.Summary.pickableModelName(forCount: 2), "Series")
	}
}
