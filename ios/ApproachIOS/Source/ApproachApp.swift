import AnalyticsServiceInterface
import Dependencies
import SwiftUI
import XCTestDynamicOverlay

@main
struct ApproachApp: App {
	var body: some Scene {
		WindowGroup {
			if !_XCTIsTesting {
				ContentView()
			}
		}
	}

	init() {
		@Dependency(\.analytics) var analytics: AnalyticsService
		analytics.initialize()
		analytics.trackEvent(AppLaunchEvent())
	}
}
