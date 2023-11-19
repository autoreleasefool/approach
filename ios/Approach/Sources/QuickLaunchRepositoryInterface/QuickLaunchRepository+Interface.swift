import Dependencies
import ModelsLibrary

public struct QuickLaunchRepository: Sendable {
	public var defaultSource: @Sendable () -> AsyncThrowingStream<QuickLaunchSource?, Error>

	public init(
		defaultSource: @escaping @Sendable () -> AsyncThrowingStream<QuickLaunchSource?, Error>
	) {
		self.defaultSource = defaultSource
	}
}

extension QuickLaunchRepository: TestDependencyKey {
	public static var testValue = Self(
		defaultSource: { unimplemented("\(Self.self).defaultSource") }
	)
}

extension DependencyValues {
	public var quickLaunch: QuickLaunchRepository {
		get { self[QuickLaunchRepository.self] }
		set { self[QuickLaunchRepository.self] = newValue }
	}
}
