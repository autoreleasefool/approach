import Dependencies
@testable import FeatureFlagsLibrary
@testable import FeatureFlagsService
@testable import FeatureFlagsServiceInterface
import PreferenceServiceInterface
import XCTest

final class FeatureFlagsServiceTests: XCTestCase {
	let testQueue = DispatchQueue(label: "TestQueue")

	override func invokeTest() {
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
			$0[PreferenceService.self].remove = { @Sendable _ in }
			$0.featureFlagsQueue = testQueue
		} operation: {
			super.invokeTest()
		}
	}

	override func tearDown() {
		testQueue.sync { }
	}

	func testIsFlagEnabledWhenEnabled() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
		} operation: {
			featureFlags = .liveValue
		}

		let enabledFlag = FeatureFlag(name: "Test", introduced: "", stage: .release)
		XCTAssertTrue(featureFlags.isEnabled(enabledFlag))
	}

	func testIsFlagEnabledWhenDisabled() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
		} operation: {
			featureFlags = .liveValue
		}

		let disabledFlag = FeatureFlag(name: "Test", introduced: "", stage: .disabled)
		XCTAssertFalse(featureFlags.isEnabled(disabledFlag))
	}

	func testAllEnabledWhenAllEnabled() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
		} operation: {
			featureFlags = .liveValue
		}

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .release),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		XCTAssertTrue(featureFlags.allEnabled(flags))
	}

	func testAllEnabledWhenSomeEnabled() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
		} operation: {
			featureFlags = .liveValue
		}

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .disabled),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		XCTAssertFalse(featureFlags.allEnabled(flags))
	}

	func testAllEnabledWhenNoneEnabled() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
		} operation: {
			featureFlags = .liveValue
		}

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .disabled),
			.init(name: "Test2", introduced: "", stage: .disabled),
			.init(name: "Test3", introduced: "", stage: .disabled),
		]

		XCTAssertFalse(featureFlags.allEnabled(flags))
	}

	func testObserveFlagReceivesChanges() async {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		var observations = featureFlags.observe(flag).makeAsyncIterator()

		let firstObservation = await observations.next()
		XCTAssertTrue(firstObservation == true)

		featureFlags.setEnabled(flag, false)

		let secondObservation = await observations.next()
		XCTAssertTrue(secondObservation == false)
	}

	func testObserveFlagDoesNotReceiveUnrelatedChanges() async {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)
		let flag2 = FeatureFlag(name: "Test2", introduced: "", stage: .release)

		var observations = featureFlags.observe(flag).makeAsyncIterator()

		let firstObservation = await observations.next()
		XCTAssertTrue(firstObservation == true)

		featureFlags.setEnabled(flag2, false)
		featureFlags.setEnabled(flag, true)

		let secondObservation = await observations.next()
		XCTAssertTrue(secondObservation == true)
	}

	func testObserveAllFlagsReceivesAllChanges() async {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .release),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		var observations = featureFlags.observeAll(flags).makeAsyncIterator()

		let firstObservation = await observations.next()
		XCTAssertEqual(firstObservation, [flags[0]: true, flags[1]: true, flags[2]: true])

		featureFlags.setEnabled(flags[0], false)
		featureFlags.setEnabled(flags[2], false)

		let secondObservation = await observations.next()
		XCTAssertEqual(secondObservation, [flags[0]: false, flags[1]: true, flags[2]: true])

		let thirdObservation = await observations.next()
		XCTAssertEqual(thirdObservation, [flags[0]: false, flags[1]: true, flags[2]: false])
	}

	func testOverridingFlagPublishesNotification() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		let expectation = self.expectation(description: "notification published")
		let cancellable = NotificationCenter.default
			.publisher(for: .FeatureFlag.didChange)
			.sink { notification in
				XCTAssertEqual(notification.object as? FeatureFlag, flag)
				expectation.fulfill()
			}

		featureFlags.setEnabled(flag, true)

		waitForExpectations(timeout: 1)
		cancellable.cancel()
	}

	func testSetOverride() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		XCTAssertTrue(featureFlags.isEnabled(flag))
		featureFlags.setEnabled(flag, false)
		XCTAssertFalse(featureFlags.isEnabled(flag))
		featureFlags.setEnabled(flag, true)
		XCTAssertTrue(featureFlags.isEnabled(flag))
		featureFlags.setEnabled(flag, nil)
		XCTAssertTrue(featureFlags.isEnabled(flag))
	}

	func testResetOverrides() {
		var featureFlags: FeatureFlagsService!
		withDependencies {
			$0[PreferenceService.self].getBool = { @Sendable _ in false }
			$0[PreferenceService.self].setBool = { @Sendable _, _ in }
		} operation: {
			featureFlags = .liveValue
		}

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		XCTAssertTrue(featureFlags.isEnabled(flag))
		featureFlags.setEnabled(flag, false)
		XCTAssertFalse(featureFlags.isEnabled(flag))
		featureFlags.resetOverrides()
		XCTAssertTrue(featureFlags.isEnabled(flag))
	}
}
