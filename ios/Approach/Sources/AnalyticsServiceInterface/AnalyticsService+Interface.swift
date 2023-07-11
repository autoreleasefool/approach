import Dependencies
import Foundation

public struct AnalyticsService: Sendable {
	public var initialize: @Sendable () -> Void
	public var setGlobalProperty: @Sendable (String?, String) async -> Void
	public var trackEvent: @Sendable (TrackableEvent) async -> Void

	public init(
		initialize: @escaping @Sendable () -> Void,
		setGlobalProperty: @escaping @Sendable (String?, String) async -> Void,
		trackEvent: @escaping @Sendable (TrackableEvent) async -> Void
	) {
		self.initialize = initialize
		self.setGlobalProperty = setGlobalProperty
		self.trackEvent = trackEvent
	}

	public func setGlobalProperty(value: String?, forKey: String) async {
		await self.setGlobalProperty(value, forKey)
	}
}

extension AnalyticsService: TestDependencyKey {
	public static var testValue = Self(
		initialize: { unimplemented("\(Self.self).initialize") },
		setGlobalProperty: { _, _ in unimplemented("\(Self.self).setGlobalProperty") },
		trackEvent: { _ in unimplemented("\(Self.self).trackEvent") }
	)
}

extension DependencyValues {
	public var analytics: AnalyticsService {
		get { self[AnalyticsService.self] }
		set { self[AnalyticsService.self] = newValue }
	}
}

public enum AnalyticsGameSessionID: DependencyKey {
	public static var liveValue = {
		@Dependency(\.uuid) var uuid
		return uuid()
	}()

	public static var testValue = {
		@Dependency(\.uuid) var uuid
		return uuid()
	}()
}

extension DependencyValues {
	public var analyticsGameSessionId: UUID {
		get { self[AnalyticsGameSessionID.self]  }
		set { self[AnalyticsGameSessionID.self] = newValue }
	}
}
