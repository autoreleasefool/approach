import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct LeaguesDataProvider: Sendable {
	public var observeLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public init(
		observeLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>
	) {
		self.observeLeagues = observeLeagues
	}
}

extension LeaguesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeLeagues: { _ in unimplemented("\(Self.self).observeLeagues") }
	)
}

extension DependencyValues {
	public var leaguesDataProvider: LeaguesDataProvider {
		get { self[LeaguesDataProvider.self] }
		set { self[LeaguesDataProvider.self] = newValue }
	}
}
