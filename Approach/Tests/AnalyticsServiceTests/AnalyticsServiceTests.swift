import TelemetryClient
import XCTest
@testable import AnalyticsService
@testable import AnalyticsServiceInterface

@MainActor
final class AnalyticsServiceTests: XCTestCase {
	func testInitializes() {
		let analytics: AnalyticsService = .liveValue
		analytics.initialize()

		XCTAssertTrue(TelemetryManager.isInitialized)
	}
}
