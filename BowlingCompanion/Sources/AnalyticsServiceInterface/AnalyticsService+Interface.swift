import Dependencies

public struct AnalyticsService: Sendable {
	public var initialize: @Sendable () -> Void
	public var trackEvent: @Sendable (AnalyticsEvent) -> Void

	public init(
		initialize: @escaping @Sendable () -> Void,
		trackEvent: @escaping @Sendable (AnalyticsEvent) -> Void
	) {
		self.initialize = initialize
		self.trackEvent = trackEvent
	}
}

extension AnalyticsService: TestDependencyKey {
	public static var testValue = Self(
		initialize: { fatalError("\(Self.self).initialize") },
		trackEvent: { _ in fatalError("\(Self.self).trackEvent") }
	)
}

extension DependencyValues {
	public var analytics: AnalyticsService {
		get { self[AnalyticsService.self] }
		set { self[AnalyticsService.self] = newValue }
	}
}
