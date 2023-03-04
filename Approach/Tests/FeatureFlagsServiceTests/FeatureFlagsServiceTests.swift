import XCTest
@testable import FeatureFlagsLibrary
@testable import FeatureFlagsService
@testable import FeatureFlagsServiceInterface

final class FeatureFlagsServiceTests: XCTestCase {
	override func tearDown() {
		let featureFlags: FeatureFlagsService = .liveValue
		featureFlags.resetOverrides()
		super.tearDown()
	}

	func testIsFlagEnabledWhenEnabled() {
		let featureFlags: FeatureFlagsService = .liveValue

		let enabledFlag = FeatureFlag(name: "Test", introduced: "", stage: .release)
		XCTAssertTrue(featureFlags.isEnabled(enabledFlag))
	}

	func testIsFlagEnabledWhenDisabled() {
		let featureFlags: FeatureFlagsService = .liveValue

		let disabledFlag = FeatureFlag(name: "Test", introduced: "", stage: .disabled)
		XCTAssertFalse(featureFlags.isEnabled(disabledFlag))
	}

	func testAllEnabledWhenAllEnabled() {
		let featureFlags: FeatureFlagsService = .liveValue

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .release),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		XCTAssertTrue(featureFlags.allEnabled(flags))
	}

	func testAllEnabledWhenSomeEnabled() {
		let featureFlags: FeatureFlagsService = .liveValue

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .disabled),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		XCTAssertFalse(featureFlags.allEnabled(flags))
	}

	func testAllEnabledWhenNoneEnabled() {
		let featureFlags: FeatureFlagsService = .liveValue

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .disabled),
			.init(name: "Test2", introduced: "", stage: .disabled),
			.init(name: "Test3", introduced: "", stage: .disabled),
		]

		XCTAssertFalse(featureFlags.allEnabled(flags))
	}

	func testObserveFlagReceivesChanges() async {
		let featureFlags: FeatureFlagsService = .liveValue

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		var observations = featureFlags.observe(flag).makeAsyncIterator()

		let firstObservation = await observations.next()
		XCTAssertTrue(firstObservation == true)

		featureFlags.setEnabled(flag, false)

		let secondObservation = await observations.next()
		XCTAssertTrue(secondObservation == false)
	}

	func testObserveFlagDoesNotReceiveUnrelatedChanges() async {
		let featureFlags: FeatureFlagsService = .liveValue

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
		let featureFlags: FeatureFlagsService = .liveValue

		let flags: [FeatureFlag] = [
			.init(name: "Test1", introduced: "", stage: .release),
			.init(name: "Test2", introduced: "", stage: .release),
			.init(name: "Test3", introduced: "", stage: .release),
		]

		var observations = featureFlags.observeAll(flags).makeAsyncIterator()

		let firstObservation = await observations.next()
		XCTAssertEqual(firstObservation, [true, true, true])

		featureFlags.setEnabled(flags[0], false)
		featureFlags.setEnabled(flags[2], false)

		let secondObservation = await observations.next()
		XCTAssertEqual(secondObservation, [false, true, true])

		let thirdObservation = await observations.next()
		XCTAssertEqual(thirdObservation, [false, true, false])
	}

	func testOverridingFlagPublishesNotification() {
		let featureFlags: FeatureFlagsService = .liveValue

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
		let featureFlags: FeatureFlagsService = .liveValue

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
		let featureFlags: FeatureFlagsService = .liveValue

		let flag = FeatureFlag(name: "Test", introduced: "", stage: .release)

		XCTAssertTrue(featureFlags.isEnabled(flag))
		featureFlags.setEnabled(flag, false)
		XCTAssertFalse(featureFlags.isEnabled(flag))
		featureFlags.resetOverrides()
		XCTAssertTrue(featureFlags.isEnabled(flag))
	}
}
