@testable import EquatableLibrary
import XCTest

final class NeverEqualTests: XCTestCase {
	func testFunctionIsNotEqual() {
		let add = { (a: Int, b: Int) in a + b }
		XCTAssertEqual(add(1, 2), 3)

		let first = NeverEqual(add)
		let second = NeverEqual(add)
		XCTAssertNotEqual(first, second)
	}

	func testIntegerIsNotEqual() {
		let integer = 4

		let first = NeverEqual(integer)
		let second = NeverEqual(integer)
		XCTAssertEqual(integer, integer)
		XCTAssertNotEqual(first, second)
	}

	func testStringIsNotEqual() {
		let string = "string"

		let first = NeverEqual(string)
		let second = NeverEqual(string)
		XCTAssertEqual(string, string)
		XCTAssertNotEqual(first, second)
	}

	func testStructIsNotEqual() {
		struct SomeStruct: Equatable {
			let value: String
		}

		let s = SomeStruct(value: "string")

		let first = NeverEqual(s)
		let second = NeverEqual(s)
		XCTAssertEqual(s, s)
		XCTAssertNotEqual(first, second)
	}
}
