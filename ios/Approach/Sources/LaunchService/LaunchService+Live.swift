import AnalyticsServiceInterface
import AppInfoServiceInterface
import Dependencies
import FeatureFlagsServiceInterface
import LaunchServiceInterface
import ProductsServiceInterface

extension LaunchService: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func initializeAnalytics() {
			@Dependency(\.analytics) var analytics
			analytics.initialize()

			Task.detached(priority: .utility) {
				await analytics.trackEvent(Analytics.App.Launched())
			}
		}

		@Sendable func initializeProducts() {
			@Dependency(\.products) var products
			products.initialize()
		}

		@Sendable func initializeAppInfo() async {
			@Dependency(\.appInfo) var appInfo
			await appInfo.recordInstallDate()
			await appInfo.recordNewSession()
		}

		return Self(
			didLaunch: {
				initializeAnalytics()

				@Dependency(\.featureFlags) var features
				let isProductsEnabled = features.isEnabled(.purchases)
				if isProductsEnabled {
					initializeProducts()
				}

				await initializeAppInfo()
			}
		)
	}()
}
