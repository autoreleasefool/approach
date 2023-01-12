import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct TeamsDataProvider: Sendable {
	public var fetchTeams: @Sendable (Team.FetchRequest) async throws -> [Team]
	public var observeTeams: @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>

	public init(
		fetchTeams: @escaping @Sendable (Team.FetchRequest) async throws -> [Team],
		observeTeams: @escaping @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>
	) {
		self.fetchTeams = fetchTeams
		self.observeTeams = observeTeams
	}
}

extension TeamsDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchTeams: { _ in fatalError("\(Self.self).fetchTeams") },
		observeTeams: { _ in fatalError("\(Self.self).observeTeams") }
	)
}

extension DependencyValues {
	public var teamsDataProvider: TeamsDataProvider {
		get { self[TeamsDataProvider.self] }
		set { self[TeamsDataProvider.self] = newValue }
	}
}
