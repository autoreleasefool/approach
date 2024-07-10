import BundlePackageServiceInterface
import ConstantsLibrary
import DatabaseServiceInterface
import Dependencies
import LaunchServiceInterface
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
			@Dependency(\.bundle) var bundle
			let sentryKey = bundle.object(forInfoDictionaryKey: "SENTRY_DSN") as? String
			options.dsn = sentryKey
		}

		@Dependency(LaunchService.self) var launch
		launch.didInit()
		Task.detached(priority: .high) {
			await launch.didLaunch()
		}

		@Dependency(\.database) var database
		database.initialize()
	}
}
