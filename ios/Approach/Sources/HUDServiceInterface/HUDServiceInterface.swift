import Dependencies
import DependenciesMacros

@DependencyClient
public struct HUDService: Sendable {
	public var hudStatusNotifications: @Sendable () -> AsyncStream<HUDStatus> = {
		unimplemented("\(Self.self).hudStatusNotifications", placeholder: .finished)
	}

	public var requestHUD: @Sendable (AnyHashableSendable, _ style: HUDStyle) async -> Void
	public var dismissHUD: @Sendable (AnyHashableSendable) async -> Void
}

extension HUDService {
	public func requestHUD<T: Hashable & Sendable>(_ id: T, style: HUDStyle) async {
		await self.requestHUD(AnyHashableSendable(id), style: style)
	}

	public func dismissHUD<T: Hashable & Sendable>(_ id: T) async {
		await self.dismissHUD(AnyHashableSendable(id))
	}
}

extension HUDService: TestDependencyKey {
	public static var testValue: Self { Self() }
}
