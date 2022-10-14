import Dependencies
import SharedModelsLibrary

public struct LeaguesDataProvider {
	public var save: @Sendable (Bowler, League) async throws -> Void
	public var delete: @Sendable (League) async throws -> Void
	public var fetchAll: @Sendable (Bowler) -> AsyncStream<[League]>

	public init(
		save: @escaping @Sendable (Bowler, League) async throws -> Void,
		delete: @escaping @Sendable (League) async throws -> Void,
		fetchAll: @escaping @Sendable (Bowler) -> AsyncStream<[League]>
	) {
		self.save = save
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension LeaguesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		save: { _, _ in fatalError("\(Self.self).save") },
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
