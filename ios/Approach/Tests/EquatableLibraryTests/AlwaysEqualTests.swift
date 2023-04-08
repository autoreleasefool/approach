@testable import EquatableLibrary
import XCTest

final class AlwaysEqualTests: XCTestCase {
	func testFunctionIsEqual() {
		let add = { (a: Int, b: Int) in a + b }
		XCTAssertEqual(add(1, 2), 3)

		let first = AlwaysEqual(add)
		let second = AlwaysEqual(add)
		XCTAssertEqual(first, second)
	}

	func testIntegerIsEqual() {
		let firstInteger = 4
		let secondInteger = 5

		let first = AlwaysEqual(firstInteger)
		let second = AlwaysEqual(secondInteger)
		XCTAssertNotEqual(firstInteger, secondInteger)
		XCTAssertEqual(first, second)
	}

	func testStringIsEqual() {
		let firstString = "something"
		let secondString = "nothing"

		let first = AlwaysEqual(firstString)
		let second = AlwaysEqual(secondString)
		XCTAssertNotEqual(firstString, secondString)
		XCTAssertEqual(first, second)
	}

	func testStructIsEqual() {
		struct SomeStruct: Equatable {
			let value: String
		}

		let firstStruct = SomeStruct(value: "something")
		let secondStruct = SomeStruct(value: "else")

		let first = AlwaysEqual(firstStruct)
		let second = AlwaysEqual(secondStruct)
		XCTAssertNotEqual(firstStruct, secondStruct)
		XCTAssertEqual(first, second)
	}
}
