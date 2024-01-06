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

	func testPercentFormat_WithNumerator_WithoutDenominator() {
		XCTAssertEqual(format(percentage: nil, withNumerator: 1), "—")
		XCTAssertEqual(format(percentage: 0.86, withNumerator: 1), "86%")
		XCTAssertEqual(format(percentage: 0.38432, withNumerator: 1), "38.4%")
		XCTAssertEqual(format(percentage: 0.99999, withNumerator: 1), "100%")
	}

	func testPercentFormat_WithoutNumerator_WithDenominator() {
		XCTAssertEqual(format(percentage: nil, withDenominator: 1), "—")
		XCTAssertEqual(format(percentage: 0.86, withDenominator: 1), "86%")
		XCTAssertEqual(format(percentage: 0.38432, withDenominator: 1), "38.4%")
		XCTAssertEqual(format(percentage: 0.99999, withDenominator: 1), "100%")
	}

	func testPercentFormat_WithNumerator_WithDenominator() {
		XCTAssertEqual(format(percentage: nil, withNumerator: 1, withDenominator: 2), "—")
		XCTAssertEqual(format(percentage: 0.86, withNumerator: 1, withDenominator: 2), "86% (1/2)")
		XCTAssertEqual(format(percentage: 0.38432, withNumerator: 1, withDenominator: 2), "38.4% (1/2)")
		XCTAssertEqual(format(percentage: 0.99999, withNumerator: 1, withDenominator: 2), "100% (1/2)")
	}
}
