import DateTimeLibrary
import XCTest

final class DateExtensionTests: XCTestCase {
	func testLongDateFormat() {
		let date = Date(timeIntervalSince1970: 72_072)
		XCTAssertEqual(date.longFormat, "January 1, 1970")
	}

	func testMediumDateFormat() {
		let date = Date(timeIntervalSince1970: 72_072)
		XCTAssertEqual(date.mediumFormat, "Thu, Jan 1")
	}

	func testShortDateFormat() {
		let date = Date(timeIntervalSince1970: 72_072)
		XCTAssertEqual(date.shortFormat, "Jan 1")
	}
}
