import AppInfoServiceInterface
import Dependencies
import PreferenceServiceInterface
@testable import StoreReviewService
@testable import StoreReviewServiceInterface
import XCTest

final class StoreReviewServiceTests: XCTestCase {
	@Dependency(StoreReviewService.self) var storeReview

	func testShouldRequestReview_WithoutSessions_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0[AppInfoService.self].numberOfSessions = { @Sendable in 1 }

			// Nov 12, 2023
			$0[AppInfoService.self].installDate = { @Sendable in Date(timeIntervalSince1970: 1699770852) }

			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0[PreferenceService.self].getString = { @Sendable key in
				switch key {
				case "appLastReviewVersion": return "1.2.3"
				default: return nil
				}
			}
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.4" }

			$0[StoreReviewService.self].shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithoutInstallDate_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0[AppInfoService.self].numberOfSessions = { @Sendable in 5 }

			// Nov 19, 2023
			$0[AppInfoService.self].installDate = { @Sendable in Date(timeIntervalSince1970: 1700462054) }

			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0[PreferenceService.self].getString = { @Sendable key in
				switch key {
				case "appLastReviewVersion": return "1.2.3"
				default: return nil
				}
			}
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.4" }

			$0[StoreReviewService.self].shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithoutLastReview_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0[AppInfoService.self].numberOfSessions = { @Sendable in 5 }

			// Nov 12, 2023
			$0[AppInfoService.self].installDate = { @Sendable in Date(timeIntervalSince1970: 1699770852) }

			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0[PreferenceService.self].getString = { @Sendable key in
				switch key {
				case "appLastReviewVersion": return "1.2.3"
				default: return nil
				}
			}
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.4" }

			$0[StoreReviewService.self].shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertTrue(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithoutReviewVersion_IsFalse() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0[AppInfoService.self].numberOfSessions = { @Sendable in 1 }

			// Nov 12, 2023
			$0[AppInfoService.self].installDate = { @Sendable in Date(timeIntervalSince1970: 1699770852) }

			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0[PreferenceService.self].getString = { @Sendable key in
				switch key {
				case "appLastReviewVersion": return "1.2.3"
				default: return nil
				}
			}
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.3" }

			$0[StoreReviewService.self].shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertFalse(storeReview.shouldRequestReview())
		}
	}

	func testShouldRequestReview_WithAllConditionsMet_IsTrue() {
		withDependencies {
			// Nov 19, 2023
			$0.calendar = .current
			$0.date.now = Date(timeIntervalSince1970: 1700462054)

			$0[AppInfoService.self].numberOfSessions = { @Sendable in 5 }

			// Nov 12, 2023
			$0[AppInfoService.self].installDate = { @Sendable in Date(timeIntervalSince1970: 1699770852) }

			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable key in
				switch key {
				// Nov 12, 2023
				case "appLastReviewRequestDate": return 1699770852
				default: return nil
				}
			}

			$0[PreferenceService.self].getString = { @Sendable key in
				switch key {
				case "appLastReviewVersion": return "1.2.3"
				default: return nil
				}
			}
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.4" }

			$0[StoreReviewService.self].shouldRequestReview = StoreReviewService.liveValue.shouldRequestReview
		} operation: {
			XCTAssertTrue(storeReview.shouldRequestReview())
		}
	}

	func testDidRequestReview_UpdatesLastReviewRequestDateAndVersion() async {
		let lastReviewRequestDate = LockIsolated(123.0)
		let lastReviewVersion = LockIsolated("1.2.3")

		await withDependencies {
			$0.date.now = Date(timeIntervalSince1970: 456.0)
			$0[AppInfoService.self].appVersion = { @Sendable in "1.2.4" }
			$0[PreferenceService.self].contains = { @Sendable _ in true }
			$0[PreferenceService.self].getDouble = { @Sendable _ in lastReviewRequestDate.value }
			$0[PreferenceService.self].setDouble = { @Sendable _, newValue in lastReviewRequestDate.setValue(newValue) }
			$0[PreferenceService.self].getString = { @Sendable _ in lastReviewVersion.value }
			$0[PreferenceService.self].setString = { @Sendable _, newValue in lastReviewVersion.setValue(newValue) }
			$0[StoreReviewService.self].didRequestReview = StoreReviewService.liveValue.didRequestReview
		} operation: {
			await storeReview.didRequestReview()
			XCTAssertEqual(lastReviewRequestDate.value, 456.0)
			XCTAssertEqual(lastReviewVersion.value, "1.2.4")
		}
	}
}
