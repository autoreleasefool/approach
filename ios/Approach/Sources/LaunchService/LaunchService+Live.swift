import AnalyticsServiceInterface
import AppInfoPackageServiceInterface
import Dependencies
import FeatureFlagsServiceInterface
import LaunchServiceInterface
import ProductsServiceInterface
import StoreReviewPackageServiceInterface

extension LaunchService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			didInit: {
				// For sync initializars that must run before anything else in the app
				@Dependency(AnalyticsService.self) var analytics
				analytics.initialize()

				Task.detached(priority: .utility) {
					await analytics.trackEvent(Analytics.App.Launched())
				}
			},
			didLaunch: {
				// For async initializers that can wait until task
				@Dependency(FeatureFlagsService.self) var features
				let isProductsEnabled = features.isEnabled(.purchases)
				if isProductsEnabled {
					@Dependency(ProductsService.self) var products
					products.initialize()
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
	}()
}
