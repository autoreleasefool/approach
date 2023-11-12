import AnalyticsServiceInterface
import ConstantsLibrary
import Dependencies
import ProductsServiceInterface
import Sentry
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
		SentrySDK.start { options in
			options.dsn = AppConstants.ApiKey.sentry
		}

		@Dependency(\.products) var products
		products.initialize()

		@Dependency(\.analytics) var analytics
		analytics.initialize()
		Task.detached {
			await analytics.trackEvent(Analytics.App.Launched())
		}
	}
}
