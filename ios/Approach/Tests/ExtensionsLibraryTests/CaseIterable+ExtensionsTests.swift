import ExtensionsLibrary
import XCTest

enum TestEnum: CaseIterable {
	case first
	case second
	case third
}

final class CaseIterableExtensionsTests: XCTestCase {
	func testNext_ReturnsNext() {
		let first = TestEnum.first
		XCTAssertEqual(first.next, .second)
	}

	func testNext_WhenLastElement_ReturnsFirstElement() {
		let last = TestEnum.third
		XCTAssertEqual(last.next, .first)
	}

	func testToNext_SetsNextElement() {
		var first = TestEnum.first
		first.toNext()
		XCTAssertEqual(first, .second)
	}
}
