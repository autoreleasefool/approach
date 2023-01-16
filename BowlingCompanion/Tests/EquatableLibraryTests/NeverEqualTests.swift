import XCTest
@testable import EquatableLibrary

final class NeverEqualTests: XCTestCase {
	func testFunctionIsNotEqual() {
		let add = { (a: Int, b: Int) in a + b }
		XCTAssertEqual(add(1, 2), 3)

		let first = NeverEqual(wrapped: add)
		let second = NeverEqual(wrapped: add)
		XCTAssertNotEqual(first, second)
	}

	func testIntegerIsNotEqual() {
		let integer = 4

		let first = NeverEqual(wrapped: integer)
		let second = NeverEqual(wrapped: integer)
		XCTAssertEqual(integer, integer)
		XCTAssertNotEqual(first, second)
	}

	func testStringIsNotEqual() {
		let string = "string"

		let first = NeverEqual(wrapped: string)
		let second = NeverEqual(wrapped: string)
		XCTAssertEqual(string, string)
		XCTAssertNotEqual(first, second)
	}

	func testStructIsNotEqual() {
		struct SomeStruct: Equatable {
			let value: String
		}

		let s = SomeStruct(value: "string")

		let first = NeverEqual(wrapped: s)
		let second = NeverEqual(wrapped: s)
		XCTAssertEqual(s, s)
		XCTAssertNotEqual(first, second)
	}
}
