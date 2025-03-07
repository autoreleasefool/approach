import DateTimeLibrary
import Foundation
import Testing

@Suite("Date Extensions")
struct DateExtensionTests {
	@Test(
		"longFormat formats correctly",
		arguments: zip(
			[72_072, 1_741_323_817],
			["January 1, 1970", "March 6, 2025"]
		)
	)
	func longFormat(timeIntervalSince1970: TimeInterval, expectedLongFormat: String) {
		let date = Date(timeIntervalSince1970: timeIntervalSince1970)
		#expect(date.longFormat == expectedLongFormat)
	}

	@Test(
		"mediumFormat formats correctly",
		arguments: zip(
			[72_072, 1_741_323_817],
			["Thu, Jan 1", "Thu, Mar 6"]
		)
	)
	func mediumFormat(timeIntervalSince1970: TimeInterval, expectedMediumFormat: String) {
		let date = Date(timeIntervalSince1970: timeIntervalSince1970)
		#expect(date.mediumFormat == expectedMediumFormat)
	}

	@Test(
		"shortFormat formats correctly",
		arguments: zip(
			[72_072, 1_741_323_817],
			["Jan 1", "Mar 6"]
		)
	)
	func mediumFormat(timeIntervalSince1970: TimeInterval, expectedShortFormat: String) {
		let date = Date(timeIntervalSince1970: timeIntervalSince1970)
		#expect(date.shortFormat == expectedShortFormat)
	}
}
