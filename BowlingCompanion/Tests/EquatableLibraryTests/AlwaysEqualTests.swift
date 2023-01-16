import XCTest
@testable import EquatableLibrary

final class AlwaysEqualTests: XCTestCase {
	func testFunctionIsEqual() {
		let add = { (a: Int, b: Int) in a + b }
		XCTAssertEqual(add(1, 2), 3)

		let first = AlwaysEqual(wrapped: add)
		let second = AlwaysEqual(wrapped: add)
		XCTAssertEqual(first, second)
	}

	func testIntegerIsEqual() {
		let firstInteger = 4
		let secondInteger = 5

		let first = AlwaysEqual(wrapped: firstInteger)
		let second = AlwaysEqual(wrapped: secondInteger)
		XCTAssertNotEqual(firstInteger, secondInteger)
		XCTAssertEqual(first, second)
	}

	func testStringIsEqual() {
		let firstString = "something"
		let secondString = "nothing"

		let first = AlwaysEqual(wrapped: firstString)
		let second = AlwaysEqual(wrapped: secondString)
		XCTAssertNotEqual(firstString, secondString)
		XCTAssertEqual(first, second)
	}

	func testStructIsEqual() {
		struct SomeStruct: Equatable {
			let value: String
		}

		let firstStruct = SomeStruct(value: "something")
		let secondStruct = SomeStruct(value: "else")

		let first = AlwaysEqual(wrapped: firstStruct)
		let second = AlwaysEqual(wrapped: secondStruct)
		XCTAssertNotEqual(firstStruct, secondStruct)
		XCTAssertEqual(first, second)
	}
}
