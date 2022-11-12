import Dependencies
import SharedModelsLibrary

public struct FramesDataProvider: Sendable {
	public var fetchFrames: @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>

	public init(
		fetchFrames: @escaping @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>
	) {
		self.fetchFrames = fetchFrames
	}
}

extension FramesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchFrames: { _ in fatalError("\(Self.self).fetchFrames") }
	)
}

extension DependencyValues {
	public var framesDataProvider: FramesDataProvider {
		get { self[FramesDataProvider.self] }
		set { self[FramesDataProvider.self] = newValue }
	}
}
