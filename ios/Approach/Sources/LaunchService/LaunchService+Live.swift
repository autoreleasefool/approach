import AnalyticsServiceInterface
import AppInfoPackageServiceInterface
import Dependencies
import FeatureFlagsLibrary
import LaunchServiceInterface
import PreferenceServiceInterface
import ProductsServiceInterface
import StoreReviewPackageServiceInterface
import UserDefaultsPackageServiceInterface

extension LaunchService: DependencyKey {
	public static var liveValue: Self {
		Self(
			didInit: {
				// For sync initializars that must run before anything else in the app
				@Dependency(\.analytics) var analytics
				try? analytics.initialize()

				Task.detached(priority: .utility) {
					try await analytics.trackEvent(Analytics.App.Launched())
				}
			},
			didLaunch: {
				// For async initializers that can wait until task
				@Dependency(\.featureFlags) var featureFlags
				featureFlags.initialize(registeringFeatureFlags: FeatureFlag.allFlags)

				let isProductsEnabled = featureFlags.isFlagEnabled(.purchases)
				if isProductsEnabled {
					@Dependency(ProductsService.self) var products
					products.initialize()
				}

				@Dependency(\.preferences) var preferences
				@Dependency(\.userDefaults) var userDefaults

				let didMigrateToSwiftUtilities = preferences.bool(forKey: .appDidMigrateToSwiftUtilities) ?? false
				if !didMigrateToSwiftUtilities {
					// Migrate from deprecated PreferenceService values
					if let deprecatedAppSessions = userDefaults.int(forKey: "appSessions") {
						userDefaults.setInt(forKey: "AppInfo.NumberOfSessions", to: deprecatedAppSessions)
					}

					if let deprecatedAppInstallDate = userDefaults.double(forKey: "appInstallDate") {
						userDefaults.setDouble(forKey: "AppInfo.InstallDate", to: deprecatedAppInstallDate)
					}

					preferences.setBool(forKey: .appDidMigrateToSwiftUtilities, to: true)
				}

				@Dependency(\.appInfo) var appInfo
				await appInfo.initialize()

				@Dependency(\.storeReview) var storeReview
				storeReview.initialize(
					numberOfSessions: 3,
					minimumDaysSinceInstall: 7,
					minimumDaysSinceLastRequest: 7,
					canReviewVersionMultipleTimes: false
				)
			}
		)
	}
}
