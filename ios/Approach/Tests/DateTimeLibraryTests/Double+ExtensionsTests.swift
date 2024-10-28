import DateTimeLibrary
import XCTest

final class DoubleExtensionTests: XCTestCase {
	func testDurationFormat() {
		XCTAssertEqual("292:03", 1_051_380.durationFormat)
		XCTAssertEqual("23:57", 86_220.durationFormat)
		XCTAssertEqual("0:24", 1_440.durationFormat)
		XCTAssertEqual("0:06", 360.durationFormat)
	}

	func testDurationFormat_IgnoresSeconds() {
		XCTAssertEqual("292:03", 1_051_401.durationFormat) /* 1051401 == 292:03:21 */
		XCTAssertEqual("23:57", 86_235.durationFormat) /* 86235 == 23:57:15 */
		XCTAssertEqual("0:06", 362.durationFormat) /* 362 = 0:06:02 */
	}
}
