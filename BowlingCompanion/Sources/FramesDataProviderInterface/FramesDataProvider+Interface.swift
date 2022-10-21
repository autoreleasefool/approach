import Dependencies
import SharedModelsLibrary

public struct FramesDataProvider: Sendable {
	public var create: @Sendable (Frame) async throws -> Void
	public var delete: @Sendable (Frame) async throws -> Void
	public var fetchAll: @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>

	public init(
		create: @escaping @Sendable (Frame) async throws -> Void,
		delete: @escaping @Sendable (Frame) async throws -> Void,
		fetchAll: @escaping @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>
	) {
		self.create = create
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension FramesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var framesDataProvider: FramesDataProvider {
		get { self[FramesDataProvider.self] }
		set { self[FramesDataProvider.self] = newValue }
	}
}
