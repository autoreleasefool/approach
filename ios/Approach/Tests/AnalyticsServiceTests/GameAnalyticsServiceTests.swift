@testable import AnalyticsService
@testable import AnalyticsServiceInterface
import Dependencies
import XCTest

@MainActor
final class GameAnalyticsServiceTests: XCTestCase {
	@Dependency(\.gameAnalytics) var gameAnalytics

	// MARK: - Record Game Viewed

	func testRecordGameEvent_RecordsEvent() async {
		let eventRecorded = self.expectation(description: "event recorded")

		await withDependencies {
			$0.analytics.trackEvent = { _ in eventRecorded.fulfill() }
			$0.gameAnalytics = .live()
		} operation: {
			await gameAnalytics.recordGameEvent(.viewed, "id")
		}

		await fulfillment(of: [eventRecorded])
	}

	func testRecordGameEvent_WithEventRecorded_DoesNotRecordMultipleTimes() async {
		let eventRecorded = self.expectation(description: "event recorded")
		eventRecorded.assertForOverFulfill = true

		await withDependencies {
			$0.analytics.trackEvent = { _ in eventRecorded.fulfill() }
			$0.gameAnalytics = .live()
		} operation: {
			await gameAnalytics.recordGameEvent(.viewed, "id")
			await gameAnalytics.recordGameEvent(.viewed, "id")
			await gameAnalytics.recordGameEvent(.viewed, "id")
		}

		await fulfillment(of: [eventRecorded])
	}

	func testRecordGameEvent_WithEventRecorded_RecordsOtherEvents() async {
		let eventRecorded = self.expectation(description: "event recorded")
		eventRecorded.assertForOverFulfill = true
		eventRecorded.expectedFulfillmentCount = 3

		await withDependencies {
			$0.analytics.trackEvent = { _ in eventRecorded.fulfill() }
			$0.gameAnalytics = .live()
		} operation: {
			await gameAnalytics.recordGameEvent(.viewed, "id")
			await gameAnalytics.recordGameEvent(.manualScoreSet, "id")
			await gameAnalytics.recordGameEvent(.updated, "id")
		}

		await fulfillment(of: [eventRecorded])
	}

	func testResetSession_RecordsEventsAgain() async {
		let eventRecorded = self.expectation(description: "event recorded")
		eventRecorded.assertForOverFulfill = true
		eventRecorded.expectedFulfillmentCount = 2

		await withDependencies {
			$0.analytics.trackEvent = { _ in eventRecorded.fulfill() }
			$0.gameAnalytics = .live()
		} operation: {
			await gameAnalytics.recordGameEvent(.viewed, "id")
			await gameAnalytics.resetSession()
			await gameAnalytics.recordGameEvent(.viewed, "id")
		}

		await fulfillment(of: [eventRecorded])
	}
}
