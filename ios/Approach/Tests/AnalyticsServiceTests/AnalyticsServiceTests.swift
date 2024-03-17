@testable import AnalyticsService
@testable import AnalyticsServiceInterface
import TelemetryClient
import XCTest

final class AnalyticsServiceTests: XCTestCase {
	func testInitializes() {
		let analytics: AnalyticsService = .liveValue
		analytics.initialize()

		XCTAssertTrue(TelemetryManager.isInitialized)
	}
}
