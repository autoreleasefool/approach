import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct TeamsDataProvider: Sendable {
	public var observeTeams: @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>

	public init(
		observeTeams: @escaping @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>
	) {
		self.observeTeams = observeTeams
	}
}

extension TeamsDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeTeams: { _ in unimplemented("\(Self.self).observeTeams") }
	)
}

extension DependencyValues {
	public var teamsDataProvider: TeamsDataProvider {
		get { self[TeamsDataProvider.self] }
		set { self[TeamsDataProvider.self] = newValue }
	}
}
