import AnalyticsServiceInterface
import Dependencies
import SwiftUI

@main
struct BowlingCompanionApp: App {
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}

	init() {
		@Dependency(\.analytics) var analytics: AnalyticsService
		analytics.initialize()
		analytics.trackEvent(AppLaunchEvent())
	}
}
