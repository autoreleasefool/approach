import Dependencies
import SharedModelsLibrary

public struct LeaguesDataProvider: Sendable {
	public var fetchLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public init(
		fetchLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>
	) {
		self.fetchLeagues = fetchLeagues
	}
}

extension LeaguesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchLeagues: { _ in fatalError("\(Self.self).fetchLeagues") }
	)
}

extension DependencyValues {
	public var leaguesDataProvider: LeaguesDataProvider {
		get { self[LeaguesDataProvider.self] }
		set { self[LeaguesDataProvider.self] = newValue }
	}
}
