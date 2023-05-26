@testable import PreferenceService
import PreferenceServiceInterface
import XCTest

final class PreferenceServiceTests: XCTestCase {

	func testRemovesKey() {
		let key = "key"
		let preferenceService: PreferenceService = .liveValue

		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getString(key))

		preferenceService.setString(key, "value")

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertNotNil(preferenceService.getString(key))

		preferenceService.remove(key)

		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getString(key))
	}

	func testStoreAndRetrievesBool() {
		let key = "bool"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getBool(key))

		preferenceService.setBool(key, true)

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual(true, preferenceService.getBool(key))

		preferenceService.remove(key)
	}

	func testStoreAndRetrievesInt() {
		let key = "int"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getInt(key))

		preferenceService.setInt(key, 101)

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual(101, preferenceService.getInt(key))

		preferenceService.remove(key)
	}

	func testStoreAndRetrievesFloat() {
		let key = "float"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getFloat(key))

		preferenceService.setFloat(key, 101.2)

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual(101.2, preferenceService.getFloat(key))

		preferenceService.remove(key)
	}

	func testStoreAndRetrievesDouble() {
		let key = "double"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getDouble(key))

		preferenceService.setDouble(key, 101.2)

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual(101.2, preferenceService.getDouble(key))

		preferenceService.remove(key)
	}

	func testStoreAndRetrievesString() {
		let key = "string"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getString(key))

		preferenceService.setString(key, "test value")

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual("test value", preferenceService.getString(key))

		preferenceService.remove(key)
	}

	func testStoreAndRetrievesStringArray() {
		let key = "stringArray"
		let preferenceService: PreferenceService = .liveValue
		XCTAssertFalse(preferenceService.contains(key))
		XCTAssertNil(preferenceService.getStringArray(key))

		preferenceService.setStringArray(key, ["test value", "another test"])

		XCTAssertTrue(preferenceService.contains(key))
		XCTAssertEqual(["test value", "another test"], preferenceService.getStringArray(key))

		preferenceService.remove(key)
	}
}
