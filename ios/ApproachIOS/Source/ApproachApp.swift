import ConstantsLibrary
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
			options.dsn = AppConstants.ApiKey.sentry
		}

		@Dependency(\.launch) var launch
		launch.didInit()
		Task.detached(priority: .high) {
			await launch.didLaunch()
		}
	}
}
