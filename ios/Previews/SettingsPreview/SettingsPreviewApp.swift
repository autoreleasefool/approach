import AppInfoPackageServiceInterface
import AppPreviewFeature
import ComposableArchitecture
import FeatureFlagsPackageServiceInterface
import PasteboardPackageServiceInterface
import SettingsFeature
import SwiftUI
import UserDefaultsPackageServiceInterface

@main
public struct SettingsPreviewApp: App {
	let store: Store = {
		return Store(
			initialState: SettingsList.State(),
			reducer: {
				SettingsList()
					._printChanges()
			}, withDependencies: {
				$0.analytics = .mock
				$0.breadcrumbs = .mock
				$0.database = .defaults
				$0.errors = .mock
				$0.userDefaults = .mock
				$0.featureFlags = .allEnabled
				$0.pasteboard.copyToClipboard = { _ in }

				$0.appInfo = AppInfoService(
					initialize: {},
					getNumberOfSessions: { 1 },
					getInstallDate: { .now },
					getAppVersion: { "1.2.3" },
					getBuildVersion: { "27" },
					getFullAppVersion: { "1.2.3b27" }
				)
			}
		)
	}()

	public init() {}

	public var body: some Scene {
		WindowGroup {
			SettingsListView(store: store)
		}
	}
}
