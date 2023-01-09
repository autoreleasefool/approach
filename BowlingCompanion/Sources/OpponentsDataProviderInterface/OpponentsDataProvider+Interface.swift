import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct OpponentsDataProvider: Sendable {
	public var fetchOpponents: @Sendable (Opponent.FetchRequest) async throws -> [Opponent]
	public var observeOpponents: @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>

	public init(
		fetchOpponents: @escaping @Sendable (Opponent.FetchRequest) async throws -> [Opponent],
		observeOpponents: @escaping @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>
	) {
		self.fetchOpponents = fetchOpponents
		self.observeOpponents = observeOpponents
	}
}

extension OpponentsDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchOpponents: { _ in fatalError("\(Self.self).fetchOpponents") },
		observeOpponents: { _ in fatalError("\(Self.self).observeOpponents") }
	)
}

extension DependencyValues {
	public var opponentsDataProvider: OpponentsDataProvider {
		get { self[OpponentsDataProvider.self] }
		set { self[OpponentsDataProvider.self] = newValue }
	}
}
