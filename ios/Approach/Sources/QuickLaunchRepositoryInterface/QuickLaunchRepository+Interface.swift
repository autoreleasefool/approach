import Dependencies
import ModelsLibrary

public struct QuickLaunchRepository: Sendable {
	public var defaultSource: @Sendable () async throws -> QuickLaunchSource?

	public init(
		defaultSource: @escaping @Sendable () async throws -> QuickLaunchSource?
	) {
		self.defaultSource = defaultSource
	}
}

extension QuickLaunchRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			defaultSource: { unimplemented("\(Self.self).defaultSource", placeholder: nil) }
		)
	}
}
