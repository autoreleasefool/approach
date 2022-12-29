import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct LeaguesDataProvider: Sendable {
	public var fetchLeagues: @Sendable (League.FetchRequest) async throws -> [League]
	public var observeLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public init(
		fetchLeagues: @escaping @Sendable (League.FetchRequest) async throws -> [League],
		observeLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>
	) {
		self.fetchLeagues = fetchLeagues
		self.observeLeagues = observeLeagues
	}
}

extension LeaguesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchLeagues: { _ in fatalError("\(Self.self).fetchLeagues") },
		observeLeagues: { _ in fatalError("\(Self.self).observeLeagues") }
	)
}

extension DependencyValues {
	public var leaguesDataProvider: LeaguesDataProvider {
		get { self[LeaguesDataProvider.self] }
		set { self[LeaguesDataProvider.self] = newValue }
	}
}
