@testable import StringsLibrary
import XCTest

final class NumberFormatterTests: XCTestCase {
	func testAverageFormat() {
		XCTAssertEqual(format(average: nil), "—")
		XCTAssertEqual(format(average: 123.0), "123")
		XCTAssertEqual(format(average: 99.9999), "100")
		XCTAssertEqual(format(average: 206.52342), "206.5")
	}

	func testPercentFormat() {
		XCTAssertEqual(format(percentage: nil), "—")
		XCTAssertEqual(format(percentage: 0.86), "86%")
		XCTAssertEqual(format(percentage: 0.38432), "38.4%")
		XCTAssertEqual(format(percentage: 0.99999), "100%")
	}
}
