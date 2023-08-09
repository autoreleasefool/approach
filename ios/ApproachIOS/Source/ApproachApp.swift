import AnalyticsServiceInterface
import Dependencies
import ProductsServiceInterface
import SwiftUI
import XCTestDynamicOverlay

@main
public struct ApproachApp: App {
	public var body: some Scene {
		WindowGroup {
			if !_XCTIsTesting {
				ContentView()
			}
		}
	}

	public init() {
		@Dependency(\.products) var products
		products.initialize()

		@Dependency(\.analytics) var analytics
		analytics.initialize()
		Task.detached {
			await analytics.trackEvent(Analytics.App.Launched())
		}
	}
}
