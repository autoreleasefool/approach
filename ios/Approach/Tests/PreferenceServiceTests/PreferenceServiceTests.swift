import Dependencies
@testable import PreferenceService
import PreferenceServiceInterface
import XCTest

final class PreferenceServiceTests: XCTestCase {
	@Dependency(\.preferences) var preferences

	func testRemovesKey() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setString = liveValue.setString
			$0.preferences.getString = liveValue.getString
		} operation: {
			let key = "key"

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getString(key))

			preferences.setString(key, "value")

			XCTAssertTrue(preferences.contains(key))
			XCTAssertNotNil(preferences.getString(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getString(key))
		}
	}

	func testStoreAndRetrievesBool() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setBool = liveValue.setBool
			$0.preferences.getBool = liveValue.getBool
		} operation: {
			let key = "bool"

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getBool(key))

			preferences.setBool(key, true)

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual(true, preferences.getBool(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getBool(key))
		}
	}

	func testStoreAndRetrievesInt() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setInt = liveValue.setInt
			$0.preferences.getInt = liveValue.getInt
		} operation: {
			let key = "int"
			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getInt(key))

			preferences.setInt(key, 101)

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual(101, preferences.getInt(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getInt(key))
		}
	}

	func testStoreAndRetrievesFloat() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setFloat = liveValue.setFloat
			$0.preferences.getFloat = liveValue.getFloat
		} operation: {
			let key = "float"
			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getFloat(key))

			preferences.setFloat(key, 101.2)

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual(101.2, preferences.getFloat(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getFloat(key))
		}
	}

	func testStoreAndRetrievesDouble() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setDouble = liveValue.setDouble
			$0.preferences.getDouble = liveValue.getDouble
		} operation: {
			let key = "double"
			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getDouble(key))

			preferences.setDouble(key, 101.2)

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual(101.2, preferences.getDouble(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getDouble(key))
		}
	}

	func testStoreAndRetrievesString() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setString = liveValue.setString
			$0.preferences.getString = liveValue.getString
		} operation: {
			let key = "string"
			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getString(key))

			preferences.setString(key, "value")

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual("value", preferences.getString(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getString(key))
		}
	}

	func testStoreAndRetrievesStringArray() {
		withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setStringArray = liveValue.setStringArray
			$0.preferences.getStringArray = liveValue.getStringArray
		} operation: {
			let key = "stringArray"
			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getStringArray(key))

			preferences.setStringArray(key, ["value1", "value2"])

			XCTAssertTrue(preferences.contains(key))
			XCTAssertEqual(["value1", "value2"], preferences.getStringArray(key))

			preferences.remove(key)

			XCTAssertFalse(preferences.contains(key))
			XCTAssertNil(preferences.getStringArray(key))
		}
	}

	func testSubscribe_ReceivesChanges() async {
		await withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setString = liveValue.setString
			$0.preferences.setBool = liveValue.setBool
			$0.preferences.observe = liveValue.observe
		} operation: {
			var observations = preferences.observe(
				keys: [.appDidCompleteOnboarding, .statisticsCountH2AsH]
			).makeAsyncIterator()

			preferences.setKey(.appDidCompleteOnboarding, toBool: true)
			preferences.setKey(.statisticsCountH2AsH, toString: "test")
			preferences.remove(PreferenceKey.appDidCompleteOnboarding.rawValue)

			let firstObservation = await observations.next()
			XCTAssertEqual(firstObservation, .appDidCompleteOnboarding)

			let secondObservation = await observations.next()
			XCTAssertEqual(secondObservation, .statisticsCountH2AsH)

			let thirdObservation = await observations.next()
			XCTAssertEqual(thirdObservation, .appDidCompleteOnboarding)
		}
	}

	func testSubscribe_DoesNotReceiveUnrelatedChanges() async {
		await withDependencies {
			let liveValue = PreferenceService.liveValue
			$0.preferences.contains = liveValue.contains
			$0.preferences.remove = liveValue.remove
			$0.preferences.setBool = liveValue.setBool
			$0.preferences.observe = liveValue.observe
		} operation: {
			var observations = preferences.observe(
				keys: [.appDidCompleteOnboarding]
			).makeAsyncIterator()

			preferences.setKey(.appDidCompleteOnboarding, toBool: true)
			preferences.setKey(.statisticsCountH2AsH, toBool: false)
			preferences.remove(PreferenceKey.appDidCompleteOnboarding.rawValue)

			let firstObservation = await observations.next()
			XCTAssertEqual(firstObservation, .appDidCompleteOnboarding)

			let secondObservation = await observations.next()
			XCTAssertEqual(secondObservation, .appDidCompleteOnboarding)
		}
	}
}
