import Dependencies
import SharedModelsLibrary

public struct LeaguesDataProvider {
	public var create: @Sendable (League) async throws -> Void
	public var delete: @Sendable (League) async throws -> Void
	public var fetchAll: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public init(
		create: @escaping @Sendable (League) async throws -> Void,
		delete: @escaping @Sendable (League) async throws -> Void,
		fetchAll: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>
	) {
		self.create = create
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension LeaguesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var leaguesDataProvider: LeaguesDataProvider {
		get { self[LeaguesDataProvider.self] }
		set { self[LeaguesDataProvider.self] = newValue }
	}
}
