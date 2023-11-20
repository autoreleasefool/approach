import AppInfoServiceInterface
import Dependencies
@testable import StoreReviewService
@testable import StoreReviewServiceInterface
import XCTest

final class StoreReviewServiceTests: XCTestCase {
	@Dependency(\.storeReview) var storeReview

	func testShouldRequestReview_WithoutSessions_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0.appInfo.numberOfSessions = { 1 }

			// Nov 12, 2023
			$0.appInfo.installDate = { Date(timeIntervalSince1970: 1699770852) }

			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0.storeReview.shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithoutInstallDate_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0.appInfo.numberOfSessions = { 5 }

			// Nov 19, 2023
			$0.appInfo.installDate = { Date(timeIntervalSince1970: 1700462054) }

			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0.storeReview.shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithoutLastReview_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0.appInfo.numberOfSessions = { 5 }

			// Nov 12, 2023
			$0.appInfo.installDate = { Date(timeIntervalSince1970: 1699770852) }

			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { key in
				switch key {
				// Nov 19, 2023
				case "appLastReviewRequestDate": return 1700462054
				default: return nil
				}
			}

			$0.storeReview.shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithAllConditionsMet_IsTrue() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0.appInfo.numberOfSessions = { 5 }

			// Nov 12, 2023
			$0.appInfo.installDate = { Date(timeIntervalSince1970: 1699770852) }

			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0.storeReview.shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertTrue(storeReview.shouldRequestReview())
		}
	}

	func testDidRequestReview_UpdatesLastReviewRequestDate() async {
		let lastReviewRequestDate = LockIsolated(123.0)

		await withDependencies {
			$0.date.now = Date(timeIntervalSince1970: 456.0)
			$0.preferences.contains = { _ in true }
			$0.preferences.getDouble = { _ in lastReviewRequestDate.value }
			$0.preferences.setDouble = { _, newValue in lastReviewRequestDate.setValue(newValue) }
			$0.storeReview.didRequestReview = StoreReviewService.liveValue.didRequestReview
		} operation: {
			await storeReview.didRequestReview()
			XCTAssertEqual(lastReviewRequestDate.value, 456.0)
		}
	}
}
