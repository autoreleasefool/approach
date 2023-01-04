import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct TeamsDataProvider: Sendable {
	public var fetchTeams: @Sendable (Team.FetchRequest) async throws -> [Team]
	public var observeTeams: @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>
	public var fetchTeamMembers: @Sendable (TeamMembership.FetchRequest) async throws -> TeamMembership
	public var observeTeamMembers: @Sendable (TeamMembership.FetchRequest) -> AsyncThrowingStream<TeamMembership, Error>

	public init(
		fetchTeams: @escaping @Sendable (Team.FetchRequest) async throws -> [Team],
		observeTeams: @escaping @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>,
		fetchTeamMembers: @escaping @Sendable (TeamMembership.FetchRequest) async throws -> TeamMembership,
		observeTeamMembers: @escaping @Sendable (TeamMembership.FetchRequest) -> AsyncThrowingStream<TeamMembership, Error>
	) {
		self.fetchTeams = fetchTeams
		self.observeTeams = observeTeams
		self.fetchTeamMembers = fetchTeamMembers
		self.observeTeamMembers = observeTeamMembers
	}
}

extension TeamsDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchTeams: { _ in fatalError("\(Self.self).fetchTeams") },
		observeTeams: { _ in fatalError("\(Self.self).observeTeams") },
		fetchTeamMembers: { _ in fatalError("\(Self.self).fetchTeamMembers") },
		observeTeamMembers: { _ in fatalError("\(Self.self).observeTeamMembers") }
	)
}

extension DependencyValues {
	public var teamsDataProvider: TeamsDataProvider {
		get { self[TeamsDataProvider.self] }
		set { self[TeamsDataProvider.self] = newValue }
	}
}
