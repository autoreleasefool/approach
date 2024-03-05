import AssetsLibrary
import Dependencies

public struct AppIconService: Sendable {
	public var setAppIcon: @Sendable (AppIcon) async throws -> Void
	public var resetAppIcon: @Sendable () async throws -> Void
	public var getAppIconName: @Sendable () async -> String?
	public var supportsAlternateIcons: @Sendable () async -> Bool

	public init(
		setAppIcon: @escaping @Sendable (AppIcon) async throws -> Void,
		resetAppIcon: @escaping @Sendable () async throws -> Void,
		getAppIconName: @escaping @Sendable () async -> String?,
		supportsAlternateIcons: @escaping @Sendable () async -> Bool
	) {
		self.setAppIcon = setAppIcon
		self.resetAppIcon = resetAppIcon
		self.getAppIconName = getAppIconName
		self.supportsAlternateIcons = supportsAlternateIcons
	}
}

extension AppIconService: TestDependencyKey {
	public static var testValue = Self(
		setAppIcon: { _ in unimplemented("\(Self.self).setAppIcon") },
		resetAppIcon: { unimplemented("\(Self.self).resetAppIcon") },
		getAppIconName: { unimplemented("\(Self.self).getAppIconName") },
		supportsAlternateIcons: { unimplemented("\(Self.self).supportsAlternateIcons") }
	)
}
