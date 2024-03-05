import AnalyticsServiceInterface
import AppInfoServiceInterface
import Dependencies
import FeatureFlagsServiceInterface
import LaunchServiceInterface
import ProductsServiceInterface

extension LaunchService: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func initializeAnalytics() {
			@Dependency(AnalyticsService.self) var analytics
			analytics.initialize()

			Task.detached(priority: .utility) {
				await analytics.trackEvent(Analytics.App.Launched())
			}
		}

		@Sendable func initializeProducts() {
			@Dependency(ProductsService.self) var products
			products.initialize()
		}

		@Sendable func initializeAppInfo() async {
			@Dependency(AppInfoService.self) var appInfo
			await appInfo.recordInstallDate()
			await appInfo.recordNewSession()
		}

		return Self(
			didInit: {
				// For sync initializars that must run before anything else in the app
				initializeAnalytics()
			},
			didLaunch: {
				// For async initializers that can wait until task
				@Dependency(FeatureFlagsService.self) var features
				let isProductsEnabled = features.isEnabled(.purchases)
				if isProductsEnabled {
					initializeProducts()
				}

				await initializeAppInfo()
			}
		)
	}()
}
