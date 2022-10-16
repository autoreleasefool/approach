import DateTimeLibrary
import XCTest

final class DateExtensionTests: XCTestCase {
	func testRegularDateFormat() {
		let date = Date(timeIntervalSince1970: 0)
		XCTAssertEqual(date.regularDateFormat, "December 31, 1969")
	}
}
