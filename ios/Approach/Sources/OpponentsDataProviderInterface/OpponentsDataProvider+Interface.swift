import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct OpponentsDataProvider: Sendable {
	public var observeOpponents: @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>

	public init(
		observeOpponents: @escaping @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>
	) {
		self.observeOpponents = observeOpponents
	}
}

extension OpponentsDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeOpponents: { _ in unimplemented("\(Self.self).observeOpponents") }
	)
}

extension DependencyValues {
	public var opponentsDataProvider: OpponentsDataProvider {
		get { self[OpponentsDataProvider.self] }
		set { self[OpponentsDataProvider.self] = newValue }
	}
}
