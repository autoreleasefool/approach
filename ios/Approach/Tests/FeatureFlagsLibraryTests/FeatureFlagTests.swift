@testable import FeatureFlagsLibrary
import XCTest

final class FeatureFlagTests: XCTestCase {
	func testDisabledRolloutStage() {
		let lesserThan: [FeatureFlag.RolloutStage] = []
		let greaterThan: [FeatureFlag.RolloutStage] = [.development, .release, .test]

		for stage in FeatureFlag.RolloutStage.allCases {
			if lesserThan.contains(stage) {
				XCTAssertTrue(stage < .disabled)
			} else if greaterThan.contains(stage) {
				XCTAssertTrue(stage > .disabled)
			} else {
				XCTAssertEqual(stage, .disabled)
			}
		}
	}

	func testDevelopmentRolloutStage() {
		let lesserThan: [FeatureFlag.RolloutStage] = [.disabled]
		let greaterThan: [FeatureFlag.RolloutStage] = [.release, .test]

		for stage in FeatureFlag.RolloutStage.allCases {
			if lesserThan.contains(stage) {
				XCTAssertTrue(stage < .development)
			} else if greaterThan.contains(stage) {
				XCTAssertTrue(stage > .development)
			} else {
				XCTAssertEqual(stage, .development)
			}
		}
	}

	func testTestRolloutStage() {
		let lesserThan: [FeatureFlag.RolloutStage] = [.disabled, .development]
		let greaterThan: [FeatureFlag.RolloutStage] = [.release]

		for stage in FeatureFlag.RolloutStage.allCases {
			if lesserThan.contains(stage) {
				XCTAssertTrue(stage < .test)
			} else if greaterThan.contains(stage) {
				XCTAssertTrue(stage > .test)
			} else {
				XCTAssertEqual(stage, .test)
			}
		}
	}

	func testReleaseRolloutStage() {
		let lesserThan: [FeatureFlag.RolloutStage] = [.disabled, .development, .test]
		let greaterThan: [FeatureFlag.RolloutStage] = []

		for stage in FeatureFlag.RolloutStage.allCases {
			if lesserThan.contains(stage) {
				XCTAssertTrue(stage < .release)
			} else if greaterThan.contains(stage) {
				XCTAssertTrue(stage > .release)
			} else {
				XCTAssertEqual(stage, .release)
			}
		}
	}
}
