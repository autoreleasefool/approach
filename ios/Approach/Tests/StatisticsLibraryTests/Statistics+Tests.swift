@testable import StatisticsLibrary
import XCTest

final class StatisticsTests: XCTestCase {
	func testType_FindsTypeByID() {
		let id = "High Single"

		let type = Statistics.type(of: id)

		XCTAssertTrue(type is Statistics.HighSingle.Type)
		XCTAssertFalse(type is Statistics.HighSeriesOf3.Type)
	}

	func testType_WithInvalidID_ReturnsNil() {
		let id = "invalid"

		let type = Statistics.type(of: id)

		XCTAssertNil(type)
	}
}
