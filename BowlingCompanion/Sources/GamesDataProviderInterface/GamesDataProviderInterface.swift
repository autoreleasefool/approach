import Dependencies
import SharedModelsLibrary

public struct GamesDataProvider: Sendable {
	public var create: @Sendable (Series, Game) async throws -> Void
	public var delete: @Sendable (Game) async throws -> Void
	public var fetchAll: @Sendable (Series) -> AsyncStream<[Game]>

	public init(
		create: @escaping @Sendable (Series, Game) async throws -> Void,
		delete: @escaping @Sendable (Game) async throws -> Void,
		fetchAll: @escaping @Sendable (Series) -> AsyncStream<[Game]>
	) {
		self.create = create
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension GamesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var gamesDataProvider: GamesDataProvider {
		get { self[GamesDataProvider.self] }
		set { self[GamesDataProvider.self] = newValue }
	}
}
