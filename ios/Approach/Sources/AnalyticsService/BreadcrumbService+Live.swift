import AnalyticsServiceInterface
import Dependencies
import Sentry

extension BreadcrumbService: DependencyKey {
	public static var liveValue: Self {
		Self(
			drop: { breadcrumb in
				let crumb = Sentry.Breadcrumb(level: .info, category: breadcrumb.category.rawValue)
				crumb.message = breadcrumb.message
				SentrySDK.addBreadcrumb(crumb)
			}
		)
	}
}
