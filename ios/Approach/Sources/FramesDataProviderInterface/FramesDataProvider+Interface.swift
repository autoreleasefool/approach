import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct FramesDataProvider: Sendable {
	public var fetchFrames: @Sendable (Frame.FetchRequest) async throws -> [Frame]

	public init(
		fetchFrames: @escaping @Sendable (Frame.FetchRequest) async throws -> [Frame]
	) {
		self.fetchFrames = fetchFrames
	}
}

extension FramesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchFrames: { _ in unimplemented("\(Self.self).fetchFrames") }
	)
}

extension DependencyValues {
	public var framesDataProvider: FramesDataProvider {
		get { self[FramesDataProvider.self] }
		set { self[FramesDataProvider.self] = newValue }
	}
}
