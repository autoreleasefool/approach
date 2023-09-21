import Dependencies
import Foundation

public struct AnalyticsService: Sendable {
	public var initialize: @Sendable () -> Void
	public var setGlobalProperty: @Sendable (String?, String) async -> Void
	public var trackEvent: @Sendable (TrackableEvent) async -> Void
	public var resetGameSessionID: @Sendable () async -> Void
	public var getOptInStatus: @Sendable () -> Analytics.OptInStatus
	public var setOptInStatus: @Sendable (Analytics.OptInStatus) async -> Analytics.OptInStatus

	public init(
		initialize: @escaping @Sendable () -> Void,
		setGlobalProperty: @escaping @Sendable (String?, String) async -> Void,
		trackEvent: @escaping @Sendable (TrackableEvent) async -> Void,
		resetGameSessionID: @escaping @Sendable () async -> Void,
		getOptInStatus: @escaping @Sendable () -> Analytics.OptInStatus,
		setOptInStatus: @escaping @Sendable (Analytics.OptInStatus) async -> Analytics.OptInStatus
	) {
		self.initialize = initialize
		self.setGlobalProperty = setGlobalProperty
		self.trackEvent = trackEvent
		self.resetGameSessionID = resetGameSessionID
		self.getOptInStatus = getOptInStatus
		self.setOptInStatus = setOptInStatus
	}

	public func setGlobalProperty(value: String?, forKey: String) async {
		await self.setGlobalProperty(value, forKey)
	}
}

extension AnalyticsService: TestDependencyKey {
	public static var testValue = Self(
		initialize: { unimplemented("\(Self.self).initialize") },
		setGlobalProperty: { _, _ in unimplemented("\(Self.self).setGlobalProperty") },
		trackEvent: { _ in unimplemented("\(Self.self).trackEvent") },
		resetGameSessionID: { unimplemented("\(Self.self).resetGameSessionID") },
		getOptInStatus: { unimplemented("\(Self.self).getOptInStatus") },
		setOptInStatus: { _ in unimplemented("\(Self.self).setOptInStatus") }
	)
}

extension DependencyValues {
	public var analytics: AnalyticsService {
		get { self[AnalyticsService.self] }
		set { self[AnalyticsService.self] = newValue }
	}
}
