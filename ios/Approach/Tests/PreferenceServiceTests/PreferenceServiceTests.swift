import Dependencies
@testable import PreferenceService
import PreferenceServiceInterface
import UserDefaultsPackageServiceInterface
import XCTest

final class PreferenceServiceTests: XCTestCase {
	@Dependency(\.preferences) var preferences

	func test_remove() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: String]>(["appDidCompleteOnboarding": "true"])

		withDependencies {
			$0.userDefaults.remove = { @Sendable key in cache.withValue { $0[key] = nil } }
			$0.preferences = liveValue
		} operation: {
			preferences.remove(key: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(cache.value, [:])
	}

	func test_setBool_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Bool]>([:])

		withDependencies {
			$0.userDefaults.setBool = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setBool(forKey: .appDidCompleteOnboarding, to: true)
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": true])
	}

	func test_getBool_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Bool]>(["appDidCompleteOnboarding": true])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.bool = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.bool(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertNotNil(appDidCompleteOnboarding)
		XCTAssertTrue(appDidCompleteOnboarding!)

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.bool = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.bool(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}

	func test_setInt_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Int]>([:])

		withDependencies {
			$0.userDefaults.setInt = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setInt(forKey: .appDidCompleteOnboarding, to: 123)
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": 123])
	}

	func test_getInt_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Int]>(["appDidCompleteOnboarding": 123])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.int = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.int(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(appDidCompleteOnboarding, 123)

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.int = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.int(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}

	func test_setFloat_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Float]>([:])

		withDependencies {
			$0.userDefaults.setFloat = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setFloat(forKey: .appDidCompleteOnboarding, to: 12.3)
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": 12.3])
	}

	func test_getFloat_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Float]>(["appDidCompleteOnboarding": 12.3])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.float = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.float(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(appDidCompleteOnboarding, 12.3)

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.float = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.float(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}

	func test_setDouble_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Double]>([:])

		withDependencies {
			$0.userDefaults.setDouble = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setDouble(forKey: .appDidCompleteOnboarding, to: 12.3)
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": 12.3])
	}

	func test_getDouble_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: Double]>(["appDidCompleteOnboarding": 12.3])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.double = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.double(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(appDidCompleteOnboarding, 12.3)

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.double = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.double(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}

	func test_setString_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: String]>([:])

		withDependencies {
			$0.userDefaults.setString = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setString(forKey: .appDidCompleteOnboarding, to: "some string")
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": "some string"])
	}

	func test_getString_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: String]>(["appDidCompleteOnboarding": "some string"])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.string = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.string(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(appDidCompleteOnboarding, "some string")

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.string = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.string(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}

	func test_setStringArray_setsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: [String]]>([:])

		withDependencies {
			$0.userDefaults.setStringArray = { @Sendable key, value in cache.withValue { $0[key] = value } }
			$0.preferences = liveValue
		} operation: {
			preferences.setStringArray(forKey: .appDidCompleteOnboarding, to: ["some string", "another string"])
		}

		XCTAssertEqual(cache.value, ["appDidCompleteOnboarding": ["some string", "another string"]])
	}

	func test_getStringArray_getsValue() {
		let liveValue: PreferenceService = .liveValue
		let cache = LockIsolated<[String: [String]]>(["appDidCompleteOnboarding": ["some string", "another string"]])

		let appDidCompleteOnboarding = withDependencies {
			$0.userDefaults.stringArray = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.stringArray(forKey: .appDidCompleteOnboarding)
		}

		XCTAssertEqual(appDidCompleteOnboarding, ["some string", "another string"])

		let gameShouldNotifyEditorChanges = withDependencies {
			$0.userDefaults.stringArray = { @Sendable key in cache.value[key] }
			$0.preferences = liveValue
		} operation: {
			preferences.stringArray(forKey: .gameShouldNotifyEditorChanges)
		}

		XCTAssertNil(gameShouldNotifyEditorChanges)
	}
}
