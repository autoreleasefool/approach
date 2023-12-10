import Dependencies

public struct LaunchService: Sendable {
	public var didLaunch: @Sendable () async -> Void

	public init(didLaunch: @escaping @Sendable () async -> Void) {
		self.didLaunch = didLaunch
	}
}

extension LaunchService: TestDependencyKey {
	public static var testValue = Self(
		didLaunch: { unimplemented("\(Self.self).didLaunch") }
	)
}

extension DependencyValues {
	public var launch: LaunchService {
		get { self[LaunchService.self] }
		set { self[LaunchService.self] = newValue }
	}
}
